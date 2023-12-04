package com.project.projecte_health.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseAdapter
import com.project.projecte_health.data.local.dashboard.model.CategoryModel
import com.project.projecte_health.data.local.users.model.UsersModel
import com.project.projecte_health.databinding.ActivityDashboardBinding
import com.project.projecte_health.databinding.ItemDoctorCategoryBinding
import com.project.projecte_health.databinding.ItemNearbyDoctorsBinding
import javax.inject.Inject

class CategoryAdapter @Inject constructor(
    private val onClickListener: (model: CategoryModel) -> Unit
) : BaseAdapter<CategoryModel>(
    diffCallback = object : DiffUtil.ItemCallback<CategoryModel>() {

        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_doctor_category,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: CategoryModel, position: Int) {
        when (binding) {
            is ItemDoctorCategoryBinding ->
                binding.apply {
                    doctorName.text = item.category
                    cardView.setOnClickListener { onClickListener.invoke(item) }

                }
        }
    }

}