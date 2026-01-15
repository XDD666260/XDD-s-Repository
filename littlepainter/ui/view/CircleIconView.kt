package com.example.littlepainter.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import com.example.littlepainter.R
import com.example.littlepainter.model.IconModel
import io.github.florent37.shapeofview.shapes.CircleView

class CircleIconView(context: Context,attrs:AttributeSet?=null):CircleView(context,attrs) {
    private lateinit var mIconTextView:IconTextView
    var clickCallback:(IconTextView)->Unit={}
        set(value)  {
            field=value
            mIconTextView.clickCallback=value
        }
    init {
        mIconTextView=IconTextView(context)
        val lp=LayoutParams(LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        lp.gravity=Gravity.CENTER
        addView(mIconTextView,lp)

        val typeArray=context.obtainStyledAttributes(attrs, R.styleable.CircleIconView)
        val textSize=typeArray.getDimension(R.styleable.CircleIconView_icon_size,20f)
        typeArray.recycle()
        setIconSize(textSize.toInt())
    }
    fun setIconModel(model: IconModel){
        mIconTextView.setIconModel(model)
    }
    fun setIconSize(size:Int){
        mIconTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,size.toFloat())
    }
}