package com.example.littlepainter.ui.fragment.home.draw.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.example.littlepainter.ui.fragment.home.draw.BaseShape

class RectangleShape: BaseShape() {
    override fun draw(canvas: Canvas) {
        canvas.drawRect(rectF,mPaint)
        super.draw(canvas)
    }

    override fun containsPointInPath(x: Float, y: Float): Boolean {
        return rectF.contains(x,y)
    }
    override fun fillColor() {
        super.fillColor()
        mPaint.style= Paint.Style.FILL
    }
}