package com.example.littlepainter.ui.view

import android.content.Context
import android.graphics.drawable.Icon
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.example.littlepainter.model.IconModel
import com.example.littlepainter.utils.IconState
import com.example.littlepainter.utils.IconType
import com.example.littlepainter.utils.ViewUtils
import com.example.littlepainter.viewmodel.HomeViewModel

class IconMenuView(context: Context, attrs:AttributeSet?): LinearLayout(context,attrs) {
    private var iconList:List<IconModel> = emptyList()
    private val defaultWidth=ViewUtils.dp2px(40)
    private val defaultHeight=ViewUtils.dp2px(40)
    private var mWidth=defaultWidth
    private var mHeight=defaultHeight
    private var mCurrentSelectedView:IconTextView?=null
    var iconClickListener:(IconType,IconState?)->Unit={_,_->}

    fun setIcons(icons:List<IconModel>){
        iconList=icons
        //权重总和
        weightSum=icons.size.toFloat()
        gravity=Gravity.CENTER
        icons.forEach { model->
            val circleIconView=CircleIconView(context)
            circleIconView.setIconModel(model)
            circleIconView.clickCallback={iconTextView ->
                dealWithCallback(iconTextView)
            }
            //设置布局参数
            val lp=LayoutParams(mWidth,mHeight)
            lp.weight=1f
            if (orientation== VERTICAL){
                lp.topMargin=ViewUtils.dp2px(8)
            }else{
                lp.marginStart=ViewUtils.dp2px(8)
            }
            addView(circleIconView,lp)
        }
    }

    //取消选中icon状态
    fun resetIconState(){
        if (mCurrentSelectedView==null)return
        mCurrentSelectedView!!.updateIconState(IconState.NORMAL)
        mCurrentSelectedView=null
    }

    private fun dealWithCallback(iconTextView: IconTextView){
        //之前没选中过
        if (mCurrentSelectedView==null){
            iconTextView.updateIconState(IconState.SELECTED)
            mCurrentSelectedView=iconTextView

            iconClickListener(iconTextView.mIconModel!!.type,IconState.SELECTED)
        }else{
            //判断与之前是不是同一个  不是同一个
            if (mCurrentSelectedView!=iconTextView){
                mCurrentSelectedView!!.updateIconState(IconState.NORMAL)
                iconTextView.updateIconState(IconState.SELECTED)
                mCurrentSelectedView=iconTextView
                iconClickListener(iconTextView.mIconModel!!.type,IconState.SELECTED)
            }//是同一个
            else{
                iconTextView.updateIconState(IconState.NORMAL)
                mCurrentSelectedView=null
                iconClickListener(iconTextView.mIconModel!!.type,IconState.NORMAL)
            }
        }
    }
}