package com.baidu.lib_common_ui.utils

/**
 * 获取时间对应的 播放时间格式 02:21
 */
fun getPlayTimeString(millionTime: Int): String {
    val min = millionTime / 1000 / 60
    val minStr = if (min < 10) "0$min" else "$min"

    val second = millionTime / 1000 % 60
    val secStr = if (second < 10) "0$second" else "$second"

    return "$minStr:$secStr"
}