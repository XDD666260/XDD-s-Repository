package com.example.littlepainter.ui.fragment.home.file

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream


class FileManager private constructor(){
    private lateinit var mContext:Context
    private  var mUserName:String?=null
    companion object{
        //缩略图文件夹名称
        private val Thumbnail_Name="thumbnail"
        //原图文件夹名称
        private val Original_Name="origin"
        val instance:FileManager by lazy { FileManager() }
        fun init(context:Application){
            instance.mContext=context
        }
    }
    //获取缩略图路径
    private fun getThumbnailPath():String{
        return "${mContext.filesDir.path}/$mUserName/$Thumbnail_Name"
    }
    //获取原图路径
    private fun getOriginalPath():String{
        return "${mContext.filesDir.path}/$mUserName/$Original_Name"
    }
    //获取用户文件夹路径
    private fun getUserRootPath():String{
        return "${mContext.filesDir.path}/$mUserName"
    }
    //用户登录
    //创建用户文件夹
    fun login(username:String){
        mUserName=username
        val userPath=getUserRootPath()
        val file=File(userPath)
        if (file.exists())return

        File(getThumbnailPath()).mkdirs()
        File(getOriginalPath()).mkdirs()
    }
    //退出登录
    fun logout(){
        mUserName=null
    }
    //注销用户
    fun logOff(){
        mUserName=null
    }
    //保存图片
    fun saveBitmap(bitmap:Bitmap,name:String,isOrigin:Boolean){
        val path=if(isOrigin){
            getOriginalPath()
        } else{
            getThumbnailPath()
        }
        val filePath="$path/$name"

        BufferedOutputStream(FileOutputStream(filePath)).use { bos->
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos)
        }
    }
    fun loadThumbnailImage():List<String>{
        val files= arrayListOf<String>()
        if (mUserName!=null){
            val thumbnailPath=getThumbnailPath()
            File(thumbnailPath).list()?.forEach { name->
                val filePath="$thumbnailPath/$name"
                files.add(filePath)
            }
        }
        return files
    }
    fun getOriginPathForPath(name:String):String{
        return "${getOriginalPath()}/$name"
    }

    fun removeFile(path:String){
        File(path).also {file->
            if (file.exists()){
                file.delete()
            }
        }
    }

}