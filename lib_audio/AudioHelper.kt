package com.baidu.lib_audio

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.baidu.lib_audio.db.FavoriteViewModel

/**
 * 音频播放的帮助类
 * 接收一个Context作为全局context
 */
class AudioHelper private constructor() {

    companion object {
        var sharedFavoriteViewModel: FavoriteViewModel? = null
        var context: Context? = null
        
        fun init(context: Context) {
            this.context = context
        }

        fun initViewModel(owner: ViewModelStoreOwner) {
            sharedFavoriteViewModel = ViewModelProvider(owner).get(FavoriteViewModel::class)
        }
    }
}