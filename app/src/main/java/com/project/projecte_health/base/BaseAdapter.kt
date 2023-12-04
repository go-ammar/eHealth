package com.project.projecte_health.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import java.util.concurrent.Executors


abstract class BaseAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, BaseViewHolder>(
    AsyncDifferConfig.Builder(diffCallback)
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(holder.binding, getItem(position), position)
        holder.binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = createBinding(parent, viewType)
        return BaseViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding
    protected abstract fun bind(binding: ViewDataBinding, item: T, position: Int)

}