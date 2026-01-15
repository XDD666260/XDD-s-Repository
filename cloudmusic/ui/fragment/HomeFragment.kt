package com.baidu.cloudmusic.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.baidu.cloudmusic.R
import com.baidu.cloudmusic.adapter.ChartAdapter
import com.baidu.cloudmusic.databinding.FragmentHomeBinding
import com.baidu.cloudmusic.viewmodel.MainViewModel
import com.baidu.lib_common_ui.base.BaseFragment
import com.baidu.lib_leancloud.LeanCloudManager

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val mAdapter = ChartAdapter()

    override fun initUI(savedInstanceState: Bundle?) {
        LeanCloudManager.instance.chartMusicModelList.observe(viewLifecycleOwner) {
            //更新榜单列表
            mAdapter.setNewData(it)
        }

        initRecyclerView()
    }

    //初始化recyclerView
    private fun initRecyclerView() {
        mBinding.recyclerView.adapter = mAdapter
        mBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}