package com.baidu.lib_audio.db

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.baidu.lib_leancloud.model.Music

class Repository {
    val api = MusicDataBase.api

    fun getAllMusics(): LiveData<List<MusicEntity>> {
        return api.getAllMusics()
    }

    suspend fun addFavoriteMusic(music: MusicEntity) {
        api.addFavoriteMusic(music)
    }

    suspend fun deleteFavoriteMusic(music: MusicEntity) {
        api.deleteFavoriteMusic(music)
    }
}