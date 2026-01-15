package com.example.littlepainter.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.littlepainter.ui.fragment.home.draw.LayerManager
import com.example.littlepainter.utils.ViewUtils

class HomeViewModel(application: Application):AndroidViewModel(application){

    var mStrokeWidth=ViewUtils.dp2pxF(1)
    var mColor=Color.BLACK
    var mStrokeStyle=Paint.Style.STROKE
    var mTextSize= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,18f,application.resources.displayMetrics)
    //保存图层管理器
    val mLayerManager=LayerManager()
    fun getContext():Context{
        return getApplication()
    }


    companion object{
        private var instance:HomeViewModel?=null

        fun init(owner:ViewModelStoreOwner){
            if (instance==null){
                instance=ViewModelProvider(owner).get(HomeViewModel::class.java)
            }
        }

        fun instance():HomeViewModel{
            if (instance==null){
                throw Exception("必须要先调用init方法")
            }
            return instance!!
        }
    }
}