package com.example.littlepainter.ui.fragment.password.login

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.example.littlepainter.R
import com.example.littlepainter.databinding.FragmentPinLoginBinding
import com.example.littlepainter.ui.base.BaseFragment
import com.example.littlepainter.ui.fragment.home.file.FileManager
import com.example.littlepainter.utils.LoginRegisterResult
import com.example.littlepainter.utils.PasswordType
import com.example.littlepainter.viewmodel.UserViewModel

class PinLoginFragment: BaseFragment<FragmentPinLoginBinding>() {
    private val viewModel: UserViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transInflater=TransitionInflater.from(requireContext())
        sharedElementEnterTransition=transInflater.inflateTransition(android.R.transition.move)
    }

    override fun initUI(savedInstanceState: Bundle?) {
        viewModel.loginRegisterResult.observe(viewLifecycleOwner){result->
            when(result){
                is LoginRegisterResult.Success->{
                    FileManager.instance.login(viewModel.currentUser!!.name)
                    findNavController().navigate(R.id.action_pinLoginFragment_to_homeFragment)
                }
                is LoginRegisterResult.Failure->{
                    Toast.makeText(requireContext(), "${result.type}", Toast.LENGTH_SHORT).show()
                }
                else->{}
            }

        }
        mBinding.unlockView.addOnPicPathFinishListener {
            if (it==viewModel.currentUser?.pinPassword){
                viewModel.login(viewModel.currentUser!!.name,viewModel.currentUser!!.picPassword,PasswordType.PIN)
                true
            }else{
                mBinding.unlockView.showError()
            }
            true
        }
    }
}