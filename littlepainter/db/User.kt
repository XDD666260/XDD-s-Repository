package com.example.littlepainter.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.littlepainter.utils.PasswordType
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val name:String,
    @ColumnInfo(name = "pin_password")
    val pinPassword:String,
    @ColumnInfo(name = "pic_password")
    val picPassword:String,
    var isLogin:Boolean=false,
    var loginDate:Date = Date(),
    val validate:Long=60*60*1000,//有效时间一个小时
    var passwordType:Int=0
):Parcelable
