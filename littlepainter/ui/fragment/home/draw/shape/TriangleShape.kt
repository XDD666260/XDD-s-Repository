package com.example.littlepainter.ui.fragment.home.draw.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.example.littlepainter.ui.fragment.home.draw.BaseShape

class TriangleShape: BaseShape() {
    override fun draw(canvas: Canvas) {

        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }


    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)

        if (mMovePosition==MovePosition.CENTER){
            mPath.offset(mMoveDx,mMoveDy)
        }else{
            mPath.reset()
            mPath.moveTo(rectF.left,rectF.bottom)
            mPath.lineTo(rectF.right,rectF.bottom)
            mPath.lineTo((rectF.left+rectF.right)/2,rectF.top)
            mPath.close()
        }
    }

    override fun fillColor() {
        super.fillColor()
        mPaint.style=Paint.Style.FILL
    }
}