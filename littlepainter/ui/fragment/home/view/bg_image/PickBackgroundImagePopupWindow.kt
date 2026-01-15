package com.example.littlepainter.ui.fragment.home.view.bg_image

import android.content.Context
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.example.littlepainter.R
import com.example.littlepainter.databinding.LayerItemLayoutBinding
import com.example.littlepainter.databinding.PickimagePopupViewLayoutBinding
import com.example.littlepainter.ui.fragment.home.layer.LayerState
import com.example.littlepainter.utils.ViewUtils

class PickBackgroundImagePopupWindow(val context:Context) {
    private var mBinding:PickimagePopupViewLayoutBinding?= null
    private val mImageModelManager=ImageModelManager()
    private val popUpWindow:PopupWindow by lazy {
        val inflater=LayoutInflater.from(context)
        mBinding=PickimagePopupViewLayoutBinding.inflate(inflater)
        initRecycleView()
        PopupWindow(context).apply {
            contentView=mBinding!!.root
            width= LayoutParams.WRAP_CONTENT
            height= LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
    var addImageSelectListener:(Int)->Unit={}

    fun showAsDropDown(parent:View,offsetX:Int,offsetY:Int){
        popUpWindow.showAsDropDown(parent,offsetX,offsetY)
    }
    fun showAtLocation(parent:View,gravity:Int=Gravity.CENTER,offsetX:Int,offsetY:Int){
        popUpWindow.showAtLocation(parent,gravity,offsetX,offsetY)
    }
    fun hide(){
        popUpWindow.dismiss()
    }
    private fun initRecycleView(){
        mBinding?.apply {
            recycleView.linear().setup {
                addType<ImageModel>(R.layout.layer_item_layout)
                onBind {
                    val binding=getBinding<LayerItemLayoutBinding>()
                    val model=getModel<ImageModel>()
                    Glide.with(binding.root)
                        .load(model.id)
                        .into(binding.layerImageView)
                    binding.coverView.visibility=
                        if (model.state==LayerState.SELECTED){
                            View.VISIBLE
                        }else{
                            View.INVISIBLE
                        }
                    //配置点击事件
                    binding.root.setOnClickListener{
                        mImageModelManager.select(model)
                        addImageSelectListener(model.id)
                        refreshRecycleView()
                    }
                }
            }.models=mImageModelManager.getImageModels()
        }
    }
    private fun refreshRecycleView(){
        mBinding!!.recycleView.models=mImageModelManager.getImageModels()
    }
}