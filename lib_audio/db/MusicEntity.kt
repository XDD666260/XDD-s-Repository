package com.baidu.lib_audio.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_favorite_table")
data class MusicEntity(
    val musicId: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)