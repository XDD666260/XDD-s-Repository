package com.example.littlepainter.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.littlepainter.utils.TypeConverter

@TypeConverters(TypeConverter::class)
@Database(entities =[User::class],version=1)
abstract class UserDataBase:RoomDatabase() {
    abstract fun userDao():UserDao

    companion object{
        private var INSTANCE:UserDataBase?=null
        fun getInstance(context: Context):UserDataBase{
            if (INSTANCE==null){
                synchronized(this){
                    INSTANCE=Room.databaseBuilder(
                        context.applicationContext,
                        UserDataBase::class.java,
                        "user_database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}