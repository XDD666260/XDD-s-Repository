package com.baidu.lib_leancloud.model

/*
保存每个榜单以及榜单下的所有音乐信息
 */
data class ChartMusicModel(val title: String, val musics: List<Music>)