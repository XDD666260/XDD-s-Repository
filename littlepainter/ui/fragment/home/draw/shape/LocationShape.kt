package com.example.littlepainter.ui.fragment.home.draw.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.example.littlepainter.ui.fragment.home.draw.ArrowPath
import com.example.littlepainter.ui.fragment.home.draw.BaseShape
import com.example.littlepainter.utils.ViewUtils
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class LocationShape: BaseShape() {
    private val mArrowLength=ViewUtils.dp2pxF(5)
    override fun draw(canvas: Canvas) {

        canvas.drawPath(mPath,mPaint)
        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        //设置路径起点
        mPaint.style= Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        super.setEndPoint(x, y)
        mPath.reset()
//        //水平
//        ArrowPath.addArrowToPath(
//            mPath,
//            Math.min(startX, endX),
//            centerY,
//            Math.max(startX, endX),
//            centerY,
//            mArrowLength
//        )
//        ArrowPath.addArrowToPath(
//            mPath,
//            centerX,
//            Math.max(startY, endY),
//            centerX,
//            Math.min(startY, endY),
//            mArrowLength
//        )
        //X轴
        ArrowPath.addArrowToPath(
            mPath,
            rectF.left,
            (rectF.top+rectF.bottom)/2,
            rectF.right,
            (rectF.top+rectF.bottom)/2,
            mArrowLength
        )
        //Y轴
        ArrowPath.addArrowToPath(
            mPath,
            (rectF.left+rectF.right)/2,
            rectF.bottom,
            (rectF.left+rectF.right)/2,
            rectF.top,
            mArrowLength
        )
    }


}