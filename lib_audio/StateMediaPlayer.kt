package com.baidu.lib_audio

import android.media.MediaPlayer

/**
 * 拥有状态管理的MediaPlayer
 */
class StateMediaPlayer : MediaPlayer() {
    private var mCompletionCallback: () -> Unit = {}
    var mState = Status.IDLE

    init {
        //监听播放完毕事件
        setOnCompletionListener {
            mState = Status.COMPLETED
            mCompletionCallback()
        }
    }

    fun onCompletionCallback(callback: () -> Unit) {
        mCompletionCallback = callback
    }

    override fun reset() {
        super.reset()
        mState = Status.IDLE
    }

    override fun setDataSource(path: String?) {
        super.setDataSource(path)
        mState = Status.INITIALIZED
    }

    override fun start() {
        super.start()
        mState = Status.STARTED
    }

    override fun pause() {
        super.pause()
        mState = Status.PAUSED
    }

    override fun stop() {
        super.stop()
        mState = Status.STOPPED
    }

    enum class Status {
        IDLE, INITIALIZED, PREPARED, STARTED, PAUSED, STOPPED, COMPLETED
    }
}