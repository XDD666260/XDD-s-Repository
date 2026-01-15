package com.example.littlepainter.ui.activity

import androidx.activity.viewModels
import com.example.littlepainter.ui.base.BaseActivity
import com.example.littlepainter.databinding.ActivityMainBinding
import com.example.littlepainter.viewmodel.UserViewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel:UserViewModel by viewModels()

    override fun initUI() {

    }
}