package com.example.littlepainter.ui.fragment.password.register

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.littlepainter.R
import com.example.littlepainter.databinding.FragmentPicRegisterBinding
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.utils.LoginRegisterResult
import com.example.littlepainter.viewmodel.UserViewModel


class PicRegisterFragment : BaseFragment<FragmentPicRegisterBinding>() {
    private val viewModel: UserViewModel by activityViewModels()
    private val args:PicRegisterFragmentArgs by navArgs()
    private var mPassword:String?=null

    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)

        viewModel.loginRegisterResult.observe(viewLifecycleOwner){
            if (it is LoginRegisterResult.Success){
                //切换到登陆界面
                viewModel.resetLoginState()
                val direction=PicRegisterFragmentDirections.actionPicRegisterFragmentToPicLoginFragment(true)
                findNavController().navigate(direction)
            }
        }
        mBinding.tvAlert.text="请设置密码图案"
        mBinding.unlockView.addOnPicPathFinishListener { string->
            if (mPassword==null){
                mPassword=string
                true
            }else{
                if (mPassword==string){
                    //密码一致
                    viewModel.register(args.name,args.password,string)
                    true
                }else{
                    mBinding.tvAlert.text="两次密码不一致 请重新设置密码图案"
                    mBinding.unlockView.showError()
                    mPassword=null
                    false
                }
            }
        }
    }
}