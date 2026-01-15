package com.baidu.lib_audio

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.baidu.lib_common_ui.utils.isVersion26
import com.baidu.lib_common_ui.utils.isVersion33
import com.baidu.lib_leancloud.model.Music
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget

class NotificationHelper {
    private lateinit var mNotification: Notification
    private val mNotificationManager: NotificationManager by lazy {
        AudioHelper.context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    val NOTIFICATION_ID = 0x01
    private val CHANNEL_ID = "music_player_channel_id"
    private val CHANNEL_NAME = "Cloud Music"
    private lateinit var mSmallRemoteViews: RemoteViews
    private lateinit var mBigRemoteViews: RemoteViews

    companion object {
        //接收器接收到广播之后，判断是什么具体事件
        //广播对应的Action
        val ACTION_NAME = "play.music.action.name"

        //发送广播的时候会附带一个Intent
        //在Intent中将不同按钮类型使用key-value的形式存储
        val ACTION__KEY = "action_key"

        val EVENT_START_PAUSE = "event_pause"
        val EVENT_NEXT = "event_next"
        val EVENT_PREVIOUS = "event_previous"
        val EVENT_FAVORITE = "event_favorite"
    }

    //初始化通知
    fun init() {
        //创建一个通知

        //创建通道Channel
        isVersion26(task = {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            mNotificationManager.createNotificationChannel(channel)
        })

        //创建RemoteViews
        initRemoteViews()

        //创建通知
        mNotification = NotificationCompat.Builder(AudioHelper.context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.small)
            .setCustomContentView(mSmallRemoteViews)
            .setCustomBigContentView(mBigRemoteViews)
            .build()
    }

    private fun initRemoteViews() {
        val startPauseIntent = PendingIntent.getBroadcast(
            AudioHelper.context,
            1,
            Intent(ACTION_NAME).apply { putExtra(ACTION__KEY, EVENT_START_PAUSE) },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val nextIntent = PendingIntent.getBroadcast(
            AudioHelper.context,
            2,
            Intent(ACTION_NAME).apply { putExtra(ACTION__KEY, EVENT_NEXT) },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val previousIntent = PendingIntent.getBroadcast(
            AudioHelper.context,
            3,
            Intent(ACTION_NAME).apply { putExtra(ACTION__KEY, EVENT_PREVIOUS) },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val favoriteIntent = PendingIntent.getBroadcast(
            AudioHelper.context,
            4,
            Intent(ACTION_NAME).apply { putExtra(ACTION__KEY, EVENT_FAVORITE) },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        mSmallRemoteViews = RemoteViews(
            AudioHelper.context!!.packageName,
            R.layout.layout_notification_collpase
        ).apply {
            //给每个控件设置需要显示的图片
            setImageViewResource(R.id.next, R.drawable.notification_next)
            setImageViewResource(R.id.previous, R.drawable.notification_previous)
            setImageViewResource(R.id.playOrPause, R.drawable.notification_play)

            //绑定数据
            //bindRemoteViewData(mSmallRemoteViews, music)

            //给每个控件添加事件
            setOnClickPendingIntent(R.id.playOrPause, startPauseIntent)
            setOnClickPendingIntent(R.id.next, nextIntent)
            setOnClickPendingIntent(R.id.previous, previousIntent)
        }

        mBigRemoteViews = RemoteViews(
            AudioHelper.context!!.packageName,
            R.layout.layout_notification_expanded
        ).apply {
            setImageViewResource(R.id.next, R.drawable.notification_next)
            setImageViewResource(R.id.previous, R.drawable.notification_previous)
            setImageViewResource(R.id.playOrPause, R.drawable.notification_play)
            setImageViewResource(R.id.favorite, R.drawable.audio_selected_love)

            //bindRemoteViewData(mBigRemoteViews, music)

            setOnClickPendingIntent(R.id.playOrPause, startPauseIntent)
            setOnClickPendingIntent(R.id.next, nextIntent)
            setOnClickPendingIntent(R.id.previous, previousIntent)
            setOnClickPendingIntent(R.id.favorite, favoriteIntent)
        }
    }

    fun getNotification(): Notification {
        return mNotification
    }

    fun notifyNotification() {
        mNotificationManager.notify(NOTIFICATION_ID, mNotification)
    }

    //更新通知显示的内容
    fun showLoadView(music: Music) {
        //更新就是找到对应的控件设置对应的属性
        bindRemoteViewData(mSmallRemoteViews, music)
        bindRemoteViewData(mBigRemoteViews, music)
        notifyNotification()
    }

    fun showStartView() {
        mSmallRemoteViews.setImageViewResource(R.id.playOrPause, R.drawable.notification_pause)
        mBigRemoteViews.setImageViewResource(R.id.playOrPause, R.drawable.notification_pause)

        notifyNotification()
    }

    fun showPauseView() {
        mSmallRemoteViews.setImageViewResource(R.id.playOrPause, R.drawable.notification_play)
        mBigRemoteViews.setImageViewResource(R.id.playOrPause, R.drawable.notification_play)
        notifyNotification()
    }

    fun showFavoriteView() {

    }

    //将数据绑定的方法抽离出来
    private fun bindRemoteViewData(remoteView: RemoteViews, music: Music) {
        remoteView.apply {
            //歌名
            setTextViewText(R.id.title, music.title)
            //歌手
            setTextViewText(R.id.singer, music.singer)
            //播放图片
            setImageViewResource(R.id.playOrPause, R.drawable.notification_play)
            //图片
            showNotificationImage(remoteView, R.id.image, music.image)
            //收藏
        }
    }

    //给通知添加图片
    private fun showNotificationImage(remoteView: RemoteViews, viewId: Int, url: String) {
        //glide默认提供了Notification图片加载的功能
        isVersion33(task = {
            //url -> bitmap
            //给remoteView 对应viewId设置图片
            //更新
            ContextCompat.checkSelfPermission(
                AudioHelper.context!!,
                Manifest.permission.POST_NOTIFICATIONS
            ).also {
                if (it == PackageManager.PERMISSION_GRANTED) {
                    val notificationTarget = NotificationTarget(
                        AudioHelper.context!!,
                        viewId,
                        remoteView,
                        mNotification,
                        NOTIFICATION_ID
                    )
                    Glide.with(AudioHelper.context!!)
                        .asBitmap()
                        .load(url)
                        .into(notificationTarget)
                }
            }

        })

    }
}