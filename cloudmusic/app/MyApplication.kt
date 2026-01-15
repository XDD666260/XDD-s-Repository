package com.baidu.cloudmusic.app

import android.app.Application
import cn.leancloud.LCObject
import cn.leancloud.LeanCloud
import com.baidu.lib_audio.AudioHelper
import com.baidu.lib_leancloud.LeanCloud_AppID
import com.baidu.lib_leancloud.LeanCloud_AppKey
import com.baidu.lib_leancloud.LeanCloud_Server_URL
import com.baidu.lib_leancloud.model.Advertisement
import com.baidu.lib_leancloud.model.Chart
import com.baidu.lib_leancloud.model.Music

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //注册LeanCloud中自定义的类
        LCObject.registerSubclass(Advertisement::class.java)
        LCObject.registerSubclass(Chart::class.java)
        LCObject.registerSubclass(Music::class.java)

        //三方库的初始化动作
        LeanCloud.initialize(
            this,
            LeanCloud_AppID,
            LeanCloud_AppKey,
            LeanCloud_Server_URL
        )

        //配置音乐播放需要的全局Context
        AudioHelper.init(this)
    }
}