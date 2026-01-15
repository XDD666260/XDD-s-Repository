package com.example.littlepainter.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.FrameLayout
import com.example.littlepainter.databinding.LayoutAlertViewBinding
import com.example.littlepainter.utils.ViewUtils
import kotlinx.coroutines.delay

class XJMAlertView(context: Context,attrs:AttributeSet?): FrameLayout(context,attrs) {
    private lateinit var binding: LayoutAlertViewBinding
    private val mDuration=500L
    private val mDistance=ViewUtils.dp2pxF(130)
    private val mDownAnimator:ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this,"translationY",0f,mDistance).apply {
            duration=mDuration
            interpolator=BounceInterpolator()
        }
    }
    private val mUpAnimator:ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this,"translationY",mDistance,0f).apply {
            duration=mDuration
            interpolator=BounceInterpolator()
        }
    }
    init {
        val inflater=LayoutInflater.from(context)
        binding=LayoutAlertViewBinding.inflate(inflater)
        val lp=LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        addView(binding.root,lp)
    }
    fun showMessage(message:String){
        binding.tvContent.text=message
        binding.tvContent.setTextColor(Color.BLACK)
        startAnimation()
    }
    fun showErrorMessage(message: String){
        binding.tvContent.text=message
        binding.tvContent.setTextColor(Color.RED)
        startAnimation()
    }
    private fun startAnimation(){
        mDownAnimator.start()
        mUpAnimator.startDelay=1500
        mUpAnimator.start()
    }
}