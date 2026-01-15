package com.example.littlepainter.application

import android.app.Application
import com.example.littlepainter.ui.fragment.home.file.FileManager

//提前配置程序所运行的context
class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        FileManager.init(this)
    }
}