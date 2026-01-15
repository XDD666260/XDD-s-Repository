package com.example.littlepainter.ui.fragment.home.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.littlepainter.utils.ViewUtils
import kotlin.math.abs
import kotlin.math.sqrt

class ColorPickerView(context: Context,attrs:AttributeSet?): View(context,attrs) {
    private var defaultWidth=ViewUtils.dp2px(200)
    private var defaultHeight=ViewUtils.dp2px(200)
    private var centerY=0f
    private var centerX=0f
    private var mColorPickerRadius=0f
    private val mColorPickerPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.FILL
        color= Color.BLACK
    }
    private lateinit var mSweepGradient:SweepGradient
    //选取颜色的圆
    private var mTouchX=0f
    private var mTouchY=0f
    private val mSelectedRadius=ViewUtils.dp2pxF(10)
    private val mSelectedPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.FILL
    }
    private val mSelectedWhitePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.STROKE
        strokeWidth=10f
        color=Color.WHITE
    }
    //记录色相
    private var mHue=0f
    //饱和度
    private var mSaturation=1f
    //明度
    private var mLightness=1f
    private var mSelectedColor:Int=Color.BLACK
    //高阶函数回调当前颜色
    private var mCallBack:(Int)->Unit={}


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width=MeasureSpec.getSize(widthMeasureSpec)
        val widthMode=MeasureSpec.getMode(widthMeasureSpec)
        if (widthMode!=MeasureSpec.EXACTLY){
            width=defaultWidth
        }

        var height=MeasureSpec.getSize(heightMeasureSpec)
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode!=MeasureSpec.EXACTLY){
            height=defaultHeight
        }
        setMeasuredDimension(width,height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX=measuredWidth/2f
        centerY=measuredHeight/2f

        mColorPickerRadius=Math.min(measuredWidth,measuredHeight)/2f
        mSweepGradient= SweepGradient(
            centerX,
            centerY,
            intArrayOf(
                0xFFFF0000.toInt(),
                0xFFFFFF00.toInt(),
                0xFF00FF00.toInt(),
                0xFF00FFFF.toInt(),
                0xFF0000FF.toInt(),
                0xFFFF00FF.toInt(),
                0xFFFF0000.toInt(),
            ),
            null  //均匀分布
        )
        mColorPickerPaint.shader=mSweepGradient

        mTouchY=centerY
        mTouchX=centerX
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制圆形的颜色选择器
        canvas.drawCircle(centerX,centerY,mColorPickerRadius,mColorPickerPaint)

        //绘制颜色选择器中选中的小圆
        mSelectedColor=Color.HSVToColor(floatArrayOf(mHue,mSaturation,mLightness))
        mCallBack(mSelectedColor)
        mSelectedPaint.color=mSelectedColor
        canvas.drawCircle(mTouchX,mTouchY,mSelectedRadius,mSelectedWhitePaint)
        canvas.drawCircle(mTouchX,mTouchY,mSelectedRadius,mSelectedPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN,MotionEvent.ACTION_MOVE->{
                mTouchX=event.x
                mTouchY=event.y
                //计算触摸点的角度
                //计算触摸点是否在圆的内部
                if (isInPickerView(mTouchX,mTouchY)){
                    val radians=Math.atan2((mTouchY-centerY).toDouble(),(mTouchX-centerX).toDouble())
                    var degree=Math.toDegrees(radians)
                    if (degree<0){
                        degree += 360
                    }
                    mHue=degree.toFloat()
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP->{
                mCallBack(mSelectedColor)
            }
        }
        return true
    }
    fun setCurrentColor(color:Int){
        val hsv=FloatArray(3)
        Color.colorToHSV(color,hsv)

        mHue=hsv[0]
        mSaturation=hsv[1]
        mLightness=hsv[2]

        updateTouchPositionFromHue()
        invalidate()
    }
    fun getCurrentColor():Int{
        return mSelectedColor
    }
    fun addPickColorListener(listener:(Int)->Unit){
        mCallBack=listener
    }
    fun setSaturation(saturation:Float){
        mSaturation=saturation
        invalidate()
    }
    fun setLightness(lightness:Float){
        mLightness = lightness
        invalidate()
    }
    private fun updateTouchPositionFromHue(){
        // 将色相转换为弧度
        val radians = Math.toRadians(mHue.toDouble())

        // 计算半径（考虑饱和度和明度的影响）
        val effectiveRadius = mColorPickerRadius * mSaturation * mLightness
        val radius = effectiveRadius.coerceAtMost(mColorPickerRadius * 0.95f) // 限制在圆盘内

        // 根据角度和半径计算坐标
        mTouchX = centerX + (radius * Math.cos(radians)).toFloat()
        mTouchY = centerY + (radius * Math.sin(radians)).toFloat()
    }
    private fun isInPickerView(x:Float,y:Float):Boolean{
        val a= abs(x-centerX)
        val b= abs(y-centerY)
        val c= sqrt(a*a+b*b)
        return c<=mColorPickerRadius
    }
}