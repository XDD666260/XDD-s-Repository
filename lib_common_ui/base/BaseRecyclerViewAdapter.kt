package com.baidu.lib_common_ui.base

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseRecyclerViewAdapter<T : ViewBinding, E> :
    RecyclerView.Adapter<BaseRecyclerViewAdapter.MyViewHolder<T>>() {
    private var dataList = listOf<E>()

    private fun createBindingInstance(view: View): T {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val clazz = type as Class<*>
        val layoutInflater = LayoutInflater.from(view.context)
        val inflatedView = clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        ).invoke(null, layoutInflater, view, false) as T

        return inflatedView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder<T> {
        return MyViewHolder(createBindingInstance(parent))
    }

    override fun onBindViewHolder(
        holder: MyViewHolder<T>,
        position: Int
    ) {
        bindData(holder.binding, dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    //需要子类去实现
    abstract fun bindData(binding: T, data: E)

    //给外部设置数据源
    fun setNewData(newData: List<E>) {
        dataList = newData
        //val result = DiffUtil.calculateDiff(MyDiffCallbackImpl<E>(dataList, newData))
        //result.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    class MyViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

    class MyDiffCallbackImpl<E>(val newData: List<E>, val oldData: List<E>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldData.size
        }

        override fun getNewListSize(): Int {
            return newData.size
        }

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return oldData[oldItemPosition] === newData[newItemPosition]
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            return oldData[oldItemPosition] == newData[newItemPosition]
        }
    }
}