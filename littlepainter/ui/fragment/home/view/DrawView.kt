package com.example.littlepainter.ui.fragment.home.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import com.example.littlepainter.ui.fragment.home.draw.ActionType
import com.example.littlepainter.ui.fragment.home.draw.LayerManager
import com.example.littlepainter.ui.fragment.home.draw.ShapeState
import com.example.littlepainter.ui.fragment.home.draw.ShapeType
import com.example.littlepainter.ui.fragment.home.draw.TextState
import com.example.littlepainter.ui.fragment.home.draw.shape.TextShape
import com.example.littlepainter.utils.IconType
import com.example.littlepainter.viewmodel.HomeViewModel
import androidx.core.graphics.scale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import androidx.core.graphics.createBitmap

class DrawView(context: Context,attrs:AttributeSet?):View(context,attrs) {
    private var mDrawShapeType:ShapeType=ShapeType.NONE
    private var mActionType:ActionType=ActionType.NONE
    private lateinit var scaledBitmap:Bitmap
    private val layerManager:LayerManager by lazy {
        HomeViewModel.instance().mLayerManager
    }
    var refreshLayerListener:()->Unit={}
    //回调显示键盘
    var addShowKeyBoardListener:(Boolean)->Unit={}
    //记录文本输入状态
    private var mTextState=TextState.NONE
    @DrawableRes
    private var mBackgroundResourceId:Int?=null
        set(value) {
            field=value
            if (value!=null){
                mBackgroundBitmap=BitmapFactory.decodeResource(resources,value)
                invalidate()
            }
        }
    private var mBackgroundBitmap: Bitmap?=null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layerManager.addLayer(measuredWidth,measuredHeight)
    }
    fun changeBackgroundImage(@DrawableRes id:Int){
        mBackgroundResourceId=id
    }
    override fun onDraw(canvas: Canvas) {
        //绘制背景图片
        if (mBackgroundResourceId!=null){
            scaledBitmap = mBackgroundBitmap!!.scale(width, height)
            canvas.drawBitmap(scaledBitmap,0f,0f,null)
        }
        layerManager.draw()

        layerManager.getLayersBitmap().forEach { bitmap->
            canvas.drawBitmap(bitmap,0f,0f,null)
        }
    }

    fun refresh(){
        invalidate()
    }
    fun getBitmap():Flow<Bitmap>{
        val bitmapFlow:Flow<Bitmap> = flow {

                val bitmap= createBitmap(width, height)
                val canvas=Canvas(bitmap)
                mBackgroundBitmap?.let {
                    scaledBitmap = mBackgroundBitmap!!.scale(width, height)
                    canvas.drawBitmap(scaledBitmap,0f,0f,null)
                }
                layerManager.getLayersBitmap().forEach { layerBitmap->
                    canvas.drawBitmap(layerBitmap,0f,0f,null)
                }
                emit(bitmap)

        }
        return bitmapFlow
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                when(mActionType){
                    ActionType.DRAW->{
                        if (mDrawShapeType==ShapeType.TEXT && mTextState==TextState.EDITING){
                            mTextState=TextState.NONE
                            layerManager.updateShapeState(ShapeState.NORMAL)
                            addShowKeyBoardListener(false)
                            invalidate()
                        }else{
                            layerManager.addShape(mDrawShapeType,event.x,event.y)
                            //修改图形所在的状态
                            layerManager.updateShapeState(ShapeState.DRAWING)
                            if (mDrawShapeType==ShapeType.TEXT){
                                if (mTextState==TextState.NONE){
                                    //弹出键盘
                                    addShowKeyBoardListener(true)
                                    mTextState=TextState.EDITING
                                }else{
                                    //退出编辑状态
                                    mTextState=TextState.NONE
                                    layerManager.updateShapeState(ShapeState.NORMAL)

                                }
                            }
                        }
                    }
                    ActionType.FILL->{
                        layerManager.fillColor(event.x,event.y)
                        invalidate()
                    }
                    ActionType.MOVE->{
                        layerManager.selectShape(event.x,event.y)
                        invalidate()
                    }
                    else->{}
                }
                return true
            }
            MotionEvent.ACTION_MOVE->{
                when(mActionType){
                    ActionType.DRAW,ActionType.MOVE->{
                        layerManager.addEndPoint(event.x,event.y)
                        invalidate()
                    }

                    else->{}
                }

            }
            MotionEvent.ACTION_UP->{
                //当前绘制完毕
                refreshLayerListener()
                if (mDrawShapeType!=ShapeType.TEXT && mActionType!=ActionType.MOVE){
                    layerManager.updateShapeState(ShapeState.NORMAL)
                }
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    fun resetDrawToolType(){
        mActionType=ActionType.NONE
    }
    //接收文本
    fun refreshText(text:String){
        HomeViewModel.instance().mLayerManager.updateText(text)
        invalidate()
    }
    fun refreshTextColor(){
        if (mDrawShapeType == ShapeType.TEXT && mTextState==TextState.EDITING){
            invalidate()
        }
    }

    fun setCurrentDrawType(type:IconType){
        when(type){
            IconType.NONE->{
                mActionType=ActionType.NONE
                mDrawShapeType=ShapeType.NONE
                mTextState=TextState.NONE
            }
            IconType.DRAW_MENU->{
                mActionType=ActionType.NONE
                mDrawShapeType=ShapeType.NONE
                mTextState=TextState.NONE
            }
            IconType.DRAW_MOVE->{
                mActionType=ActionType.MOVE
                mDrawShapeType=ShapeType.NONE
                mTextState=TextState.NONE
            }
            IconType.DRAW_BRUSH->{
                mActionType=ActionType.FILL
                mDrawShapeType=ShapeType.NONE
                mTextState=TextState.NONE
            }
            else-> {
                mActionType = ActionType.DRAW
                mDrawShapeType = when(type){
                    IconType.DRAW_CIRCLE-> ShapeType.CIRCLE
                    IconType.DRAW_RECTANGLE-> ShapeType.RECTANGLE
                    IconType.DRAW_LINE-> ShapeType.LINE
                    IconType.DRAW_CURVE-> ShapeType.CURVE
                    IconType.DRAW_TRIANGLE-> ShapeType.TRIANGLE
                    IconType.DRAW_BEZEL-> ShapeType.BEZEL
                    IconType.DRAW_LINE_ARROW-> ShapeType.ARROW
                    IconType.DRAW_LOCATION-> ShapeType.LOCATION
                    IconType.DRAW_TEXT-> ShapeType.TEXT
                    IconType.DRAW_ERASER->ShapeType.ERASER

                    else-> ShapeType.NONE
                }
            }
        }
    }


}