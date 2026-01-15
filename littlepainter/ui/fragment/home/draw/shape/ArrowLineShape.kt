package com.example.littlepainter.ui.fragment.home.draw.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import com.example.littlepainter.ui.fragment.home.draw.ArrowPath
import com.example.littlepainter.ui.fragment.home.draw.BaseShape
import com.example.littlepainter.ui.fragment.home.draw.ShapeState
import com.example.littlepainter.utils.ViewUtils
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ArrowLineShape: BaseShape() {
    private var leftIsStart=false
    private val mArrowLength=ViewUtils.dp2pxF(20)
    override fun calculateMovePosition(x: Float, y: Float) {
        mMoveStartX=x
        mMoveStartY=y
        leftIsStart= startX<endX
        var leftX=0f
        var rightX=0f
        var leftY=0f
        var rightY=0f
        if (startX<endX){//从左到右
            leftX = startX
            leftY = startY
            rightX=endX
            rightY=endY
        }else{//从右到左
            leftX = endX
            leftY = endY
            rightX=startX
            rightY=startY
        }
        if (x in leftX-mCornerSize..leftX+mCornerSize && y in leftY-mCornerSize..leftY+mCornerSize){
            mMovePosition=MovePosition.LEFT
        }else if (x in rightX-mCornerSize..rightX+mCornerSize && y in rightY-mCornerSize..rightY+mCornerSize){
            mMovePosition=MovePosition.RIGHT
        }else{
            mMovePosition=MovePosition.CENTER
        }
    }
    override fun setEndPoint(x: Float, y: Float) {
        when(mMovePosition) {
            MovePosition.NONE -> {
                if (mIsInMoveMode)return
                //矩形区域
                endX = x
                endY = y
                rectF.left = Math.min(startX, endX)
                rectF.right = Math.max(startX, endX)
                rectF.top = Math.min(startY, endY)
                rectF.bottom = Math.max(startY, endY)
            }

            MovePosition.CENTER -> {
                mMoveDx = x - mMoveStartX
                mMoveDy = y - mMoveStartY
                //修改起始点坐标
                startX += mMoveDx
                startY += mMoveDy
                //修改终点坐标
                endX += mMoveDx
                endY += mMoveDy

                mMoveStartX = x
                mMoveStartY = y
                rectF.offset(mMoveDx, mMoveDy)
            }
            MovePosition.LEFT->{
                if (leftIsStart){
                    startX=x
                    startY=y
                }else{
                    endX=x
                    endY=y
                }
            }
            MovePosition.RIGHT->{
                if (leftIsStart){
                    endX=x
                    endY=y
                }else{
                    startX=x
                    startY=y
                }
            }
            else->{}
        }
        mPath.reset()
        ArrowPath.addArrowToPath(mPath,startX,startY,endX,endY,mArrowLength)
    }
    override fun draw(canvas: Canvas) {
        if (mShapeState== ShapeState.SELECT){
            //在起点画一个矩形
            canvas.drawBitmap(mCornerBitmap, startX - mCornerSize, startY-mCornerSize, null)
            //在终点画一个矩形
            canvas.drawBitmap(mCornerBitmap, endX - mCornerSize, endY-mCornerSize, null)
        }
        canvas.drawPath(mPath,mPaint)
    }

    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        //设置路径起点
        mPaint.style= Paint.Style.STROKE
    }
    override fun containsPointInPath(x:Float, y:Float):Boolean{
        val tolerance=mPaint.strokeWidth
        val d1=distance(startX,startY,x,y)
        val d2=distance(endX,endY,x,y)
        val lineLen=distance(startX,startY,endX,endY)
        return Math.abs(d1+d2-lineLen)<=tolerance
    }

    override fun containsPointInRect(x: Float, y: Float): Boolean {
        val tolerance=mPaint.strokeWidth
        val d1=distance(startX,startY,x,y)
        val d2=distance(endX,endY,x,y)
        val lineLen=distance(startX,startY,endX,endY)
        return Math.abs(d1+d2-lineLen)<=tolerance
    }

    private fun distance(x1:Float,y1:Float,x2:Float,y2:Float):Float{
        return sqrt(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)).toDouble()).toFloat()
    }

}