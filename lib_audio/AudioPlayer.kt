package com.baidu.lib_audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.PowerManager
import com.baidu.lib_common_ui.utils.TimerUtil
import com.baidu.lib_common_ui.utils.isVersion26
import com.baidu.lib_common_ui.utils.isVersion29
import com.baidu.lib_leancloud.model.Music
import org.greenrobot.eventbus.EventBus

class AudioPlayer {
    private val mPlayer = StateMediaPlayer()
    private val mWifiLock: WifiManager.WifiLock by lazy {
        //拿到系统的WifiManager
        val wifiManager =
            AudioHelper.context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager

        var wifiLock: WifiManager.WifiLock? = null
        //创建wifilock
        isVersion29(
            task = {
                wifiLock = wifiManager.createWifiLock(
                    WifiManager.WIFI_MODE_FULL_LOW_LATENCY,
                    "AudioPlayer"
                )
            },
            elseTask = {
                wifiLock =
                    wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "AudioPlayer")
            }
        )

        wifiLock!!
    }
    private val mAudioManager: AudioManager by lazy {
        AudioHelper.context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    private lateinit var mFocusChangeListener: AudioManager.OnAudioFocusChangeListener
    private var mFocusRequest: AudioFocusRequest? = null
    private var mCompletionListener: () -> Unit = {}
    var mMusic: Music? = null


    init {
        //对player对象配置基本属性
        //设置唤醒模式 保持CPU运行，但允许屏幕关闭
        mPlayer.setWakeMode(AudioHelper.context, PowerManager.PARTIAL_WAKE_LOCK)

        //监听播放完毕的事件
        mPlayer.onCompletionCallback {
            //音乐播放完毕 释放wifi锁
            mWifiLock.release()

            //释放音频焦点
            isVersion26(task = {
                mAudioManager.abandonAudioFocusRequest(mFocusRequest!!)
            }, elseTask = {
                mAudioManager.abandonAudioFocus(mFocusChangeListener)
            })

            mCompletionListener()
        }

        //配置进度更新间隔
        TimerUtil.init(200)
    }

    //监听音乐播放完毕事件
    fun setOnCompletionListener(listener: () -> Unit) {
        mCompletionListener = listener
    }

    //加载音乐
    fun loadMusic(music: Music) {
        
        mMusic = music

        //获取wifi锁
        mWifiLock.acquire()

        //请求音频焦点
        initAudioFocusManager()

        //初始化播放器
        mPlayer.reset()
        mPlayer.setDataSource(music.url)
        mPlayer.prepareAsync()
        mPlayer.setOnPreparedListener {
            mMusic?.duration = mPlayer.duration
            EventBus.getDefault().post(AudioLoadFinishedEvent(music))
            start()
        }
        //使用EventBus将音乐加载的事件发布出去
        EventBus.getDefault().post(AudioLoadEvent(music))
    }

    fun isPlaying(): Boolean {
        return mPlayer.isPlaying
    }

    fun seekTo(msec: Int) {
        isVersion26(task = {
            mPlayer.seekTo(msec.toLong(), MediaPlayer.SEEK_CLOSEST)
        }, elseTask = {
            mPlayer.seekTo(msec)
        })

    }

    fun startOrPause() {
        if (mPlayer.isPlaying) {
            pause()
        } else {
            start()
        }
    }

    //开始播放
    fun start() {
        //音乐初始化完毕 且 没有播放
        mPlayer.start()

        //更新进度
        startUpdateProgress()

        //使用EventBus将音乐播放的事件发布出去
        EventBus.getDefault().post(AudioStartEvent())
    }

    //暂停播放
    fun pause() {
        mPlayer.pause()

        //暂停进度更新
        stopUpdateProgress()

        //使用EventBus将音乐暂停的事件发布出去
        EventBus.getDefault().post(AudioPauseEvent())
    }

    //释放
    fun release() {
        mWifiLock.release()
    }

    //开始更新进度
    private fun startUpdateProgress() {
        //开始进度切换的定时器
        TimerUtil.instance.startTimer {
            //获取当前播放的进度
            mMusic?.progress = mPlayer.currentPosition
            //更新进度
            EventBus.getDefault().post(AudioProgressUpdateEvent())
        }
    }

    //暂停更新进度
    private fun stopUpdateProgress() {
        TimerUtil.instance.stopTimer()
    }

    //配置音频焦点
    private fun initAudioFocusManager() {
        //请求音频焦点
        //音频属性
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        //创建监听器对象
        mFocusChangeListener = getAudioFocusChangeListener()

        //请求音频焦点
        isVersion26(task = {
            mFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(mFocusChangeListener)
                .build()
            mAudioManager.requestAudioFocus(mFocusRequest!!)
        }, elseTask = {
            mAudioManager.requestAudioFocus(
                mFocusChangeListener, //焦点变化的监听器
                AudioManager.STREAM_MUSIC, //音频请求是用来播放什么类型音乐
                AudioManager.AUDIOFOCUS_GAIN //请求时长
            )
        })
    }

    //获取焦点变化的监听器对象
    private fun getAudioFocusChangeListener(): AudioManager.OnAudioFocusChangeListener {
        return object : AudioManager.OnAudioFocusChangeListener {
            override fun onAudioFocusChange(focusChange: Int) {
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> {//获取到焦点了
                    }

                    AudioManager.AUDIOFOCUS_LOSS -> {//失去焦点
                    }

                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {//暂时失去焦点
                    }

                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {//瞬间失去焦点，但可以降低音量
                    }
                }
            }
        }
    }


}