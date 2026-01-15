package com.baidu.lib_common_ui.utils

import java.util.Timer
import java.util.TimerTask

class TimerUtil private constructor() {
    private lateinit var timer: Timer

    companion object {
        val instance: TimerUtil = TimerUtil()
        private var mDuration = 1000L
        fun init(duration: Long) {
            mDuration = duration
        }
    }

    fun startTimer(task: () -> Unit) {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                task()
            }
        }, 0, mDuration)
    }

    fun stopTimer() {
        timer.cancel()
    }
}