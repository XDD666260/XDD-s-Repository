package com.example.littlepainter.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.example.littlepainter.model.IconModel
import com.example.littlepainter.utils.IconState
import com.example.littlepainter.viewmodel.HomeViewModel

class IconTextView(context: Context,attrs:AttributeSet?=null):AppCompatTextView(context,attrs) {
    var mIconModel:IconModel?=null
    var clickCallback:(IconTextView)->Unit={}
    init {
        typeface=Typeface.createFromAsset(context.assets,"iconfont.ttf")
        gravity=Gravity.CENTER
        setTextColor(Color.WHITE)
        setOnClickListener {
            clickCallback(this)
        }
    }
    fun setIconModel(model:IconModel){
        mIconModel=model
        text=resources.getString(model.iconString)
        setBackgroundColor(resources.getColor(model.normalColor,null))
    }
    fun updateIconState(state: IconState){
        if (state == IconState.SELECTED){
            setBackgroundColor(resources.getColor(mIconModel?.selectColor?:0,null))
        }else{
            setBackgroundColor(resources.getColor(mIconModel?.normalColor?:0,null))
        }
        mIconModel?.state=state
    }
}