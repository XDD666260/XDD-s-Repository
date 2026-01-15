package com.baidu.lib_audio.fragent

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.baidu.lib_audio.adapter.MusicAdapter
import com.baidu.lib_audio.databinding.FragmentPlayListBinding
import com.baidu.lib_common_ui.base.BaseFragment

class PlayListFragment : BaseFragment<FragmentPlayListBinding>() {
    private val viewModel: PlayListViewModel by activityViewModels()
    private val adapter = MusicAdapter()
    override fun initUI(savedInstanceState: Bundle?) {
        mBinding.recyclerView.adapter = adapter
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        viewModel.playList.observe(viewLifecycleOwner) {
            adapter.setNewData(it)
        }
    }
}