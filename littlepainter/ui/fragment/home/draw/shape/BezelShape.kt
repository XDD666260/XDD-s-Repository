package com.example.littlepainter.ui.fragment.home.draw.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.example.littlepainter.ui.fragment.home.draw.BaseShape
import kotlin.math.abs

class BezelShape: BaseShape() {
    override fun draw(canvas: Canvas) {

        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        mPaint.style=Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        //设置三角形的起点
        val space=Math.abs(startX-endX)/4f
        val sx=Math.min(startX,endX)
        val ex=Math.max(endX,startX)
        val midY=(startY+endY)/2
        val height=Math.abs(startY-endY)


        val controlX1=sx+(ex-sx)*0.382
        val controlX2=ex-(ex-sx)*0.382
        mPath.reset()
        mPath.moveTo(sx,midY)
        mPath.cubicTo(
            controlX1.toFloat(), Math.min(startY, endY)-height,
            controlX2.toFloat(), Math.max(startY,endY)+height,
            Math.max(endX,startX), (startY+endY)/2f
        )
    }

}