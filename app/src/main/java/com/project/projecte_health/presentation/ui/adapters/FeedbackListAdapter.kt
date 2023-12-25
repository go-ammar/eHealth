package com.project.projecte_health.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseAdapter
import com.project.projecte_health.data.local.bookings.Feedback
import com.project.projecte_health.data.local.medicines.PrescriptionModel
import com.project.projecte_health.databinding.ItemFeedbackBinding
import com.project.projecte_health.databinding.ItemPrescriptionBinding

class FeedbackListAdapter(
) : BaseAdapter<Feedback>(
    diffCallback = object : DiffUtil.ItemCallback<Feedback>() {

        override fun areItemsTheSame(oldItem: Feedback, newItem: Feedback): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Feedback, newItem: Feedback): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_feedback,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: Feedback, position: Int) {
        when (binding) {
            is ItemFeedbackBinding ->
                binding.apply {

                    detailsTv.text = item.feedback
                    rating.text = item.rating.toString()

                }
        }
    }

}