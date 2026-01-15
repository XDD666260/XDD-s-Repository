package com.baidu.lib_audio.adapter

import android.app.Activity
import com.baidu.lib_audio.AudioController
import com.baidu.lib_audio.AudioHelper
import com.baidu.lib_audio.R
import com.baidu.lib_audio.databinding.MusicItemBinding
import com.baidu.lib_common_ui.base.BaseRecyclerViewAdapter
import com.baidu.lib_common_ui.utils.loadUrl
import com.baidu.lib_leancloud.model.Music

class MusicAdapter : BaseRecyclerViewAdapter<MusicItemBinding, Music>() {
    override fun bindData(
        binding: MusicItemBinding,
        data: Music
    ) {
        binding.image.loadUrl(data.image, cornerRadiusDp = 10)
        binding.title.text = data.title
        binding.singer.text = data.singer


        binding.root.setOnClickListener {
            //播放音乐
            AudioController.instance.playMusic(data)
            //获取view所在的activity
            val activity = binding.root.context as Activity
            //关闭activity
            activity.finish()
        }
    }
}