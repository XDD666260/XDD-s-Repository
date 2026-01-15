package com.example.littlepainter.ui.fragment.home.draw.shape

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.example.littlepainter.ui.fragment.home.draw.BaseShape
import com.example.littlepainter.utils.ViewUtils
import kotlin.io.path.Path
import androidx.core.graphics.toColorInt
import com.example.littlepainter.ui.fragment.home.draw.ShapeState
import com.example.littlepainter.viewmodel.HomeViewModel

class EraserShape: BaseShape() {
    private val mEraserCirclePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color= "#80CCCCCC".toColorInt()
        style=Paint.Style.FILL
    }
    private var mCircleCenterX=0f
    private var mCircleCenterY=0f
    private var mEraserSize=ViewUtils.dp2pxF(30)
    override fun draw(canvas: Canvas) {
        canvas.drawPath(mPath,mPaint)
        if (mShapeState==ShapeState.DRAWING){
            canvas.drawCircle(mCircleCenterX,mCircleCenterY,mEraserSize/2f,mEraserCirclePaint)
        }
    }

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        //橡皮擦中心点
        mCircleCenterX=x
        mCircleCenterY=y
        //修改画笔的混合模式
        mPaint.xfermode=PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mPaint.strokeWidth=mEraserSize
        //设置路径起点
        mPath.moveTo(x,y)
        mPaint.style=Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        if (mIsInMoveMode)return
        //矩形区域
        endX=x
        endY=y
        rectF.left=Math.min(startX,endX)
        rectF.right=Math.max(startX,endX)
        rectF.top=Math.min(startY,endY)
        rectF.bottom=Math.max(startY,endY)
        mPath.lineTo(x,y)
        //橡皮擦中心点
        mCircleCenterX=x
        mCircleCenterY=y
    }

}