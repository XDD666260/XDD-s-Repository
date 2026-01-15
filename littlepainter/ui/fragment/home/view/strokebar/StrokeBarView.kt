package com.example.littlepainter.ui.fragment.home.view.strokebar

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.littlepainter.databinding.StrokeBarLayoutBinding
import com.example.littlepainter.utils.ViewUtils
import com.example.littlepainter.viewmodel.HomeViewModel

class StrokeBarView(context: Context, attrs:AttributeSet?=null):FrameLayout(context,attrs) {
    private var mBinding: StrokeBarLayoutBinding?=null
    private val mDistance:Float by lazy {
        (mBinding!!.ivDotFill.top-mBinding!!.ivDotEmpty.top).toFloat()
    }
    private val mDownAnimation:ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding?.indicatorView,"translationY",mDistance).apply {
            duration=200
        }
    }
    private val mUpAnimation:ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(mBinding?.indicatorView,"translationY",0f).apply {
            duration=200
        }
    }
    private var mIsEmptyStyle=true
    init {
        val inflater=LayoutInflater.from(context)
        mBinding=StrokeBarLayoutBinding.inflate(inflater)

        val lp=LayoutParams(LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.gravity=Gravity.CENTER
        addView(mBinding!!.root)


        mBinding!!.barView.addProgressChangeListener={progress->
            mBinding!!.tvSize.text="$progress"
            HomeViewModel.instance().mStrokeWidth=ViewUtils.dp2pxF(progress)
        }
        mBinding!!.ivDotEmpty.setOnClickListener {
            if (!mUpAnimation.isRunning && !mIsEmptyStyle){
                mUpAnimation.start()
                mIsEmptyStyle=true
                HomeViewModel.instance().mStrokeStyle=Paint.Style.STROKE
            }
        }
        mBinding!!.ivDotFill.setOnClickListener {
            if (!mDownAnimation.isRunning && mIsEmptyStyle){
                mDownAnimation.start()
                mIsEmptyStyle=false
                HomeViewModel.instance().mStrokeStyle=Paint.Style.FILL
            }
        }
    }
}