package com.example.littlepainter.ui.fragment.home.view.bg_image

import com.example.littlepainter.R
import com.example.littlepainter.ui.fragment.home.layer.LayerState

class ImageModelManager {
    private val mImageModels:ArrayList<ImageModel> = arrayListOf()
    private var mLastSelected:ImageModel?= null
    init {
        mImageModels.add(ImageModel(R.drawable.bg_1))
        mImageModels.add(ImageModel(R.drawable.bg_2))
        mImageModels.add(ImageModel(R.drawable.bg_3))
        mImageModels.add(ImageModel(R.drawable.bg_4))
        mImageModels.add(ImageModel(R.drawable.bg_5))
        mImageModels.add(ImageModel(R.drawable.bg_6))
        mImageModels.add(ImageModel(R.drawable.bg_7))
        mImageModels.add(ImageModel(R.drawable.bg_8))
        mImageModels.add(ImageModel(R.drawable.bg_9))
        mImageModels.add(ImageModel(R.drawable.bg_10))
    }
    fun getImageModels():List<ImageModel>{
        return mImageModels
    }
    fun select(model:ImageModel){
        if (mLastSelected==null){
            //之前没有选中任意一个
            model.state=LayerState.SELECTED
            mLastSelected=model
        }else{
            if (mLastSelected==model)return
            mLastSelected?.state=LayerState.NORMAL
            model.state=LayerState.SELECTED
            mLastSelected=model
        }
    }
}