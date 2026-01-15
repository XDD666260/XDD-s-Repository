package com.example.littlepainter.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.littlepainter.R
import com.example.littlepainter.model.DotModel
import com.example.littlepainter.model.DotState
import com.example.littlepainter.utils.ViewUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class XJMUnlockView: View {
    private val defaultSize=ViewUtils.dp2px(300)
    private var mSquareSize=0
    private var mRadius=0f
    private var mSpace=0f
    private var cx=0f
    private var cy=0f
    private val mDotModels= arrayListOf<DotModel>()
    private var mPasswordBuilder=StringBuilder()
    private var mLastSelectedDot: DotModel?=null
    private val bgCirclePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.FILL
        color=Color.parseColor("#5ccccccc")
    }
    private var mLinePath=Path()
    private var mMovePath=Path()
    private val mMovePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.STROKE
        color=context.getColor(R.color.blue)
        strokeWidth=ViewUtils.dp2px(5).toFloat()
    }
    private val mLinePaint=Paint(Paint.ANTI_ALIAS_FLAG)
    private var mCallBack:(String)->Boolean={true}
    constructor(context: Context):super(context){}
    constructor(context: Context,attrs:AttributeSet?):super(context,attrs){}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var widthSize=MeasureSpec.getSize(widthMeasureSpec)
        val widthMode=MeasureSpec.getMode(widthMeasureSpec)
        if (widthMode!=MeasureSpec.EXACTLY){
            widthSize=defaultSize
        }
        var heightSize=MeasureSpec.getSize(heightMeasureSpec)
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode!=MeasureSpec.EXACTLY){
            heightSize=defaultSize
        }
        setMeasuredDimension(widthSize,heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mSquareSize=Math.min(measuredWidth,measuredHeight)
        mRadius=mSquareSize/10f
        mSpace = mRadius
        cx=(measuredWidth-mSquareSize)/2+mRadius+mSpace
        cy=(measuredHeight-mSquareSize)/2+mRadius+mSpace
        initNineDotModels()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawNineDot(canvas)
        drawMovePath(canvas)
        drawLinePath(canvas)
        drawSelectedDot(canvas)

    }
    fun addOnPicPathFinishListener(listener:(String)->Boolean){
        mCallBack=listener
    }
    fun showError(){
        mDotModels.forEach { dot->
            if (dot.state== DotState.SELECTED){
                dot.state= DotState.ERROR
            }
        }
        mMovePaint.color=Color.RED
        invalidate()
    }
    private fun initNineDotModels(){
        var number=1
        for (i in 0..2){
            for (j in 0..2){
                val x=cx*(j+1)+mSpace*j
                val y=cy*(i+1)+mSpace*i
                val model= DotModel(number,x,y,mRadius,WeakReference(context))
                number++
                mDotModels.add(model)
            }
        }
    }
    private fun drawNineDot(canvas: Canvas){
        for (i in 0..2){
            for (j in 0..2){
                canvas.drawCircle(cx*(i+1)+mSpace*i,cy*(j+1)+mSpace*j,mRadius,bgCirclePaint)
            }
        }
    }
    private fun drawSelectedDot(canvas: Canvas){
        mDotModels.forEach { dot->
            if (dot.state== DotState.SELECTED){
                canvas.drawBitmap(dot.normalBitmap,null,dot.recF,mLinePaint)
            }else if (dot.state== DotState.ERROR){
                canvas.drawBitmap(dot.errorBitmap,null,dot.recF,mLinePaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN->{touchEvent(event.x,event.y)}
            MotionEvent.ACTION_MOVE->{touchEvent(event.x,event.y)}
            MotionEvent.ACTION_UP->{handleResult()}
        }
        return true
    }
    private fun touchEvent(x:Float,y:Float){
        mDotModels.forEach { dot->
            if (dot.containPoint(x,y)){
                if (dot.state== DotState.NORMAL){
                    dot.state= DotState.SELECTED
                    mPasswordBuilder.append(dot.num)
                    mLastSelectedDot=dot
                    invalidate()
                    if (mLinePath.isEmpty){
                        mLinePath.moveTo(dot.cx,dot.cy)
                    }else{
                        mLinePath.lineTo(dot.cx,dot.cy)
                    }
                }
            }else{
                //触摸点在外部
                if (mLastSelectedDot!=null){
                    mMovePath.reset()
                    mMovePath.moveTo(mLastSelectedDot!!.cx,mLastSelectedDot!!.cy)
                    mMovePath.lineTo(x,y)
                    invalidate()
                }
            }
        }
    }

    private fun handleResult(){
        mMovePath.reset()
        invalidate()
        val shouldClear=mCallBack(mPasswordBuilder.toString())
        val delayTime=if (shouldClear){
            200L
        }else{
            //显示错误状态
            1000L
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(delayTime)
            withContext(Dispatchers.Main){
                clear()
            }
        }
    }
    private fun clear(){
        mLinePath.reset()
        mDotModels.forEach { dot->
            if (dot.state!= DotState.NORMAL){
                dot.state= DotState.NORMAL
            }
        }
        mPasswordBuilder.clear()
        mMovePaint.color=resources.getColor(R.color.blue,null)
        invalidate()
    }

    private fun drawMovePath(canvas: Canvas){
        if (mMovePath.isEmpty)return
        canvas.drawPath(mMovePath,mMovePaint)
    }
    private fun drawLinePath(canvas: Canvas){
        if (mLinePath.isEmpty)return
        canvas.drawPath(mLinePath,mMovePaint)
    }
}