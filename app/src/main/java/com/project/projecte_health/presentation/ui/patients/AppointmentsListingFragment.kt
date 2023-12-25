package com.project.projecte_health.presentation.ui.patients

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.databinding.FragmentAppointmentsListingBinding
import com.project.projecte_health.databinding.FragmentDoctorListingBinding
import com.project.projecte_health.presentation.ui.adapters.AppointmentListAdapter
import com.project.projecte_health.presentation.ui.adapters.DoctorListAdapter
import com.project.projecte_health.utils.Utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AppointmentsListingFragment : BaseFragment() {


    lateinit var binding: FragmentAppointmentsListingBinding
    private lateinit var adapter: AppointmentListAdapter
    private lateinit var adapterPast: AppointmentListAdapter
    private var userId = ""
    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            userId = (activity as AppointmentsActivity).prefsManager.getUserId().toString()
            getAppointmentsForPatient(userId, { appointments ->
                // Use the list of appointments here


                adapter = AppointmentListAdapter {

                }

                adapter.submitList(appointments)

                binding.appointmentsRv.adapter = adapter


            }, { appointmentsPast ->

                adapterPast = AppointmentListAdapter {

                    if (it.feedbackGiven == true) {
                        Log.d("TAG", "Feedback not given: show details.")
                    } else {
                        val action =
                            AppointmentsListingFragmentDirections.actionAppointmentsListingFragmentToGiveFeedbackFragment(
                                it
                            )
                        findNavController().safeNavigate(action)
                    }
                }

                adapterPast.submitList(appointmentsPast)

                binding.pastAppointmentsRv.adapter = adapterPast

            })
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentsListingBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }


    private fun getAppointmentsForPatient(
        patientId: String,
        callback: (List<Appointment>) -> Unit,
        callbackPast: (List<Appointment>) -> Unit
    ) {

        val appointmentsQuery: Query = database.reference.child("appointments")
            .orderByChild("patientId")
            .equalTo(patientId)

        appointmentsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointments = mutableListOf<Appointment>()
                val appointmentsPast = mutableListOf<Appointment>()

                val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                val currentDate = Date()
                val currentDateInFormat = dateFormat.format(currentDate)

                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    appointment?.let {

                        val result =
                            it.date?.let { it1 -> formatTimestamp(it1).compareTo(currentDateInFormat) }

                        if (result != null) {
                            when {
                                result < 0 -> {
                                    //if current date is after appointment date (allow feedback only)
                                    appointmentsPast.add(it)
                                }

                                result > 0 -> {
                                    //if current date is before appointment date
                                    appointments.add(it)

                                }

                                else -> {
                                    //check time here
                                    val currentLocalTime: LocalTime = LocalTime.now()
                                    val formatter: DateTimeFormatter =
                                        DateTimeFormatter.ofPattern("kk:mm")
                                    val formattedTime: String = currentLocalTime.format(formatter)

                                    val parsedTime1: LocalTime =
                                        LocalTime.parse(formattedTime, formatter)
                                    val parsedTime2: LocalTime =
                                        LocalTime.parse(it.endTime, formatter)

                                    val resultTime: Int = parsedTime1.compareTo(parsedTime2)

                                    when {
                                        resultTime < 0 -> {
                                            //show here
                                            appointments.add(it)
                                        }

                                        resultTime > 0 -> {
                                            //allow feedback
                                            appointmentsPast.add(it)
                                        }

                                        else -> {
                                            //show here
                                            appointments.add(it)
                                        }
                                    }

                                }
                            }
                        }

                    }
                }

                callback(appointments)
                callbackPast(appointmentsPast)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                callback(emptyList())
            }
        })
    }


}