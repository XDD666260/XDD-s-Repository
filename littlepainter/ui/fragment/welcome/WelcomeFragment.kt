package com.example.littlepainter.ui.fragment.welcome

import android.animation.Animator
import android.app.Notification.Action
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.littlepainter.R
import com.example.littlepainter.databinding.FragmentWelcomeLayoutBinding
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.ui.fragment.home.file.FileManager
import com.example.littlepainter.utils.PasswordType
import com.example.littlepainter.viewmodel.UserViewModel
import java.util.Date

class WelcomeFragment: BaseFragment<FragmentWelcomeLayoutBinding>() {
    private val viewModel:UserViewModel by activityViewModels()
    private var isAnimatorEnd=false
    override fun initUI(savedInstanceState: Bundle?) {

        viewModel.isFindFinished.observe(viewLifecycleOwner){
            if (isAnimatorEnd){
                navigate()

            }
        }
        mBinding.lottieView.addAnimatorListener(object :Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator) {
            }
            override fun onAnimationEnd(animation: Animator) {
                if (viewModel.isFindFinished.value==true){
                    navigate()
                }else{
                    isAnimatorEnd=true
                }
            }
            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
    private fun insertUser(){
        //viewModel.register("xjm","123","123")
        //viewModel.login("xjm","123",PasswordType.PIN)
        viewModel.changePasswordType(PasswordType.PIC)
    }
    private fun navigate(){
        val user=viewModel.currentUser
        //有用户
        if (user!=null){
            val duration=Date().time-user.loginDate.time
            //过期
            if (duration>user.validate){
                //跳转到pin
                if (user.passwordType==0){
                    //图案登录
                    val extras= FragmentNavigatorExtras(mBinding.animationView to "explode")
                    findNavController().navigate(R.id.action_welcomeFragment_to_pinLoginFragment,null,null,extras)
                }
                //跳转到pic
                else{
                    //密码登录
                    val direction=WelcomeFragmentDirections.actionWelcomeFragmentToPicLoginFragment(false)
                    findNavController().navigate(direction)
                }
            }
            //没过期
            else{
                FileManager.instance.login(user.name)
                findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
                Log.v("xjm","${user.validate}")
                Log.v("xjm","$duration")
            }
        } else{
            findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
        }
    }
}