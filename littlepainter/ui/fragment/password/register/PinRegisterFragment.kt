package com.example.littlepainter.ui.fragment.password.register

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.littlepainter.databinding.FragmentPinRegisterBinding
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.utils.FragmentType
import com.example.littlepainter.viewmodel.UserViewModel

class PinRegisterFragment : BaseFragment<FragmentPinRegisterBinding>() {
    private val viewModel:UserViewModel by activityViewModels()
    override fun initUI(savedInstanceState: Bundle?) {
        super.initUI(savedInstanceState)
        mBinding.usernameView.addTextChangeListener {
            changeRegisterButtonState()
        }
        mBinding.passwordView.addTextChangeListener {
            changeRegisterButtonState()
        }
        mBinding.passwordAgainView.addTextChangeListener {
            changeRegisterButtonState()
        }
        mBinding.registerButton.setOnClickListener {
            if (mBinding.passwordView.text()!=mBinding.passwordAgainView.text()){
                mBinding.passwordView.showError()
                mBinding.passwordAgainView.showError()
            }else{
                val direction=PinRegisterFragmentDirections.actionPinRegisterFragmentToPicRegisterFragment(
                    mBinding.usernameView.text(),
                    mBinding.passwordView.text()
                )
                findNavController().navigate(direction)
            }
        }
    }
    private fun changeRegisterButtonState(){
        mBinding.registerButton.isEnabled=
            mBinding.usernameView.text().isNotEmpty()&& mBinding.passwordView.text().isNotEmpty() && mBinding.passwordAgainView.text().isNotEmpty()
    }
}