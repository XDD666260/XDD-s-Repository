package com.example.littlepainter.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.littlepainter.db.Repository
import com.example.littlepainter.db.User
import com.example.littlepainter.utils.FragmentType
import com.example.littlepainter.utils.LoginRegisterResult
import com.example.littlepainter.utils.PasswordType
import com.example.littlepainter.utils.WrongType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class UserViewModel(application: Application):AndroidViewModel(application) {
    private val repository=Repository(application)
    val users:LiveData<List<User>>
    private var _loginRegisterResult:MutableLiveData<LoginRegisterResult> =MutableLiveData(LoginRegisterResult.None())
    val loginRegisterResult:LiveData<LoginRegisterResult> = _loginRegisterResult
    private var _currentUser:User?=null
    val currentUser get() =_currentUser
    private var _isFindFinished:MutableLiveData<Boolean> =MutableLiveData(false)
    val isFindFinished:LiveData<Boolean> =_isFindFinished

init {
    users=repository.loadUsers()
    viewModelScope.launch(Dispatchers.IO) {
        repository.findLoginedUser().also {
            if (it.isNotEmpty()){
                withContext(Dispatchers.Main){
                    _currentUser=it[0]
                }
            }
            _isFindFinished.postValue(true)
        }
    }
}
    //退出登录
    fun logout(){
        if (_currentUser!=null){
            _currentUser!!.isLogin=false
            updateUser(_currentUser!!)
        }
    }
    //修改密码类型
    fun changePasswordType(type: PasswordType){
        if (_currentUser==null)return
        _currentUser!!.passwordType=if(type==PasswordType.PIN) 0 else 1
        updateUser(_currentUser!!)
    }
    //注册
    fun register(name:String,pinPassword:String,picPassword:String){
        if (name.isEmpty() || pinPassword.isEmpty() || picPassword.isEmpty()){
            _loginRegisterResult.value=LoginRegisterResult.Failure(WrongType.NAME_OR_PASSWORD_EMPTY)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(User(0,name,pinPassword,picPassword))
            _loginRegisterResult.postValue(LoginRegisterResult.Success())
        }
    }
    //登录
    fun login(name: String,password: String,type:PasswordType){
        if (name.isEmpty() || password.isEmpty()){
            _loginRegisterResult.value=LoginRegisterResult.Failure(WrongType.NAME_OR_PASSWORD_EMPTY)
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.findUser(name).also {
                withContext(Dispatchers.Main){
                    if (it.isEmpty()){
                        _loginRegisterResult.value=LoginRegisterResult.Failure(WrongType.USER_NOT_FOUND)
                    }else{
                        val user=it[0]
                        if (type==PasswordType.PIN && password==user.pinPassword){
                            changeLoginStatus(user)
                        }else if (type==PasswordType.PIC && password==user.picPassword){
                            changeLoginStatus(user)
                        }else{
                            _loginRegisterResult.value=LoginRegisterResult.Failure(WrongType.WRONG_PASSWORD)
                        }
                    }
                }
            }
        }
    }
    fun resetLoginState(){
        _loginRegisterResult.value=LoginRegisterResult.None()
    }
    private fun changeLoginStatus(user: User){
        if (_currentUser==null){
            //没有用户登录
            user.isLogin=true
            updateUser(user)
            _currentUser=user
            _loginRegisterResult.postValue(LoginRegisterResult.Success())
        }else{
            if (_currentUser!!.id != user.id){
                //两次用户登录不一致
                _currentUser!!.isLogin=false
                updateUser(_currentUser!!)
                user.isLogin=true
                user.loginDate=Date()
                updateUser(user)
                _currentUser=user
                _loginRegisterResult.postValue(LoginRegisterResult.Success())
            }else{
                user.loginDate=Date()
                updateUser(user)
                _loginRegisterResult.postValue(LoginRegisterResult.Success())
            }
        }
    }
    private fun updateUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }
}