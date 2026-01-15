package com.example.littlepainter.db

import android.content.Context
import androidx.lifecycle.LiveData

class Repository(context: Context) {
    private val dataBase=UserDataBase.getInstance(context)
    private val userDao=dataBase.userDao()

    suspend fun insertUser(user: User){
        userDao.insertUser(user)
    }
    fun loadUsers():LiveData<List<User>>{
        return userDao.loadUsers()
    }

    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }
    suspend fun findUser(name:String):List<User>{
        return userDao.findUser(name)
    }
    suspend fun findLoginedUser():List<User>{
        return userDao.findLoginedUser()
    }
}