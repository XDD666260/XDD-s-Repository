package com.example.littlepainter.ui.fragment.password.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.littlepainter.R
import com.example.littlepainter.databinding.FragmentPicLoginBinding
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.ui.fragment.home.file.FileManager
import com.example.littlepainter.utils.LoginRegisterResult
import com.example.littlepainter.utils.PasswordType
import com.example.littlepainter.utils.WrongType
import com.example.littlepainter.utils.delayTask
import com.example.littlepainter.viewmodel.UserViewModel

class PicLoginFragment: BaseFragment<FragmentPicLoginBinding>() {
    private val viewModel:UserViewModel by activityViewModels()
    private val args:PicLoginFragmentArgs by navArgs()
    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)
        initEvent()
    }
    private fun initEvent(){
        mBinding.usernameView.addTextChangeListener {
            mBinding.loginButton.isEnabled=mBinding.usernameView.text().isNotEmpty()&&
                    mBinding.passwordView.text().isNotEmpty()
        }
        mBinding.passwordView.addTextChangeListener {
            mBinding.loginButton.isEnabled=mBinding.usernameView.text().isNotEmpty()&&
                    mBinding.passwordView.text().isNotEmpty()
        }
        mBinding.loginButton.setOnClickListener {
            viewModel.login(mBinding.usernameView.text(),mBinding.passwordView.text(),PasswordType.PIC)
        }
        viewModel.loginRegisterResult.observe(viewLifecycleOwner){
            when(it){
                is LoginRegisterResult.Success->{
                    mBinding.alertView.showMessage("登陆成功")
                    FileManager.instance.login(mBinding.usernameView.text().trim())
                    delayTask(200){
                        if (args.hasHome){
                            findNavController().navigateUp()
                        }else{
                            findNavController().navigate(R.id.action_picLoginFragment_to_homeFragment)
                        }
                    }
                }
                is LoginRegisterResult.Failure->{
                    when(it.type){
                        WrongType.USER_NOT_FOUND->{
                            mBinding.usernameView.showError()
                            mBinding.alertView.showErrorMessage("用户不存在")
                        }
                        WrongType.WRONG_PASSWORD-> {
                            mBinding.passwordView.showError()
                            mBinding.alertView.showErrorMessage("密码错误")
                        }
                        WrongType.USER_LOGINED->{
                            mBinding.alertView.showMessage("用户已登录 立刻跳转")
                            findNavController().navigate(R.id.action_picLoginFragment_to_homeFragment)
                        }
                        else->{}
                    }
                }
                else->{}
            }
        }
        if (args.hasHome){
            //从home界面跳转过来
            showBack()
            mBinding.ivBackBtn.setOnClickListener {
                findNavController().navigateUp()
            }
        }else{
            //从welcome跳转过来
            hideBack()
        }
        mBinding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_picLoginFragment_to_pinRegisterFragment)
        }
    }
    private fun showBack(){
        mBinding.ivBackBtn.visibility=View.VISIBLE
        mBinding.tvBack.visibility=View.VISIBLE
    }
    private fun hideBack(){
            mBinding.ivBackBtn.visibility=View.INVISIBLE
        mBinding.tvBack.visibility=View.INVISIBLE
    }
}