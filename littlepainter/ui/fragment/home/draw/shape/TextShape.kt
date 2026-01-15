package com.example.littlepainter.ui.fragment.home.draw.shape

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.text.TextPaint
import android.util.Log
import android.util.TypedValue
import com.example.littlepainter.R
import com.example.littlepainter.ui.fragment.home.draw.BaseShape
import com.example.littlepainter.ui.fragment.home.draw.ShapeState
import com.example.littlepainter.utils.ViewUtils
import com.example.littlepainter.viewmodel.HomeViewModel
import kotlin.io.path.Path

class TextShape: BaseShape() {
    private val mBoarderPath= Path()
    private val mBoarderPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style=Paint.Style.STROKE
        strokeWidth=ViewUtils.dp2pxF(2)
        color=HomeViewModel.instance().getContext().getColor(R.color.light_blue)
    }
    private val mTextPaint=TextPaint().apply {
        color=HomeViewModel.instance().mColor
        textSize=HomeViewModel.instance().mTextSize
    }
    var mText:String=""

    private val mPadding=ViewUtils.dp2pxF(5)
    private var cy=0f
    private var textLines:List<String> = emptyList()
    private var oneLineHeight=0f

    fun updateText(text:String){
        mText=text
        changeBoarderSize()
    }

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        rectF.top=y
        rectF.left=x
        rectF.right=x
        rectF.bottom=y
    }
    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mBoarderPath.reset()
        mBoarderPath.addRect(rectF,Path.Direction.CW)
    }
    override fun draw(canvas: Canvas) {


        if (mShapeState==ShapeState.DRAWING){
            canvas.drawPath(mBoarderPath,mBoarderPaint)
        }
        //修改字体颜色
        mTextPaint.color=HomeViewModel.instance().mColor
        textLines.forEachIndexed { index, line ->
            canvas.drawText(line,mPadding+rectF.left,cy+index*oneLineHeight,mTextPaint)
        }
        super.draw(canvas)
    }

    override fun containsPointInPath(x: Float, y: Float): Boolean {
        return true
    }

    private fun changeBoarderSize(){

        textLines=mText.split('\n')
        if (textLines.isEmpty())return
        var maxWidth=0f
        textLines.forEach { line->
            val w=mTextPaint.measureText(line)
            maxWidth= Math.max(maxWidth,w)
        }
        val textWidth=maxWidth
        val metrics=mTextPaint.fontMetrics
        oneLineHeight=metrics.bottom-metrics.top
        val textTotalHeight=oneLineHeight*textLines.size
        val space=(rectF.height()-textTotalHeight)/2

        rectF.top=space+rectF.top-mPadding
        rectF.right=rectF.left+textWidth+mPadding*2
        rectF.bottom=rectF.top+textTotalHeight+2*mPadding

        mBoarderPath.reset()
        mBoarderPath.addRect(rectF, Path.Direction.CW)

        val offsetY=(metrics.descent-metrics.ascent)/2-metrics.descent
        cy=mPadding+oneLineHeight/2+offsetY+rectF.top

    }
}