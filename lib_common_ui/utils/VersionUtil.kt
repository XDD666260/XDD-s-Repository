package com.baidu.lib_common_ui.utils

import android.os.Build

fun isVersion29(task: () -> Unit, elseTask: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        task()
    } else {
        elseTask()
    }
}

fun isVersion26(task: () -> Unit, elseTask: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        task()
    } else {
        elseTask()
    }
}

fun isVersion26To33(task: () -> Unit, higherTask: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        task()
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        higherTask()
    }
}

fun isVersion33(task: () -> Unit, elseTask: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        task()
    } else {
        elseTask()
    }
}

fun isVersion31(task: () -> Unit, elseTask: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        task()
    } else {
        elseTask()
    }
}

fun isVersion34(task: () -> Unit, elseTask: () -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        task()
    } else {
        elseTask()
    }
}
