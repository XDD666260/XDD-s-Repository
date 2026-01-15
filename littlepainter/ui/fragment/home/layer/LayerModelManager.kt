package com.example.littlepainter.ui.fragment.home.layer

import android.util.Log
import com.example.littlepainter.ui.fragment.home.draw.Layer

class LayerModelManager private constructor(){
    private val dataList:ArrayList<LayerModel> = arrayListOf()
    //记录之前选中的图层
    private var mLastSelectedLayerModel:LayerModel?=null
    companion object{
        val instance:LayerModelManager by lazy { LayerModelManager() }
    }
    fun getLayerModels():List<LayerModel>{
        return dataList
    }
    fun addLayer(layer:Layer){
        val layerModel=LayerModel(layer.id,layer.getBitmap(),LayerState.SELECTED)
        mLastSelectedLayerModel?.state=LayerState.NORMAL
        dataList.add(0,layerModel)
        mLastSelectedLayerModel=layerModel
    }
    fun removeLayer(id:Int){
        if (dataList.size==1)return
        dataList.forEachIndexed {index,layer->
            if (layer.id==id){
                //如果当前为选中状态
                if (layer.state==LayerState.SELECTED){
                    //如果删除图层为最后一个
                    if (index==dataList.size-1){
                        mLastSelectedLayerModel=dataList.first()
                        mLastSelectedLayerModel!!.state=LayerState.SELECTED
                    }else{
                        mLastSelectedLayerModel=dataList[index+1]
                        mLastSelectedLayerModel!!.state=LayerState.SELECTED
                    }
                }
                dataList.remove(layer)
                return
            }
        }
    }

    //滑动删除后默认操作哪一个视图
    fun resetCurrentSelected(index:Int){
        //如果删除元素索引值等于删除后数组的长度,则删除的是最后一个元素
        if (index==dataList.size){
            if (dataList.isNotEmpty()){
                mLastSelectedLayerModel=dataList.first()
                mLastSelectedLayerModel!!.state=LayerState.SELECTED
            }else{
                mLastSelectedLayerModel=null
            }
        }else{
            mLastSelectedLayerModel=dataList[index]
            mLastSelectedLayerModel!!.state=LayerState.SELECTED
        }
    }
    //交换图层
    fun switchLayer(source:Int,dest:Int){
        val temp=dataList[source]
        dataList[source]=dataList[dest]
        dataList[dest]=temp
    }
    fun selectLayer(model:LayerModel){
        if (mLastSelectedLayerModel!=model){
            mLastSelectedLayerModel!!.state=LayerState.NORMAL
            model.state=LayerState.SELECTED
            mLastSelectedLayerModel=model
        }
    }
    fun getCurrentLayerId():Int{
        return mLastSelectedLayerModel!!.id
    }
}
