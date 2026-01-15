package com.baidu.cloudmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baidu.cloudmusic.databinding.ChartItemBinding
import com.baidu.cloudmusic.databinding.MusicItemBinding
import com.baidu.lib_audio.AudioController
import com.baidu.lib_audio.AudioFavoriteEvent
import com.baidu.lib_audio.AudioHelper
import com.baidu.lib_audio.AudioLoadEvent
import com.baidu.lib_audio.R
import com.baidu.lib_common_ui.utils.loadUrl
import com.baidu.lib_leancloud.model.ChartMusicModel
import com.baidu.lib_leancloud.model.Music
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MusicAdapter : RecyclerView.Adapter<MusicAdapter.MyViewHolder>() {
    private var musicList = listOf<Music>()
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        EventBus.getDefault().register(this)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflator = LayoutInflater.from(parent.context)

        return MyViewHolder(MusicItemBinding.inflate(inflator, parent, false))
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.binding(musicList[position], musicList)
    }

    override fun getItemCount(): Int {
        return musicList.size
    }


    class MyViewHolder(val binding: MusicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(model: Music, dataList: List<Music>) {
            binding.image.loadUrl(model.image, cornerRadiusDp = 5)
            binding.title.text = model.title
            binding.singer.text = model.singer
            if(AudioHelper.sharedFavoriteViewModel!!.isFavorite(musicId = model.id)){
                binding.favorite.setImageResource(R.drawable.audio_selected_love)
            }else{
                binding.favorite.setImageResource(R.drawable.audio_love)
            }

            //点击榜单中的音乐
            //播放当前这首音乐 并且把播放列表切换为当前榜单
            binding.root.setOnClickListener {
                AudioController.instance.initMusicList(dataList, dataList.indexOf(model))
            }

            //收藏按钮添加点击事件
//            binding.favorite.setOnClickListener {
//                AudioHelper.sharedFavoriteViewModel?.changeFavoriteMusic(model.id)
//            }
        }

    }

    fun setNewData(newData: List<Music>) {
        musicList = newData
        notifyDataSetChanged()
    }

    //定义收藏事件
    @Subscribe
    fun onAudioFavoriteEvent(event: AudioFavoriteEvent) {
        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        EventBus.getDefault().unregister(this)
    }
}