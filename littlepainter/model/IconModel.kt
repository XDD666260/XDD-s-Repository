package com.example.littlepainter.model

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.example.littlepainter.R
import com.example.littlepainter.utils.IconState
import com.example.littlepainter.utils.IconType
import kotlinx.parcelize.Parcelize

@Parcelize
data class IconModel(
    val type:IconType,
    @StringRes val iconString:Int,
    var state:IconState=IconState.NORMAL,
    @ColorRes val normalColor:Int= R.color.middle_black,
    @ColorRes val selectColor:Int=R.color.light_blue
) : Parcelable