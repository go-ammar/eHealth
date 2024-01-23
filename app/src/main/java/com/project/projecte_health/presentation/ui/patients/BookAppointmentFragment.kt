package com.project.projecte_health.presentation.ui.patients

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.data.local.users.Availability
import com.project.projecte_health.databinding.FragmentBookAppointmentBinding
import com.project.projecte_health.presentation.ui.bottomsheets.BottomSheetSpeciality
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class BookAppointmentFragment : BaseFragment() {

    lateinit var binding: FragmentBookAppointmentBinding
    private val args: BookAppointmentFragmentArgs by navArgs()
    private var userId = ""
    private var userName = ""

    private var selectedDate: Long = 0
    private var daysList: MutableList<String> = ArrayList()
    private var timeList: List<String> = ArrayList()


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
            var lastSelectedCalendar = Calendar.getInstance()

            val docUserId = args.docInfo.userId
            val userRef = database.reference.child("users/$docUserId").child("availability")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Access the value from the DataSnapshot
                        val profileData = snapshot.getValue(Availability::class.java)
                        // Do something with the height
                        if (profileData != null) {
                            // Process the height value

                            daysList = profileData.days.toMutableList()

                            timeList = generateTimeIntervals(profileData.startTime.toString(), profileData.endTime.toString())


                        }
                    } else {
                        // Data does not exist
                        println("Data does not exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle potential errors
                    println("Database error: ${error.message}")
                }
            })



            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val checkCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                // Get the day of the week as a string (e.g., "Monday")
                val selectedDayOfWeekString = SimpleDateFormat("EEEE", Locale.getDefault()).format(checkCalendar.time)

                // Check if the selected day is in the valid days list
                if (selectedDayOfWeekString !in daysList) {
                    // If not in the list, switch back to the last selected date
                    binding.calendarView.date = lastSelectedCalendar.timeInMillis
                } else {
                    // Update lastSelectedCalendar for valid days
                    lastSelectedCalendar = checkCalendar
                    selectedDate = getTimestamp(year, month, dayOfMonth)
                }
            }


            binding.timePicker.setOnClickListener {
                val btmSheetDialogFragment = BottomSheetSpeciality(
                    timeList
                )
                btmSheetDialogFragment.show(
                    childFragmentManager,
                    BottomSheetSpeciality.TAG
                )

                btmSheetDialogFragment.stringData.observe(viewLifecycleOwner) {
                    binding.timePicker.text = it
                }

            }

            confirmBtn.setOnClickListener {

                createAppointment(
                    userId,
                    args.docInfo.userId,
                    selectedDate,
                    binding.timePicker.text.substring(0, 5),
                    binding.timePicker.text.substring(binding.timePicker.text.length - 5)
                )

            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun generateTimeIntervals(startTime: String, endTime: String): List<String> {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val start = LocalTime.parse(startTime, formatter)
        val end = LocalTime.parse(endTime, formatter)

        val intervals = mutableListOf<String>()
        var current = start

        while (current.isBefore(end)) {
            val next = current.plusMinutes(30.toLong())
            intervals.add("${current.format(formatter)}-${next.format(formatter)}")
            current = next
        }

        return intervals
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
            doctorId = doctorId,
            doctorName = args.docInfo.name,
            patientId = patientId,
            patientName = userName,
            date = date,
            startTime = startTime,
            endTime = endTime,
            details = binding.descriptionEt.text.toString(),
            feedbackGiven = false
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

}