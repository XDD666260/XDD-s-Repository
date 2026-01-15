package com.baidu.lib_audio.fragent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.baidu.lib_leancloud.model.Music

class PlayListViewModel : ViewModel() {
    private val _playList = MutableLiveData<List<Music>>()
    val playList: LiveData<List<Music>> = _playList

    fun setPlayList(dataList: List<Music>) {
        _playList.value = dataList
    }
}