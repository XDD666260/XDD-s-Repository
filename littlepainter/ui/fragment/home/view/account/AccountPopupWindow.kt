package com.example.littlepainter.ui.fragment.home.view.account

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import androidx.navigation.findNavController
import com.example.littlepainter.R
import com.example.littlepainter.databinding.AccountLayoutBinding
import com.example.littlepainter.ui.fragment.home.HomeFragmentDirections
import com.example.littlepainter.utils.delayTask
import com.example.littlepainter.viewmodel.UserViewModel
import java.util.Date

class AccountPopupWindow(val context: Context,val userViewModel:UserViewModel) {
    private var mBinding:AccountLayoutBinding?= null
    //private var mView:View?=null
    private var tempNavView: View? = null
    private val popUpWindow:PopupWindow by lazy {
        val inflater=LayoutInflater.from(context)
        mBinding=AccountLayoutBinding.inflate(inflater)
        initEvent()
        PopupWindow(context).apply {
            contentView=mBinding!!.root
            width= LayoutParams.WRAP_CONTENT
            height= LayoutParams.WRAP_CONTENT
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
    private fun initUI(){
        val user=userViewModel.currentUser
        if (user==null){
            //没有用户信息
            mBinding!!.coverView.visibility=View.VISIBLE
        }else{
            //有用户
            if (Date().time-user.loginDate.time>user.validate){
                //过期
                mBinding!!.coverView.visibility=View.VISIBLE
            }else{
                //没过期
                mBinding!!.coverView.visibility=View.INVISIBLE
                mBinding!!.tvName.text=user.name
                mBinding!!.tvIconName.text=user.name.first().uppercase()
            }
        }
    }
    private fun initEvent(){
        mBinding!!.coverView.setOnClickListener {
            hide()
            //切换到登录界面
            userViewModel.resetLoginState()
            val direction=HomeFragmentDirections.actionHomeFragmentToPicLoginFragment(true)
            tempNavView?.findNavController()?.navigate(direction)

        }
        mBinding!!.workLayout.setOnClickListener {
            mBinding!!.bgIndicatorView.y=mBinding!!.workLayout.y
            //跳转到我的作品
            tempNavView?.findNavController()?.navigate(R.id.action_homeFragment_to_myWorksFragment)

            hide()
        }
        mBinding!!.settingLayout.setOnClickListener {
            mBinding!!.bgIndicatorView.y=mBinding!!.settingLayout.y
        }
        mBinding!!.logoutLayout.setOnClickListener {
            mBinding!!.bgIndicatorView.y=mBinding!!.logoutLayout.y
            mBinding!!.loadingView.visibility=View.VISIBLE
        }
        mBinding!!.logoffLayout.setOnClickListener {
            mBinding!!.bgIndicatorView.y=mBinding!!.logoffLayout.y
        }
        mBinding!!.loadingView.addAnimatorListener(object :AnimatorListener{
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                delayTask(300){
                    mBinding!!.loadingView.visibility=View.INVISIBLE
                    mBinding!!.coverView.visibility=View.VISIBLE
                    userViewModel.logout()
                }

            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        //设置密码类型
        mBinding!!.settingLayout.setOnClickListener {
            mBinding!!.bgIndicatorView.y=mBinding!!.settingLayout.y
            tempNavView!!.findNavController().navigate(R.id.action_homeFragment_to_setPasswordTypeFragment2)
            hide()
        }
    }

    fun showAsDropDown(parent:View,offsetX:Int=0,offsetY:Int=0){
        tempNavView=parent
        popUpWindow.showAsDropDown(parent,offsetX,offsetY)
        initUI()
    }
    fun showAtLocation(parent:View,gravity:Int=Gravity.CENTER,offsetX:Int,offsetY:Int){
        tempNavView=parent
        popUpWindow.showAtLocation(parent,gravity,offsetX,offsetY)
        initUI()
    }
    fun hide(){
        popUpWindow.dismiss()
    }

}