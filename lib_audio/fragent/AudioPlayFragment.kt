package com.baidu.lib_audio.fragent

import android.content.Intent
import android.os.Bundle
import com.baidu.lib_audio.AudioController
import com.baidu.lib_audio.AudioFavoriteChangeEvent
import com.baidu.lib_audio.AudioFavoriteEvent
import com.baidu.lib_audio.AudioHelper
import com.baidu.lib_audio.AudioLoadEvent
import com.baidu.lib_audio.AudioLoadFinishedEvent
import com.baidu.lib_audio.AudioOverEvent
import com.baidu.lib_audio.AudioPauseEvent
import com.baidu.lib_audio.AudioPlayModeChangeEvent
import com.baidu.lib_audio.AudioProgressUpdateEvent
import com.baidu.lib_audio.AudioStartEvent
import com.baidu.lib_audio.R
import com.baidu.lib_audio.activity.PlayListActivity
import com.baidu.lib_audio.databinding.FragmentAudioPlayBinding
import com.baidu.lib_common_ui.base.BaseFragment
import com.baidu.lib_common_ui.utils.getPlayTimeString
import com.baidu.lib_common_ui.utils.loadUrl
import com.baidu.lib_common_ui.utils.releasePlayViewRotate
import com.baidu.lib_common_ui.utils.rotate45CCW
import com.baidu.lib_common_ui.utils.rotate45CW
import com.baidu.lib_common_ui.utils.startPlayViewRotate
import com.baidu.lib_common_ui.utils.stopPlayViewRotate
import com.baidu.lib_leancloud.model.Music
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AudioPlayFragment : BaseFragment<FragmentAudioPlayBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)

    }

    
    override fun initUI(savedInstanceState: Bundle?) {
        //初始化当前播放的音乐
        loadMusicInfo(AudioController.Companion.instance.getCurrentPlayingMusic(), true)
        //加载当前音乐时长
        showMusicTime(AudioController.Companion.instance.getCurrentPlayingMusic())
        //加载当前收藏状态
        showFavoriteState(AudioController.Companion.instance.getCurrentPlayingMusic())

        initEvent()
    }

    private fun showFavoriteState(music: Music) {
        if (AudioHelper.sharedFavoriteViewModel!!.isFavorite(music.id)) {
            mBinding.favoriteView.setImageResource(R.drawable.audio_selected_love)
        } else {
            mBinding.favoriteView.setImageResource(R.drawable.audio_love)
        }
    }

    private fun initEvent() {
        //旋转播放按钮
        mBinding.nextView.setOnClickListener {
            AudioController.Companion.instance.next()
        }
        mBinding.previousView.setOnClickListener {
            AudioController.Companion.instance.previous()
        }

        mBinding.playView.setOnClickListener {
            AudioController.Companion.instance.startOrPause()
        }

        //返回按钮事件
        mBinding.closeView.setOnClickListener {
            requireActivity().finish()
        }

        //显示播放列表
        mBinding.playListView.setOnClickListener {
            startActivity(Intent(requireContext(), PlayListActivity::class.java))
        }

        //播放模式切换
        mBinding.modeView.setOnClickListener {
            AudioController.instance.changePlayMode()
        }

        //添加收藏按钮点击事件
        mBinding.favoriteView.setOnClickListener {
            val musicId = AudioController.instance.getCurrentPlayingMusic().id
            AudioHelper.sharedFavoriteViewModel?.changeFavoriteMusic(musicId)
        }

        //进度拖拽事件
        mBinding.progressView.setOnSeekListener(onStart = {
            //暂停播放
            AudioController.Companion.instance.pause()
        }, onStop = { rate ->
            //progress
            //duration
            //rate
            val music = AudioController.Companion.instance.getCurrentPlayingMusic()
            //计算rate对应的时间值
            val seekProgress = (rate * music.duration).toInt()
            //获取音乐应该播放的位置
            val position = music.progress + seekProgress
            //更新播放器
            AudioController.Companion.instance.seekTo(position)

            //开始播放
            AudioController.Companion.instance.start()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        mBinding.playView.releasePlayViewRotate()
    }

    //定义音乐开始加载事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAudioLoadEvent(event: AudioLoadEvent) {
        //获取当前加载的音乐
        loadMusicInfo(event.music)
    }

    //订阅音乐加载完毕事件  可以拿到音乐总时长
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAudioLoadFinishedEvent(event: AudioLoadFinishedEvent) {
        showMusicTime(event.music)
    }

    //定义音乐加载事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAudioStartEvent(event: AudioStartEvent) {
        startMusic()
    }

    //定义音乐加载事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAudioPauseEvent(event: AudioPauseEvent) {
        mBinding.albumView.stopPlayViewRotate()
        mBinding.playView.setImageResource(R.drawable.audio_play)
        mBinding.indicatorView.rotate45CCW()
    }

    //订阅进度更新事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAudioProgressUpdateEvent(event: AudioProgressUpdateEvent) {
        showProgress()
    }

    //订阅播放模式修改事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAudioPlayModeChangeEvent(event: AudioPlayModeChangeEvent) {
        when (event.mode) {
            AudioController.PlayMode.LOOP -> mBinding.modeView.setImageResource(R.drawable.audio_mode_list_cycle)
            AudioController.PlayMode.RANDOM -> mBinding.modeView.setImageResource(R.drawable.audio_mode_random)
            AudioController.PlayMode.REPEAT -> mBinding.modeView.setImageResource(R.drawable.audio_mode_recycle)
        }
    }

    //订阅音乐收藏事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAudioFavoriteChangeEvent(event: AudioFavoriteChangeEvent) {
        if (event.isFavorite) {
            mBinding.favoriteView.setImageResource(R.drawable.audio_selected_love)
        } else {
            mBinding.favoriteView.setImageResource(R.drawable.audio_love)
        }
        EventBus.getDefault().post(AudioFavoriteEvent())
    }
    //定义音乐播放完毕事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioOverEvent(event: AudioOverEvent) {
       //调用下一首
        AudioController.instance.next()
    }

    //显示播放进度时间
    private fun showProgress() {
        val music = AudioController.Companion.instance.getCurrentPlayingMusic()
        //显示当前进度对应的时间
        mBinding.timeStartView.text = getPlayTimeString(music.progress)
        //更新进度条
        mBinding.progressView.updateProgress(music.progress.toFloat() / music.duration)
        //判断是否播完
        if(music.progress.toFloat() / music.duration >= 0.999){
            EventBus.getDefault().post(AudioOverEvent())
        }
    }


    //显示音乐时长
    private fun showMusicTime(music: Music) {
        //显示音乐总时长
        mBinding.timeEndView.text = getPlayTimeString(music.duration)
    }

    //加载音乐信息
    private fun loadMusicInfo(music: Music, chickState: Boolean = false) {
        mBinding.albumView.loadUrl(music.image, true)
        mBinding.titleView.text = music.title
        mBinding.authorView.text = music.singer

        if (chickState) {
            if (AudioController.Companion.instance.isPlaying()) {
                startMusic()
            }
        }
    }

    private fun startMusic() {
        mBinding.albumView.startPlayViewRotate()
        mBinding.playView.setImageResource(R.drawable.audio_stop)
        mBinding.indicatorView.rotate45CW()
    }
}