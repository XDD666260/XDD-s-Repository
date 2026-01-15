package com.baidu.lib_leancloud.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.leancloud.LCObject
import cn.leancloud.annotation.LCClassName
import kotlinx.parcelize.Parcelize


@Parcelize
@LCClassName("Music")
class Music() : LCObject(), Parcelable {
    val id: String
        get() {
            return getString("objectId")
        }
    val chartId: String
        get() {
            return getString("chart_id")
        }
    val album: String
        get() {
            return getString("album")
        }
    val image: String
        get() {
            return getString("image")
        }
    val singer: String
        get() {
            return getString("singer")
        }
    val title: String
        get() {
            return getString("title")
        }
    val url: String
        get() {
            return getString("url")
        }

    var duration: Int = 0
    var progress: Int = 0
}