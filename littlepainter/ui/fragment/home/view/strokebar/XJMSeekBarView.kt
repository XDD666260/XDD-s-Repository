package com.example.littlepainter.ui.fragment.home.view.strokebar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.littlepainter.R
import com.example.littlepainter.utils.ViewUtils

class XJMSeekBarView(context: Context, attrs: AttributeSet?):View(context,attrs) {
    private var mMin:Int=1
    private var mMax:Int=50
    private var mProgress:Int=0
        set(value){
            field=value
            addProgressChangeListener(value)
        }
    private var mOrientation= Orientation.VERTICAL
    private var mProgressBarWidth=ViewUtils.dp2pxF(8)
    private var mDotSize=ViewUtils.dp2pxF(16)
    private var mDefaultHeight=ViewUtils.dp2pxF(100)
    private var mProgressBackgroundColor=Color.parseColor("#D1D1D1")
    private var mProgressColor=Color.parseColor("#6375FE")
    private var mDotColor=Color.WHITE
    private var mPadding=ViewUtils.dp2pxF(1)
    private val mPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.FILL
    }
    private val mBgRect=RectF()
    private val mProgressRect=RectF()
    private var mCx=0f
    private var mCy=0f
    var addProgressChangeListener:(Int)->Unit={}
    var addTouchStateListener:(Boolean)->Unit={}


    init {
        val typeArray=context.obtainStyledAttributes(attrs, R.styleable.XJMSeekBarView)
        mMin=typeArray.getInteger(R.styleable.XJMSeekBarView_min,1)
        mMax=typeArray.getInteger(R.styleable.XJMSeekBarView_max,10)
        mProgress=typeArray.getInteger(R.styleable.XJMSeekBarView_progress,1)
        val value=typeArray.getInteger(R.styleable.XJMSeekBarView_orientation,0)
        mOrientation=if (value==0) Orientation.VERTICAL else Orientation.HORIZONTAL
        typeArray.recycle()
        if (mProgress<mMin){
            mProgress=mMin
        }else if(mProgress>mMax){
            mProgress=mMax
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width=MeasureSpec.getSize(widthMeasureSpec)
        val widthMode=MeasureSpec.getMode(widthMeasureSpec)
        if (widthMode!=MeasureSpec.EXACTLY){
            if (mOrientation== Orientation.VERTICAL){
                width=(mDotSize+mPadding*2).toInt()
            }else{
                width=mDefaultHeight.toInt()
            }

        }
        var height=MeasureSpec.getSize(heightMeasureSpec)
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode!=MeasureSpec.EXACTLY){
            if (mOrientation== Orientation.VERTICAL){
                height=mDefaultHeight.toInt()
            }else{
                height=(mDotSize+mPadding*2).toInt()
            }

        }
        setMeasuredDimension(width,height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mOrientation== Orientation.VERTICAL){
            val hSpace=(measuredWidth-mProgressBarWidth)/2
            mBgRect.apply {
                left=hSpace
                right=measuredWidth-hSpace
                top=0f
                bottom=measuredHeight.toFloat()
            }
            mProgressRect.apply {
                left=hSpace
                right=measuredWidth-hSpace
                top=0f
                bottom=measuredHeight*mProgress/(mMax-mMin).toFloat()
            }
            mCx=measuredWidth/2f
            mCy=mProgressRect.bottom
        }else{
            val vSpace=(measuredHeight-mProgressBarWidth)/2
            mBgRect.apply {
                left=0f
                right=measuredWidth.toFloat()
                top=vSpace
                bottom=measuredHeight-vSpace
            }
            mProgressRect.apply {
                left=0f
                right=measuredWidth*mProgress/(mMax-mMin).toFloat()
                top=vSpace
                bottom=measuredHeight-vSpace
            }
            mCx=mProgressRect.right
            mCy=measuredHeight/2f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制背景
        mPaint.color=mProgressBackgroundColor
        canvas.drawRoundRect(mBgRect,mProgressBarWidth/2,mProgressBarWidth/2,mPaint)
        //绘制进度
        mPaint.color=mProgressColor
        canvas.drawRoundRect(mProgressRect,mProgressBarWidth/2,mProgressBarWidth/2,mPaint)
        //绘制圆点
        if (mOrientation== Orientation.VERTICAL){
            mCy=mProgressRect.bottom
        }else{
            mCx=mProgressRect.right
        }
        mPaint.color=mDotColor
        canvas.drawCircle(mCx,mCy,mDotSize/2,mPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_MOVE->{
                if (mOrientation== Orientation.VERTICAL){
                    if (event.y in 0f..measuredHeight.toFloat()){
                        mProgressRect.bottom=event.y
                        mProgress=((event.y/measuredHeight)*(mMax-mMin)).toInt()+1
                        invalidate()
                        addTouchStateListener(true)
                    }
                }else{
                    if (event.x in 0f..measuredWidth.toFloat()) {
                        mProgressRect.right = event.x
                        mProgress=((event.x/measuredWidth)*(mMax-mMin)).toInt()+1
                        invalidate()
                        addTouchStateListener(true)
                    }
                }

            }
            MotionEvent.ACTION_UP->{
                addTouchStateListener(false)
            }
        }
        return true
    }

    enum class Orientation{
        HORIZONTAL,VERTICAL
    }
}