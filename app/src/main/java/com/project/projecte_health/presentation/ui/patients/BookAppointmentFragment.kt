package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.databinding.FragmentBookAppointmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class BookAppointmentFragment : BaseFragment() {

    lateinit var binding: FragmentBookAppointmentBinding
    private val args: BookAppointmentFragmentArgs by navArgs()
    private var userId = ""
    private var userName = ""

    private var selectedDate: Long = 0
    private var selectedStartTime: String = ""
    private var selectedEndTime: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            userId = (activity as DashboardActivity).prefsManager.getUserId().toString()
            userName = (activity as DashboardActivity).prefsManager.getUserName().toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                selectedDate = getTimestamp(year, month, dayOfMonth)
            }

            startTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                selectedStartTime = formatTime(hourOfDay, minute)
            }

            endTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
                selectedEndTime = formatTime(hourOfDay, minute)
            }

            confirmBtn.setOnClickListener {

                createAppointment(
                    userId,
                    args.docInfo.userId,
                    selectedDate,
                    selectedStartTime,
                    selectedEndTime
                )

            }
        }


    }

    private fun createAppointment(
        patientId: String,
        doctorId: String,
        date: Long,
        startTime: String,
        endTime: String
    ) {
        val appointmentRef = database.getReference("appointments")

        val appointment = Appointment(
            patientId = patientId,
            patientName = userName,
            doctorId = doctorId,
            doctorName = args.docInfo.name,
            date = date,
            startTime = startTime,
            endTime = endTime,
            details = binding.descriptionEt.text.toString()
        )

        // Push the appointment to the database
        val appointmentKey = appointmentRef.push().key
        appointmentRef.child(appointmentKey!!).setValue(appointment).addOnSuccessListener {
            // Handle success
            // Optionally, you can navigate to another screen or show a success message
        }
            .addOnFailureListener {
                // Handle failure
                // Optionally, you can show an error message
            }

    }

    private fun getTimestamp(year: Int, month: Int, dayOfMonth: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.timeInMillis
    }

    private fun formatTime(hourOfDay: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)

    }
}