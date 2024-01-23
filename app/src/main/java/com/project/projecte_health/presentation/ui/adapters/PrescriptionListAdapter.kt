package com.project.projecte_health.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseAdapter
import com.project.projecte_health.data.local.medicines.PrescriptionModel
import com.project.projecte_health.databinding.ItemPrescriptionBinding

class PrescriptionListAdapter (
    private val onClickListener: (model: PrescriptionModel) -> Unit
) : BaseAdapter<PrescriptionModel>(
    diffCallback = object : DiffUtil.ItemCallback<PrescriptionModel>() {

        override fun areItemsTheSame(oldItem: PrescriptionModel, newItem: PrescriptionModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PrescriptionModel, newItem: PrescriptionModel): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_prescription,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: PrescriptionModel, position: Int) {
        when (binding) {
            is ItemPrescriptionBinding ->
                binding.apply {

                    medicineName.text = item.name

                    detailsTv.text = item.dosage

                    cardView.setOnClickListener { onClickListener.invoke(item) }
                }
        }
    }

}