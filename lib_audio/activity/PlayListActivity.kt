package com.baidu.lib_audio.activity

import androidx.activity.viewModels
import com.baidu.lib_audio.AudioController
import com.baidu.lib_audio.R
import com.baidu.lib_audio.databinding.ActivityPlayListBinding
import com.baidu.lib_audio.fragent.PlayListViewModel
import com.baidu.lib_common_ui.base.BaseActivity
import com.baidu.lib_common_ui.utils.isVersion34
import com.baidu.lib_leancloud.model.Music

class PlayListActivity : BaseActivity<ActivityPlayListBinding>() {
    private val viewModel: PlayListViewModel by viewModels()
    private var musicList = emptyList<Music>()

    companion object {
        const val MUSIC_KEY = "play_list_music_key"
    }

    override fun initUI() {
        initActivityTransitionAnimation()


        viewModel.setPlayList(AudioController.Companion.instance.getMusicList())
    }

    private fun initActivityTransitionAnimation() {
        isVersion34(task = {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.bottom_in,
                0
            )

            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                0,
                R.anim.bottom_out
            )
        }, elseTask = {
            overridePendingTransition(R.anim.bottom_in, 0)
        })
    }
}