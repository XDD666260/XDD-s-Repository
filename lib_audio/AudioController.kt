package com.baidu.lib_audio

import android.util.Log
import com.baidu.lib_leancloud.model.Music
import org.greenrobot.eventbus.EventBus
import kotlin.random.Random

class AudioController private constructor() {
    //记录当前播放的列表
    private var mPlayList = emptyList<Music>()

    //播放音乐的对象
    private var mPlayer = AudioPlayer()

    //播放模式
    private var mCurrentPlayModeIndex = 0
    private val mPlayModeList = listOf(PlayMode.LOOP, PlayMode.RANDOM, PlayMode.REPEAT)

    //记录当前播放的音乐的索引值
    var currentIndex = 0

    companion object {
        val instance = AudioController()
    }
    fun setOnCompletionListener(callback: ()->Unit){
        mPlayer.setOnCompletionListener(callback)
    }

    //外部配置音乐播放列表 默认播放第一首
    fun initMusicList(dataList: List<Music>) {
        mPlayList = dataList

        //默认加载播放列表的第一首歌曲
        mPlayer.loadMusic(mPlayList[0])
    }

    //外部配置音乐播放列表 默认播放第一首
    fun initMusicList(dataList: List<Music>, playIndex: Int) {
        mPlayList = dataList
        currentIndex = playIndex
        EventBus.getDefault().post(AudioPlayListUpdateEvent(mPlayList))

        //默认加载播放列表的第一首歌曲
        mPlayer.loadMusic(mPlayList[playIndex])
    }

    //加载音乐
    fun playMusic(music: Music) {
        if (isPlaying()) {
            pause()
        }
        //修改当前音乐的索引值
        currentIndex = mPlayList.indexOf(music)

        mPlayer.loadMusic(music)
    }

    fun getMusicList(): List<Music> {
        return mPlayList
    }

    //获取当前播放的音乐
    fun getCurrentPlayingMusic(): Music {
        return mPlayList[currentIndex]
    }

    // 修改播放模式
    fun changePlayMode() {
        mCurrentPlayModeIndex = (mCurrentPlayModeIndex + 1) % mPlayModeList.size

        //可以发布切换事件
        EventBus.getDefault().post(AudioPlayModeChangeEvent(mPlayModeList[mCurrentPlayModeIndex]))
    }

    //当前是不是播放
    fun isPlaying(): Boolean {
        return mPlayer.isPlaying()
    }

    //手动拖拽进度
    fun seekTo(msc: Int) {
        mPlayer.seekTo(msc)
    }

    fun start() {
        mPlayer.start()
    }

    fun startOrPause() {
        mPlayer.startOrPause()
    }

    fun pause() {
        mPlayer.pause()
    }

    //点击切换下一曲
    fun next() {
        when (mPlayModeList[mCurrentPlayModeIndex]) {
            PlayMode.RANDOM -> {
                currentIndex = Random.nextInt(mPlayList.size)
            }

            PlayMode.REPEAT -> {}
            PlayMode.LOOP -> {
                currentIndex = (currentIndex + 1) % mPlayList.size
            }
        }
        changeMusic()
    }

    //点击切换上一曲
    fun previous() {
        when (mPlayModeList[mCurrentPlayModeIndex]) {
            PlayMode.RANDOM -> {
                currentIndex = Random.nextInt(mPlayList.size)
            }

            PlayMode.REPEAT -> {}
            PlayMode.LOOP -> {
                currentIndex--
                if (currentIndex < 0) {
                    currentIndex = mPlayList.size - 1
                }
            }
        }
        changeMusic()
    }

    //滚动切换下一曲
    fun scrollNext() {
        currentIndex++
        if (currentIndex >= mPlayList.size - 1) {
            currentIndex = mPlayList.size - 1
        }
        changeMusic()
    }

    //滚动切换上一曲
    fun scrollPrevious() {
        currentIndex--
        if (currentIndex < 0) {
            currentIndex = mPlayList.size - 1
        }
        changeMusic()
    }

    //播放模式
    enum class PlayMode {
        RANDOM, //随机播放
        LOOP, //列表循环
        REPEAT //单曲循环
    }

    //切换音乐
    private fun changeMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause()
        }
        mPlayer.loadMusic(mPlayList[currentIndex])
    }
}






