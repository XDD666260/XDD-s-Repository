package com.baidu.lib_audio.db

import android.media.metrics.Event
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.lib_audio.AudioFavoriteChangeEvent
import com.baidu.lib_leancloud.model.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class FavoriteViewModel : ViewModel() {
    private val repository = Repository()
    private var _favoriteMusicList = MutableLiveData(emptyList<MusicEntity>())
    val favoriteMusicList: LiveData<List<MusicEntity>> = _favoriteMusicList
    init {
        repository.getAllMusics().observeForever { favoriteList ->
            // 处理数据变化（如更新 UI 数据）
            _favoriteMusicList.value = favoriteList ?: emptyList()
        }
    }
    fun isFavorite(musicId: String): Boolean {
        if (_favoriteMusicList.value.isEmpty()) {
            return false
        } else {
            _favoriteMusicList.value.forEach { music ->
                if (music.musicId == musicId) {
                    return true
                }
            }
            return false
        }
    }

    fun changeFavoriteMusic(musicId: String) {
        _favoriteMusicList.value?.let { musicList ->
            var exists = false
            musicList.forEach { music ->
                if (music.musicId == musicId) {
                    //取消收藏
                    deleteFavoriteMusic(music)
                    exists = true
                }
            }

            if (!exists) {
                //说明没有这首音乐 需要收藏
                favoriteMusic(MusicEntity(musicId))
            }
        }
    }

    private fun favoriteMusic(music: MusicEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFavoriteMusic(music)
            withContext(Dispatchers.Main) {
                EventBus.getDefault().post(AudioFavoriteChangeEvent(music, true))
            }
        }
    }

    private fun deleteFavoriteMusic(music: MusicEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavoriteMusic(music)
            withContext(Dispatchers.Main) {
                EventBus.getDefault().post(AudioFavoriteChangeEvent(music, false))
            }
        }
    }
}