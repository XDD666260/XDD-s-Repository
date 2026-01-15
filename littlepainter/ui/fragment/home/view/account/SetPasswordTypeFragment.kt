package com.example.littlepainter.ui.fragment.home.view.account

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.littlepainter.databinding.ChoosePasswordTypeLayoutBinding
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.ui.fragment.home.view.loadingview.LoadingView
import com.example.littlepainter.utils.PasswordType
import com.example.littlepainter.utils.delayTask
import com.example.littlepainter.viewmodel.UserViewModel

class SetPasswordTypeFragment: BaseFragment<ChoosePasswordTypeLayoutBinding>() {
    private val mUserViewModel: UserViewModel by activityViewModels()
    private val mLoadingView:LoadingView by lazy {
        LoadingView(requireContext())
    }
    override fun initUI(savedInstanceState: Bundle?) {

        mBinding.picImage.setOnClickListener {
            mBinding.outLineView.x=mBinding.picImage.x
            mUserViewModel.changePasswordType(PasswordType.PIN)
            startUpdatingAnimation()
        }
        mBinding.pinImage.setOnClickListener {
            mBinding.outLineView.x=mBinding.pinImage.x
            mUserViewModel.changePasswordType(PasswordType.PIC)
            startUpdatingAnimation()
        }
        mBinding.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        delayTask(200){
            if (mUserViewModel.currentUser!!.passwordType==0){
                mBinding.outLineView.x=mBinding.picImage.x
            }else{
                mBinding.outLineView.x=mBinding.pinImage.x
            }
        }
    }
    private fun startUpdatingAnimation(){
        mLoadingView.show(mBinding.root)
        delayTask(200){
            mLoadingView.hide {
                findNavController().navigateUp()
            }
        }
    }
}