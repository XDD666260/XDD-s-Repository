package com.baidu.lib_leancloud

import android.util.Log
import androidx.lifecycle.MutableLiveData
import cn.leancloud.LCObject
import com.baidu.lib_leancloud.model.Advertisement
import com.baidu.lib_leancloud.model.Chart
import com.baidu.lib_leancloud.model.ChartMusicModel
import com.baidu.lib_leancloud.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import kotlin.random.Random

/**
 * 管理LeanCloud中的所有操作
 * 获取广告图 获取榜单 获取榜单中的音乐
 */
class LeanCloudManager private constructor() {
    private var advertisementList: List<Advertisement> = emptyList()
    var chartList: List<Chart> = emptyList()
    var musicList: List<Music> = emptyList()
    private var isChartOk = false
    private var isMusicOK = false
    val chartMusicModelList = MutableLiveData<List<ChartMusicModel>>()

    //合并数据
    private fun mergeChartAndMusic() {
        if (isChartOk && isMusicOK) {
            val chartMusicList = mutableListOf<ChartMusicModel>()
            LeanCloudManager.instance.chartList.forEach { chart ->
                //过滤music中id为chart_id的所有音乐
                val musics = LeanCloudManager.instance.musicList.filter { music ->
                    music.chartId == chart.id
                }
                //创建ChartMusicModel对象
                chartMusicList.add(ChartMusicModel(chart.name, musics))
            }
            //更新数据
            chartMusicModelList.postValue(chartMusicList)
        }
    }

    companion object {
        val instance = LeanCloudManager()
    }

    /*
    查询榜单信息
     */
    fun loadCharts(callback: (LeanCloudManager, List<Chart>) -> Unit = { _, _ -> }) {
        CoroutineScope(Dispatchers.IO).launch {
            val query = LCObject.getQuery(Chart::class.java)
            val results = query.find()
            chartList = results

            isChartOk = true
            mergeChartAndMusic()

            withContext(Dispatchers.Main) {
                callback(this@LeanCloudManager, chartList)
            }
        }
    }

    /**
     * 查询某个榜单下的歌曲
     */
    fun loadAllMusics(callback: (List<Music>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            val query = LCObject.getQuery(Music::class.java)
            musicList = query.find()
            isMusicOK = true
            mergeChartAndMusic()

            withContext(Dispatchers.Main) {
                callback(musicList)
            }
        }
    }

    /**
     * 查询某个榜单下的歌曲
     */
    fun loadMusics(chartId: String, callback: (List<Music>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            val query = LCObject.getQuery(Music::class.java)
            //查询music表中的chart_id为chartId的数据
            query.whereEqualTo("chart_id", chartId)
            val results = query.find()

            withContext(Dispatchers.Main) {
                callback(results)
            }
        }
    }


    /*
    外部通过调用这个方法发起下载的动作
     */
    fun loadAdv(callback: (LeanCloudManager, List<Advertisement>) -> Unit = { _, _ -> }) {
        CoroutineScope(Dispatchers.IO).launch {
            //查询广告表
            //创建查询对象LCQuery
            val query = LCObject.getQuery(Advertisement::class.java)
            val results = query.find()
            advertisementList = results

            withContext(Dispatchers.Main) {
                callback(this@LeanCloudManager, advertisementList)
            }
        }
    }

    /**
     * 获取一个广告对象
     */
    fun getRandomAdv(): Advertisement {
        val index = Random.nextInt(advertisementList.size)
        return advertisementList[index]
    }
}