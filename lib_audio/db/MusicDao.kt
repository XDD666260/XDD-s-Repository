package com.baidu.lib_audio.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.baidu.lib_leancloud.model.Music

@Dao
interface MusicDao {
    @Query("select * from music_favorite_table")
    fun getAllMusics(): LiveData<List<MusicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteMusic(music: MusicEntity)

    @Delete
    suspend fun deleteFavoriteMusic(music: MusicEntity)
}