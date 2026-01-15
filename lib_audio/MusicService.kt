package com.baidu.lib_audio

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.baidu.lib_common_ui.utils.isVersion26To33
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@SuppressLint("UnspecifiedRegisterReceiverFlag")
class MusicService : Service() {
    //创建NotificationHelper对象
    private val mNotificationHelper = NotificationHelper()
    private val mReceiver: BroadcastReceiverImpl = BroadcastReceiverImpl()

    override fun onCreate() {
        super.onCreate()
        //订阅EventBus事件
        EventBus.getDefault().register(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initNotification()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        //注销通知
        unregisterReceiver(mReceiver)
        //取消EventBus的订阅
        EventBus.getDefault().unregister(this)
    }

    private fun initNotification() {
        mNotificationHelper.init()

        //绑定为前台服务
        startForeground(mNotificationHelper.NOTIFICATION_ID, mNotificationHelper.getNotification())

        //注册通知
        isVersion26To33(task = {
            registerReceiver(
                mReceiver,
                IntentFilter(NotificationHelper.ACTION_NAME)
            )
        }, higherTask = {
            registerReceiver(
                mReceiver,
                IntentFilter(NotificationHelper.ACTION_NAME),
                RECEIVER_EXPORTED
            )
        })

        //推送通知
        //mNotificationHelper.notifyNotification()
    }

    //订阅EventBus事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAudioLoadEvent(event: AudioLoadEvent) {
        //更新通知显示新的音乐
        mNotificationHelper.showLoadView(AudioController.instance.getCurrentPlayingMusic())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioStartEvent(event: AudioStartEvent) {
        //更新通知显示新的音乐
        mNotificationHelper.showStartView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioPauseEvent(event: AudioPauseEvent) {
        //更新通知显示新的音乐
        mNotificationHelper.showPauseView()
    }

    class BroadcastReceiverImpl : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == NotificationHelper.ACTION_NAME) {
                    //获取对应的操作
                    val eventType = it.getStringExtra(NotificationHelper.ACTION__KEY)
                    when (eventType) {
                        NotificationHelper.EVENT_START_PAUSE -> {
                            AudioController.instance.startOrPause()
                        }

                        NotificationHelper.EVENT_NEXT -> {
                            AudioController.instance.next()
                        }

                        NotificationHelper.EVENT_PREVIOUS -> {
                            AudioController.instance.previous()
                        }

                        NotificationHelper.EVENT_FAVORITE -> {

                        }
                    }
                }
            }
        }
    }
}






