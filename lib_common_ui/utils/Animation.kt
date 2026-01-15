package com.baidu.lib_common_ui.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.RotateAnimation

//淡入动画
fun View.fadeIn(duration: Long = 500) {
    this.animate().alpha(1f).setDuration(duration)
}

//淡出动画
fun View.fadeOut(duration: Long = 500) {
    this.animate().alpha(0f).setDuration(duration)
}

//旋转动画
var bottomViewRotateAnimator: ObjectAnimator? = null
var playViewRotateAnimator: ObjectAnimator? = null

//旋转动画
fun View.startPlayViewRotate() {
    if (playViewRotateAnimator == null) {
        playViewRotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f).apply {
            duration = 10000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
    }
    if (playViewRotateAnimator!!.isPaused) {
        playViewRotateAnimator!!.resume()
    } else {
        playViewRotateAnimator!!.start()
    }
}

fun View.stopPlayViewRotate() {
    playViewRotateAnimator?.let {
        it.pause()
    }
}

fun View.releasePlayViewRotate() {
    playViewRotateAnimator = null
}

//旋转动画
fun View.startBottomViewRotate() {
    if (bottomViewRotateAnimator == null) {
        bottomViewRotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f).apply {
            duration = 10000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
    }
    if (bottomViewRotateAnimator!!.isPaused) {
        bottomViewRotateAnimator!!.resume()
    } else {
        bottomViewRotateAnimator!!.start()
    }
}

fun View.stopBottomViewRotate() {
    bottomViewRotateAnimator?.let {
        it.pause()
    }
}

fun View.releaseBottomViewRotate() {
    bottomViewRotateAnimator = null
}


fun View.rotate45CW() {
    val rotate = RotateAnimation(
        0f,
        30f,
        RotateAnimation.RELATIVE_TO_SELF,
        0f,
        RotateAnimation.RELATIVE_TO_SELF,
        0.1f
    )
    rotate.duration = 500
    rotate.fillAfter = true
    startAnimation(rotate)
}

fun View.rotate45CCW() {
    val rotate = RotateAnimation(
        30f,
        0f,
        RotateAnimation.RELATIVE_TO_SELF,
        0f,
        RotateAnimation.RELATIVE_TO_SELF,
        0.1f
    )
    rotate.duration = 500
    rotate.fillAfter = true
    startAnimation(rotate)
}


