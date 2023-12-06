package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.databinding.FragmentBookAppointmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BookAppointmentFragment : BaseFragment() {

    lateinit var binding : FragmentBookAppointmentBinding
    private val args : BookAppointmentFragmentArgs by navArgs()
    private var userId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            userId = (activity as DashboardActivity).prefsManager.getUserId().toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.doctorRv.setOnClickListener {
            val appointmentsRef = (activity as DashboardActivity).database.getReference("appointments")

// Create a new appointment
            val newAppointment = Appointment(args.docInfo.userId, userId, "9pm")

// Push the appointment to the database
            val appointmentKey = appointmentsRef.push().key
            appointmentsRef.child(appointmentKey!!).setValue(newAppointment)

        }

    }
}