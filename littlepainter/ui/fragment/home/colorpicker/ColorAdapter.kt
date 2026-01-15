package com.example.littlepainter.ui.fragment.home.colorpicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.littlepainter.R
import com.example.littlepainter.databinding.ColorItemLayoutBinding
import com.example.littlepainter.viewmodel.HomeViewModel
import java.util.zip.Inflater

class ColorAdapter:RecyclerView.Adapter<ColorAdapter.MyViewHolder>() {
    private var colorList= emptyList<Int>()
    private val TYPE_ADD=888888
    private val TYPE_DELETE=888887
    private val TYPE_NORMAL=888886
    var actionListener:(action:ItemAction,color: Int?)->Unit={action, position ->  }
    inner class MyViewHolder(view:View):RecyclerView.ViewHolder(view){
        private val colorView : View=itemView.findViewById(R.id.colorView)
        fun bind(color:Int,type:Int){
            colorView.setBackgroundColor(color)
            itemView.setOnClickListener {
                when(type){
                    TYPE_ADD->{actionListener(ItemAction.ADD,null)}
                    TYPE_DELETE->{actionListener(ItemAction.DELETE,null)}
                    TYPE_NORMAL-> {
                        HomeViewModel.instance().mColor = color
                        actionListener(ItemAction.NORMAL,color)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        var view:View?=null
        when(viewType){
            TYPE_ADD->{
                view=layoutInflater.inflate(R.layout.color_item_add_layout,parent,false)
                MyViewHolder(view)
            }
            TYPE_DELETE ->{
                view=layoutInflater.inflate(R.layout.color_item_delete_layout,parent,false)
                MyViewHolder(view)
            }
            TYPE_NORMAL ->{
                view=layoutInflater.inflate(R.layout.color_item_layout,parent,false)
                MyViewHolder(view)
            }
        }
        return MyViewHolder(view!!)
    }
    override fun getItemViewType(position: Int): Int {
        return when(position){
            0->TYPE_ADD
            1->TYPE_DELETE
            else->TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return colorList.size+2
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when(position){
            0->holder.bind(0,TYPE_ADD)
            1->holder.bind(0,TYPE_DELETE)
            else->holder.bind(colorList[position-2],TYPE_NORMAL)
        }
    }
    fun setDates(colors:List<Int>){
        colorList=colors
        notifyDataSetChanged()
    }
}