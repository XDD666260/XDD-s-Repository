package com.baidu.lib_audio.db

import android.provider.MediaStore
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.baidu.lib_audio.AudioHelper
import com.baidu.lib_audio.AudioPlayer
import com.baidu.lib_leancloud.model.Music

@Database(entities = [MusicEntity::class], version = 1)
abstract class MusicDataBase : RoomDatabase() {
    abstract fun getMusicDao(): MusicDao

    companion object {
        val api: MusicDao by lazy {
            val db =
                Room.databaseBuilder(AudioHelper.context!!, MusicDataBase::class.java, "music.db")
                    .build()
            db.getMusicDao()
        }
    }
}