package com.baidu.lib_common_ui.utils

import android.content.res.Resources

fun dp2px(dp: Int): Int {
    return (Resources.getSystem().displayMetrics.density * dp).toInt()
}

fun dp2pxF(dp: Int): Float {
    return Resources.getSystem().displayMetrics.density * dp
}