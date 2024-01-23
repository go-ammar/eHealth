package com.project.projecte_health.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseAdapter
import com.project.projecte_health.data.local.users.UsersModel
import com.project.projecte_health.databinding.ItemDoctorListBinding

class DoctorListAdapter(
    private val onClickListener: (model: UsersModel) -> Unit
) : BaseAdapter<UsersModel>(
    diffCallback = object : DiffUtil.ItemCallback<UsersModel>() {

        override fun areItemsTheSame(oldItem: UsersModel, newItem: UsersModel): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UsersModel, newItem: UsersModel): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_doctor_list,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: UsersModel, position: Int) {
        when (binding) {
            is ItemDoctorListBinding ->
                binding.apply {

                    doctorName.text = item.name.replaceFirstChar {
                        it.uppercaseChar()
                    }
                    doctorCategory.text = item.speciality
                    rating.text = item.rating.toString()

                    cardView.setOnClickListener {
                        onClickListener.invoke(item)
                    }

                }
        }
    }

}