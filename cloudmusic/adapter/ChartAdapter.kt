package com.baidu.cloudmusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baidu.cloudmusic.databinding.ChartItemBinding
import com.baidu.lib_audio.AudioController
import com.baidu.lib_leancloud.model.ChartMusicModel

class ChartAdapter : RecyclerView.Adapter<ChartAdapter.MyViewHolder>() {
    private var chartMusicList = listOf<ChartMusicModel>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        return MyViewHolder(ChartItemBinding.inflate(inflator, parent, false))
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.binding(chartMusicList[position])
    }

    override fun getItemCount(): Int {
        return chartMusicList.size
    }


    class MyViewHolder(val binding: ChartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(model: ChartMusicModel) {
            binding.chartTitle.text = model.title
            //配置音乐内容的RecyclerView
            binding.chartRecyclerView.apply {
                adapter = MusicAdapter().apply {
                    setNewData(model.musics)
                }
                layoutManager = GridLayoutManager(
                    context,
                    3,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
            }
        }
    }

    fun setNewData(newData: List<ChartMusicModel>) {
        chartMusicList = newData
        notifyDataSetChanged()
    }
}