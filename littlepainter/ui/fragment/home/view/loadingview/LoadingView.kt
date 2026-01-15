package com.example.littlepainter.ui.fragment.home.view.loadingview

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.example.littlepainter.R
import com.example.littlepainter.databinding.LayerItemLayoutBinding
import com.example.littlepainter.databinding.LoadingLayoutBinding
import com.example.littlepainter.databinding.PickimagePopupViewLayoutBinding
import com.example.littlepainter.ui.fragment.home.layer.LayerState
import androidx.core.graphics.drawable.toDrawable

class LoadingView(val context:Context) {
    private var mBinding:LoadingLayoutBinding?= null

    private val popUpWindow:PopupWindow by lazy {
        val inflater=LayoutInflater.from(context)
        mBinding=LoadingLayoutBinding.inflate(inflater)
        PopupWindow(context).apply {
            contentView=mBinding!!.root
            width= LayoutParams.WRAP_CONTENT
            height= LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    fun show(parent:View){
        popUpWindow.showAtLocation(parent,Gravity.CENTER,0,0)
        mBinding!!.okView.visibility=View.INVISIBLE
    }
    fun hide(onAnimationEnd:()->Unit={}){
        mBinding!!.loadingView.visibility=View.INVISIBLE
        mBinding!!.okView.visibility=View.VISIBLE
        mBinding!!.okView.addAnimatorListener(object :Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                popUpWindow.dismiss()
                onAnimationEnd()
                mBinding!!.loadingView.visibility=View.VISIBLE
                mBinding!!.okView.visibility=View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })

    }

}