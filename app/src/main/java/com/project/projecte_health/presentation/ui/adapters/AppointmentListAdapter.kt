package com.project.projecte_health.presentation.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseAdapter
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.databinding.ItemAppointmentListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppointmentListAdapter(
    private val isDoctorLoggedIn: Boolean? = false,
    private val onClickListener: (model: Appointment) -> Unit,
) : BaseAdapter<Appointment>(
    diffCallback = object : DiffUtil.ItemCallback<Appointment>() {

        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_appointment_list,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: Appointment, position: Int) {
        when (binding) {
            is ItemAppointmentListBinding ->
                binding.apply {


                    if (isDoctorLoggedIn == true) {
                        if (item.patientName?.isNotEmpty() == true) {
                            doctorName.text = item.patientName.replaceFirstChar {
                                it.uppercaseChar()
                            }
                        }
                        appointmentTime.text =
                            item.date?.let { formatTimestamp(it) } + " " + item.startTime
                        cardView.setOnClickListener {
                            onClickListener.invoke(item)
                        }

                    } else {
                        if (item.doctorName?.isNotEmpty() == true) {
                            doctorName.text = item.doctorName.replaceFirstChar {
                                it.uppercaseChar()
                            }
                        }

                        appointmentTime.text =
                            item.date?.toLong()?.let { formatTimestamp(it) } + " " + item.startTime
                        cardView.setOnClickListener {
                            onClickListener.invoke(item)
                        }

                    }


                }
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }
}