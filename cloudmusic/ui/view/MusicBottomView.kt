package com.baidu.cloudmusic.ui.view

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.baidu.cloudmusic.R
import com.baidu.cloudmusic.adapter.BottomMusicAdapter
import com.baidu.cloudmusic.databinding.MusicBottomViewBinding
import com.baidu.lib_audio.activity.PlayListActivity
import com.baidu.lib_audio.AudioController
import com.baidu.lib_audio.AudioLoadEvent
import com.baidu.lib_audio.AudioPauseEvent
import com.baidu.lib_audio.AudioPlayListUpdateEvent
import com.baidu.lib_audio.AudioStartEvent
import com.baidu.lib_common_ui.utils.loadUrl
import com.baidu.lib_common_ui.utils.startBottomViewRotate
import com.baidu.lib_common_ui.utils.stopBottomViewRotate
import com.baidu.lib_leancloud.model.Music
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MusicBottomView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private lateinit var mBinding: MusicBottomViewBinding
    private var mPlayList: List<Music> = emptyList()
    private val mAdapter = BottomMusicAdapter()
    private var isSwitched = false
    private var shouldScrollChangeMusic = true
    private var isScrolling = false

    init {
        initView()
        initEvent()
        initRecyclerView()

        //注册EventBus
        EventBus.getDefault().register(this)
    }


    //订阅音乐列表更新事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioPlayListUpdateEvent(event: AudioPlayListUpdateEvent) {
        //修改当前播放列表
        mPlayList = event.dataList

        //更新RecyclerView中的数据
        mAdapter.setNewData(mPlayList)
    }

    //定义音乐加载事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioLoadEvent(event: AudioLoadEvent) {
        //获取当前加载的音乐
        val music = event.music

        //获取当前这首音乐在列表中的索引值
        val position = mPlayList.indexOf(music)

        Log.v("pxd", "position: $position")

        //加载音乐图片
        mBinding.ivImage.loadUrl(mPlayList[position].image, true, R.drawable.smile)
        //若是代码滚动改为滚动不触发下一首
        shouldScrollChangeMusic = false
        //让RecyclerView滚动到这首音乐的位置
        mBinding.recyclerView.scrollToPosition(position)
        //改回可滚动
        shouldScrollChangeMusic = true

    }

    //定义音乐加载事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioStartEvent(event: AudioStartEvent) {
        //修改播放按钮的图片
        mBinding.btnPlay.setImageResource(R.drawable.audio_stop)

        //音乐唱片开始旋转
        mBinding.ivImage.startBottomViewRotate()
    }

    //定义音乐加载事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioPauseEvent(event: AudioPauseEvent) {
        //修改播放按钮的图片
        mBinding.btnPlay.setImageResource(R.drawable.audio_play)

        //音乐唱片开始旋转
        mBinding.ivImage.stopBottomViewRotate()
    }


    //外部设置播放列表
    fun setPlayList(list: List<Music>) {
        mPlayList = list
        //更新recyclerView的数据源
        mAdapter.setNewData(list)
        //配置AudioController中的播放列表
        AudioController.instance.initMusicList(mPlayList)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //取消注册EventBus
        EventBus.getDefault().unregister(this)
    }

    //初始化各种点击或者滚动的事件
    private fun initEvent() {
        mBinding.btnPlay.setOnClickListener {
            AudioController.instance.startOrPause()
        }

        //播放列表按钮被点击
        mBinding.btnPlayList.setOnClickListener {
            val intent = Intent(context, PlayListActivity::class.java)
            context.startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        //初始化RecyclerView
        mBinding.recyclerView.adapter = mAdapter
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        PagerSnapHelper().attachToRecyclerView(mBinding.recyclerView)

        //监听当前显示的是第几个item
        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //获取当前显示的item
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstVisibleItemPosition()

                if (AudioController.instance.currentIndex == position) return
                if (shouldScrollChangeMusic && !isSwitched) {
                    //通过判断dx的值来切换歌曲
                    //dx > 0：下一曲  dx < 0: 上一曲
                    if (dx > 0) {
                        AudioController.instance.scrollNext()
                        isSwitched = true
                    } else if (dx < -100) {
                        AudioController.instance.scrollPrevious()
                        isSwitched = true
                    }else{
                        isSwitched = false
                        AudioController.instance.scrollPrevious()
                        AudioController.instance.scrollNext()
                    }

                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    // 1. 滑动开始前（手指刚接触并准备拖动）
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        if (!isScrolling) { // 防止重复触发
                            isSwitched = false // 滑动开始前重置
                            isScrolling = true
                        }
                    }
                    // 2. 滑动结束后（完全停止）
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        isSwitched = false // 滑动结束后重置
                        isScrolling = false
                    }
                    // 3. 惯性滑动中（手指已离开，但列表还在动）
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        // 无需重置，保持标记状态直到滑动结束

                    }
                }
            }
        })
    }

    private fun initView() {
        //解析layout中的布局
        mBinding = MusicBottomViewBinding.inflate(LayoutInflater.from(context))
        //创建布局参数
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        //添加到当前这个view group上
        addView(mBinding.root, lp)
    }
}















