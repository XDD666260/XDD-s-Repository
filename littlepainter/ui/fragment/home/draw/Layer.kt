package com.example.littlepainter.ui.fragment.home.draw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.example.littlepainter.ui.fragment.home.draw.shape.ArrowLineShape
import com.example.littlepainter.ui.fragment.home.draw.shape.BezelShape
import com.example.littlepainter.ui.fragment.home.draw.shape.CircleShape
import com.example.littlepainter.ui.fragment.home.draw.shape.EraserShape
import com.example.littlepainter.ui.fragment.home.draw.shape.FreeCurveShape
import com.example.littlepainter.ui.fragment.home.draw.shape.LineShape
import com.example.littlepainter.ui.fragment.home.draw.shape.LocationShape
import com.example.littlepainter.ui.fragment.home.draw.shape.RectangleShape
import com.example.littlepainter.ui.fragment.home.draw.shape.TextShape
import com.example.littlepainter.ui.fragment.home.draw.shape.TriangleShape
import com.example.littlepainter.viewmodel.HomeViewModel

class Layer(val id: Int,val width:Int,val height:Int) {
    //图层对应的canvas对象
    private var mCanvas: Canvas
    private var mBitmap: Bitmap = createBitmap(width, height,Bitmap.Config.ARGB_8888)
    private val mShapes:ArrayList<BaseShape> = arrayListOf()
    private var mLastSelectedShape:BaseShape?=null
    //初始化广播
    private val mIconClickReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (mLastSelectedShape!=null){
                    mLastSelectedShape?.unSelected()
                    mLastSelectedShape=null
                }
            }
        }
    }
    init {
        mCanvas=Canvas(mBitmap)

        val intentFilter= IntentFilter(BroadCastCenter.ICON_CLICK_BROADCAST_NAME)
        //注册广播
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            HomeViewModel.instance().getContext().registerReceiver(mIconClickReceiver,intentFilter,
                Context.RECEIVER_EXPORTED)
        }else{
            ContextCompat.registerReceiver(
                HomeViewModel.instance().getContext(),
                mIconClickReceiver,
                intentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }


    fun onDestroy(){
        HomeViewModel.instance().getContext().unregisterReceiver(mIconClickReceiver)
    }
    //获取当前图层bitmap
    fun getBitmap():Bitmap{
        return mBitmap
    }
    //当手触摸屏幕，且是在绘制图形 添加图形
    fun addShape(type: ShapeType,startX:Float,startY:Float){
        var tShape:BaseShape?=null
        tShape=when(type){
            ShapeType.CIRCLE->{
                CircleShape()//圆形
            }
            ShapeType.RECTANGLE->{
                RectangleShape()//矩形
            }
            ShapeType.LINE->{
                LineShape()//直线
            }
            ShapeType.CURVE->{
                FreeCurveShape()//随意画
            }
            ShapeType.TRIANGLE->{
                TriangleShape()//三角形
            }
            ShapeType.BEZEL->{
                BezelShape()//三次贝塞尔曲线
            }
            ShapeType.ARROW->{
                ArrowLineShape()//箭头
            }
            ShapeType.LOCATION->{
                LocationShape()//坐标系
            }
            ShapeType.TEXT->{
                TextShape()//文本
            }
            ShapeType.ERASER->{
                EraserShape()//橡皮擦
            }
            else->{null}
        }
        tShape?.let {
            it.setStartPoint(startX,startY)
            mShapes.add(it)
        }
    }
    fun addEndPoint(exdX:Float,endY:Float){
        currentShape()?.setEndPoint(exdX,endY)
    }
    fun draw(){
        mBitmap.eraseColor(Color.TRANSPARENT)
        mShapes.forEach { shape->
            shape.draw(mCanvas)
        }
    }
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun undo(){
        if (mShapes.isNotEmpty()){
            mShapes.removeLast()
        }
    }
    fun updateShapeState(state: ShapeState){
        currentShape()?.updateShapeState(state)
    }
    fun clear(){
        mShapes.clear()
    }
    fun updateText(text:String){
        currentShape()?.let {shape->
            if (shape is TextShape){
                shape.updateText(text)
            }
        }
    }
    fun fillColor(x:Float,y:Float){
        for (shape in mShapes.asReversed()){
            if (shape.containsPointInPath(x,y)){
                shape.fillColor()
            }
        }
    }
    fun selectShape(x:Float,y:Float){
        val selectedShape=findSelectedShape(x,y)
        //点击在空白区域
        if (selectedShape==null){
            if (mLastSelectedShape!=null){
                //取消之前选中图形
                mLastSelectedShape?.unSelected()
                mLastSelectedShape=null
            }
        }else{
            if (mLastSelectedShape!=null){
                if (mLastSelectedShape!=selectedShape){
                    //与上一次点击的不是同一个
                    mLastSelectedShape?.unSelected()
                    selectedShape.select()
                    mLastSelectedShape=selectedShape
                }else{
                    //是同一个  拖拉点拽
                    selectedShape.calculateMovePosition(x,y)
                }
            }else{
                //第一次选中
                selectedShape.select()
                mLastSelectedShape=selectedShape
            }
        }
    }
    fun updateMoveMode(isInMoveMode:Boolean){
        mShapes.forEach { shape->
            shape.updateMoveMode(isInMoveMode)
        }
    }

    //获取触摸点的shape
    private fun findSelectedShape(x:Float,y:Float):BaseShape?{
        for (shape in mShapes.asReversed()){
            if (shape.containsPointInRect(x,y)){
                return shape
            }
        }
        //说明点击在空白区域
        return null
    }
    private fun currentShape():BaseShape?{
        if (mLastSelectedShape!=null)return mLastSelectedShape!!
        return if(mShapes.isNotEmpty()){
             mShapes.last()
        }else{
            null
        }
    }
}