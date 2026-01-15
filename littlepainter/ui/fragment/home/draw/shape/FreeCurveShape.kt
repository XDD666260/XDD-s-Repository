package com.example.littlepainter.ui.fragment.home.draw.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.text.Selection.moveLeft
import com.example.littlepainter.ui.fragment.home.draw.BaseShape
import kotlin.io.path.Path
import kotlin.math.max
import kotlin.math.min

class FreeCurveShape: BaseShape() {
    private var mostLeft=0f
    private var mostRight=0f
    private var mostTop=0f
    private var mostBottom=0f
    override fun draw(canvas: Canvas) {

        canvas.drawPath(mPath,mPaint)
        super.draw(canvas)
    }

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        rectF.left=x
        rectF.right=x
        rectF.top=y
        rectF.bottom=y
        //设置路径起点
        mPath.moveTo(x,y)
        mPaint.style=Paint.Style.STROKE
    }

    override fun setEndPoint(x: Float, y: Float) {
        when(mMovePosition) {
            MovePosition.NONE -> {
                if (mIsInMoveMode) return
                rectF.left= min(rectF.left,x)
                rectF.right= max(rectF.right,x)
                rectF.top= min(rectF.top,y)
                rectF.bottom= max(rectF.bottom,y)
            }

            MovePosition.CENTER -> {
                mMoveDx = x - mMoveStartX
                mMoveDy = y - mMoveStartY

                rectF.offset(mMoveDx, mMoveDy)
                mPath.offset(mMoveDx,mMoveDy)
                mMoveStartX=x
                mMoveStartY=y
            }
            else->{}
        }

        if (mIsInMoveMode)return
        mPath.lineTo(x,y)
    }
    private fun moveLeft(x:Float,y:Float){
        mMoveDx=x-mMoveStartX
        if (startX<endX){//从左到右画
            startX+=mMoveDx
        }else{//从右到左画
            endX+=mMoveDx
        }
        mMoveStartX=x
        rectF.left+=mMoveDx
    }
    private fun moveRight(x:Float,y: Float){
        mMoveDx=x-mMoveStartX
        if (startX<endX){//从左到右画
            endX+=mMoveDx
        }else{//从右到左画
            startX+=mMoveDx
        }
        mMoveStartX=x
        rectF.right+=mMoveDx
    }
    private fun moveTop(x:Float,y: Float){
        mMoveDy=y-mMoveStartY
        if (startX<endX){//从上到下画
            startY+=mMoveDy
        }else{//从下到上画
            endY+=mMoveDy
        }
        mMoveStartY=y
        rectF.top+=mMoveDy
    }
    private fun moveBottom(x: Float,y: Float){
        mMoveDy=y-mMoveStartY
        if (startX<endX){//从上到下画
            endY+=mMoveDy
        }else{//从下到上画
            startY+=mMoveDy
        }
        mMoveStartY=y
        rectF.bottom+=mMoveDy
    }

}