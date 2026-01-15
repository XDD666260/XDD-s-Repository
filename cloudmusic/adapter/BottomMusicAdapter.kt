package com.baidu.cloudmusic.adapter

import android.app.Activity
import android.content.Intent
import com.baidu.cloudmusic.R
import com.baidu.cloudmusic.databinding.BottomMusicItemBinding
import com.baidu.cloudmusic.ui.activity.MusicPlayerActivity
import com.baidu.lib_common_ui.base.BaseRecyclerViewAdapter
import com.baidu.lib_common_ui.utils.isVersion34
import com.baidu.lib_leancloud.model.Music

class BottomMusicAdapter : BaseRecyclerViewAdapter<BottomMusicItemBinding, Music>() {
    override fun bindData(
        binding: BottomMusicItemBinding,
        data: Music
    ) {
        binding.title.text = data.title
        binding.singer.text = data.singer
        //添加点击事件
        binding.root.setOnClickListener {
            //切换到播放页面
            binding.root.context.startActivity(
                Intent(
                    binding.root.context,
                    MusicPlayerActivity::class.java
                )
            )
        }

    }
}