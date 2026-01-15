package com.example.littlepainter.ui.fragment.home.draw

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.littlepainter.ui.fragment.home.layer.LayerModelManager

class LayerManager {
    private val layers:ArrayList<Layer> = arrayListOf()

    fun addLayer(width:Int,height:Int){
        Layer(layers.size+1,width,height).apply {
            layers.add(0,this)
            LayerModelManager.instance.addLayer(this)
        }
    }
    fun removeLayer(id:Int):Boolean{
        layers.forEach {
            if (it.id==id){
                it.onDestroy()
                layers.remove(it)
                //LayerModelManager.instance.removeLayer(id)
                return true
            }
        }
        return false
    }

    //交换图层
    fun switchLayer(from:Int,target:Int){
        val temp=layers[from]
        layers[from]=layers[target]
        layers[target]=temp
    }
    //获取最上层图层
    fun getCurrentLayer():Layer?{
        layers.forEach { layer->
            if (LayerModelManager.instance.getCurrentLayerId()==layer.id){
                return layer
            }
        }
        return null
    }
    fun getLayers():List<Layer>{
        return layers
    }
    fun getLayersBitmap():List<Bitmap>{
        val bitmapList= arrayListOf<Bitmap>()
        for (layer in layers.asReversed()){
            bitmapList.add(layer.getBitmap())
        }
        return bitmapList
    }

    fun addShape(type: ShapeType,startX:Float,startY:Float){
       getCurrentLayer()?.addShape(type,startX,startY)
    }
    fun addEndPoint(exdX:Float,endY:Float){
        getCurrentLayer()?.addEndPoint(exdX,endY)
    }
    fun updateMoveMode(isInMoveMode:Boolean){
        getCurrentLayer()?.updateMoveMode(isInMoveMode)
    }
    fun draw(){
        for (layer in layers.asReversed()){
            layer.draw()
        }
    }
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun undo(){
        getCurrentLayer()?.undo()
    }
    fun clearLayer(){
        getCurrentLayer()?.clear()
    }

    //填充颜色
    fun fillColor(x:Float,y:Float){
        getCurrentLayer()?.fillColor(x,y)
    }
    //选中图形
    fun selectShape(x:Float,y:Float){
        getCurrentLayer()?.selectShape(x,y)
    }
    fun updateText(text:String){
        getCurrentLayer()!!.updateText(text)
    }
    fun updateShapeState(state: ShapeState){
        getCurrentLayer()?.updateShapeState(state)
    }
    private fun getLayerWithId(id:Int):Layer?{
        layers.forEach {
            if (it.id==id){
                layers.remove(it)
                return it
            }
        }
        return null
    }
}