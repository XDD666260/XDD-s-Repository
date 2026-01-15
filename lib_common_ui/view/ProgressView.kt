package com.baidu.lib_common_ui.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.baidu.lib_common_ui.R
import com.baidu.lib_common_ui.utils.dp2px
import kotlin.io.path.Path

class ProgressView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val mDefaultWidth = dp2px(100)
    private val mDefaultHeight = dp2px(10)
    private var mBackgroundColor: Int
    private var mProgressColor: Int
    private var mProgress: Float = 0f
    private var mProgressWidth: Float = 0f
    private var mTouchX: Float = 0f
    private var mSeekStartListener: () -> Unit = {}
    private var mSeekStopListener: (Float) -> Unit = {}
    private val mBgPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mBackgroundColor }
    }
    private val mProgressPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = mProgressColor }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)
        mBackgroundColor =
            typedArray.getColor(R.styleable.ProgressView_backgroundColor, Color.BLACK)
        mProgressColor = typedArray.getColor(R.styleable.ProgressView_progressColor, Color.WHITE)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val realWidth =
            resolveSizeAndState(mDefaultWidth, widthMeasureSpec, MEASURED_STATE_TOO_SMALL)
        val realHeight =
            resolveSizeAndState(mDefaultHeight, heightMeasureSpec, MEASURED_STATE_TOO_SMALL)

        setMeasuredDimension(realWidth, realHeight)
    }

    override fun onDraw(canvas: Canvas) {
        //绘制背景圆角矩形
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 1000f, 1000f, mBgPaint)
        canvas.drawRoundRect(0f, 0f, mProgressWidth, height.toFloat(), 1000f, 1000f, mProgressPaint)
    }

    fun updateProgress(rate: Float) {
        mProgress = rate * width
        mProgressWidth = mProgress
        invalidate()
    }

    //外部监听数据回调
    //开始拖拽了
    //停下来了
    fun setOnSeekListener(onStart: () -> Unit, onStop: (Float) -> Unit) {
        mSeekStartListener = onStart
        mSeekStopListener = onStop
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchX = event.x
                mSeekStartListener()
            }

            MotionEvent.ACTION_MOVE -> {
                //获取触摸点到放手为止的距离
                val dx = event.x - mTouchX
                mProgressWidth = mProgress + dx
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                //获取触摸点到放手为止的距离
                val dx = event.x - mTouchX
                //计算移动距离在整个进度条中的比例
                val seekRate = dx / width
                //回调数据
                mSeekStopListener(seekRate)
            }
        }
        return true
    }
}