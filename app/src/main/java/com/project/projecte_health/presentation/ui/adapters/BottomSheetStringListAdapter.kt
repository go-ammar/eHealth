package com.project.projecte_health.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseAdapter
import com.project.projecte_health.databinding.ItemStringListBinding

class BottomSheetStringListAdapter(private val onClickListener: (text: String) -> Unit) :
    BaseAdapter<String>(
        diffCallback = object : DiffUtil.ItemCallback<String>() {

            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean {
                return oldItem == newItem
            }

        }) {


    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_string_list,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: String, position: Int) {
        when (binding) {
            is ItemStringListBinding -> {

                binding.text.text = item
                binding.item.setOnClickListener { onClickListener.invoke(item) }

            }
        }
    }


}