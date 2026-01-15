package com.example.littlepainter.ui.fragment.home.draw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.util.Log
import com.example.littlepainter.R
import com.example.littlepainter.utils.ViewUtils
import com.example.littlepainter.viewmodel.HomeViewModel

//绘制图形的抽象类
abstract class BaseShape {
    var startX:Float=0f
    var startY:Float=0f
    var endX:Float=0f
    var endY:Float=0f
    var centerX:Float=0f
    var centerY:Float=0f
    var mShapeState=ShapeState.NORMAL
    val mPath=Path()
    protected var mIsInMoveMode=false//记录move图标是不是被点击
    //矩形区域
    var rectF:RectF= RectF()
    val mPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth=HomeViewModel.instance().mStrokeWidth
        color=HomeViewModel.instance().mColor
        style=HomeViewModel.instance().mStrokeStyle
        strokeJoin=Paint.Join.ROUND
        strokeCap=Paint.Cap.ROUND
    }
    open val mCornerBitmap:Bitmap by lazy {
        BitmapFactory.decodeResource(HomeViewModel.instance().getContext().resources,
        R.drawable.scale_corner)
    }
    //四个角默认的绘制尺寸
    open val mCornerSize:Float by lazy {
        ViewUtils.dp2pxF(12)/2
    }
    private val mSelectedBoarderPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color=Color.parseColor("#6375FE")
        strokeWidth=ViewUtils.dp2pxF(2)
        style=Paint.Style.STROKE
    }
    //手指响应尺寸
    private val mReactSize:Float by lazy {
        ViewUtils.dp2pxF(16)
    }
     var mMovePosition=MovePosition.NONE
    protected var mMoveStartX=0f
    protected var mMoveStartY=0f
    var mMoveDx=0f
    var mMoveDy=0f


    //填充颜色
    open fun fillColor(){
        mPaint.color=HomeViewModel.instance().mColor
    }

    fun select(){
        mShapeState=ShapeState.SELECT
    }
    fun unSelected(){
        mShapeState=ShapeState.NORMAL
        mMovePosition=MovePosition.NONE
    }
    open fun calculateMovePosition(x:Float,y: Float){
        mMoveStartX=x
        mMoveStartY=y
        if (x in rectF.left-mReactSize..rectF.left+mReactSize && y in rectF.top-mReactSize .. rectF.top+mReactSize){
            //左上角
            mMovePosition=MovePosition.TOP_LEFT
        }else if (x in rectF.right-mReactSize..rectF.right+mReactSize && y in rectF.top-mReactSize .. rectF.top+mReactSize){
            //右上角
            mMovePosition=MovePosition.TOP_RIGHT
        }else if (x in rectF.left-mReactSize..rectF.left+mReactSize && y in rectF.bottom-mReactSize .. rectF.bottom+mReactSize){
            //左下角
            mMovePosition=MovePosition.BOTTOM_LEFT
        }else if (x in rectF.right-mReactSize..rectF.right+mReactSize && y in rectF.bottom-mReactSize .. rectF.bottom+mReactSize){
            //右下角
            mMovePosition=MovePosition.BOTTOM_RIGHT
        }else if (x in rectF.left-mReactSize..rectF.left+mReactSize){
            //左边
            mMovePosition=MovePosition.LEFT
        }else if (x in rectF.right-mReactSize..rectF.right+mReactSize){
            //右边
            mMovePosition=MovePosition.RIGHT
        }else if(y in rectF.top-mReactSize .. rectF.top+mReactSize){
            //上边
            mMovePosition=MovePosition.TOP
        }else if(y in rectF.bottom-mReactSize .. rectF.bottom+mReactSize){
            //底部
            mMovePosition=MovePosition.BOTTOM
        }else{
            mMovePosition=MovePosition.CENTER
        }
        Log.v("xjm","$mMovePosition")
    }

    //设置起始点坐标
    open fun setStartPoint(x:Float,y:Float){
        startX=x
        startY=y
        mPaint.strokeWidth=HomeViewModel.instance().mStrokeWidth
        mPaint.color=HomeViewModel.instance().mColor
        mPaint.style=HomeViewModel.instance().mStrokeStyle
    }
    //设置终点坐标
    open fun setEndPoint(x:Float,y:Float){
        when(mMovePosition){
            MovePosition.NONE->{
                if (mIsInMoveMode)return
                //矩形区域
                endX=x
                endY=y
                rectF.left=Math.min(startX,endX)
                rectF.right=Math.max(startX,endX)
                rectF.top=Math.min(startY,endY)
                rectF.bottom=Math.max(startY,endY)
            }
            MovePosition.CENTER->{
                mMoveDx=x-mMoveStartX
                mMoveDy=y-mMoveStartY
                //修改起始点坐标
                startX+=mMoveDx
                startY+=mMoveDy
                //修改终点坐标
                endX+=mMoveDx
                endY+=mMoveDy

                mMoveStartX=x
                mMoveStartY=y
                rectF.offset(mMoveDx,mMoveDy)
            }
            MovePosition.LEFT->{
                moveLeft(x,y)
            }
            MovePosition.RIGHT->{
                moveRight(x,y)
            }
            MovePosition.TOP->{
                moveTop(x,y)
            }
            MovePosition.BOTTOM->{
                moveBottom(x,y)
            }
            MovePosition.TOP_LEFT->{
                moveTop(x,y)
                moveLeft(x,y)
            }
            MovePosition.TOP_RIGHT->{
                moveTop(x,y)
                moveRight(x,y)
            }
            MovePosition.BOTTOM_LEFT->{
                moveBottom(x,y)
                moveLeft(x,y)
            }
            MovePosition.BOTTOM_RIGHT->{
                moveBottom(x,y)
                moveRight(x,y)
            }
        }
        centerX=(startX+endX)/2
        centerY=(startY+endY)/2
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
    fun updateShapeState(state: ShapeState){
        mShapeState=state
    }

    open fun draw(canvas: Canvas){
        if (mShapeState==ShapeState.SELECT){
            canvas.drawRect(rectF,mSelectedBoarderPaint)
            //左上角
            canvas.drawBitmap(mCornerBitmap, rectF.left - mCornerSize, rectF.top-mCornerSize, null)
            //右下角
            canvas.drawBitmap(mCornerBitmap, rectF.right - mCornerSize, rectF.top-mCornerSize, null)
            //左下角
            canvas.drawBitmap(mCornerBitmap, rectF.left - mCornerSize, rectF.bottom-mCornerSize, null)
            //右下角
            canvas.drawBitmap(mCornerBitmap, rectF.right - mCornerSize, rectF.bottom-mCornerSize, null)
        }
    }
    //判断触摸点是否在path内部
    open fun containsPointInPath(x:Float, y:Float):Boolean{
        val pathRegion=Region()
        val clipRegion=Region(
            rectF.left.toInt(),
            rectF.top.toInt(),
            rectF.right.toInt(),
            rectF.bottom.toInt()
        )
        pathRegion.setPath(mPath,clipRegion)
        return pathRegion.contains(x.toInt(),y.toInt())
    }
    //判断触摸点是否在矩形区域 内部
    open fun containsPointInRect(x:Float,y:Float):Boolean{
        //创建包含选择区域的rect
        val outRectF=RectF().apply {
            left=rectF.left-mCornerSize
            top=rectF.top-mCornerSize
            right=rectF.right+mCornerSize
            bottom=rectF.bottom+mCornerSize
        }
        return outRectF.contains(x,y)
    }
    fun updateMoveMode(isInMoveMode:Boolean){
        mIsInMoveMode=isInMoveMode
    }

    enum class MovePosition{
        TOP_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER,
        NONE
    }

}