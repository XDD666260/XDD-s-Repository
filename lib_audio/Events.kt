package com.baidu.lib_audio

import android.R
import com.baidu.lib_audio.db.MusicEntity
import com.baidu.lib_leancloud.model.Music

//音乐开始加载事件
class AudioLoadEvent(val music: Music)

//音乐开始加载事件
class AudioLoadFinishedEvent(val music: Music)

//音乐开始播放事件
class AudioStartEvent()

//音乐暂停播放事件
class AudioPauseEvent()

//音乐播放进度更新事件
class AudioProgressUpdateEvent()

//音乐播放模式切换事件
class AudioPlayModeChangeEvent(val mode: AudioController.PlayMode)

//音乐播放列表更新事件
class AudioPlayListUpdateEvent(val dataList: List<Music>)

//详细页收藏
class AudioFavoriteChangeEvent(val music: MusicEntity, val isFavorite: Boolean)
//音乐播放完毕事件
class AudioOverEvent()
//主页收藏
class AudioFavoriteEvent()