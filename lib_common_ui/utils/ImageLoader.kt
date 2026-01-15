package com.baidu.lib_common_ui.utils

import android.widget.ImageView
import com.baidu.lib_common_ui.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/*
负责图片加载方法
 */
fun ImageView.loadUrl(
    url: String,
    centerCrop: Boolean = false,
    placeholder: Int = 0,
    cornerRadiusDp: Int = 1
) {
    if (centerCrop) {
        Glide.with(this)
            .load(url)
            .placeholder(placeholder)
            .circleCrop()
            .into(this)
    } else {
        Glide.with(this)
            .load(url)
            .placeholder(placeholder)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(dp2px(cornerRadiusDp))))
            .into(this)
    }
}

