package com.project.projecte_health.presentation.ui.doctors

import android.os.Build
import android.os.Bundle
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
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.databinding.FragmentDoctorAppointmentListBinding
import com.project.projecte_health.presentation.ui.adapters.AppointmentListAdapter
import com.project.projecte_health.utils.Utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DoctorAppointmentListFragment : BaseFragment() {

    private lateinit var binding: FragmentDoctorAppointmentListBinding

    private lateinit var adapter: AppointmentListAdapter
    private lateinit var adapterPast: AppointmentListAdapter

    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoctorAppointmentListBinding.inflate(layoutInflater, container, false)


        lifecycleScope.launch {
            userId = (activity as DoctorAppointmentActivity).prefsManager.getUserId().toString()
            getAppointmentsForPatient(userId, { appointments ->
                // Use the list of appointments here

                adapter = AppointmentListAdapter(true) {

                    val action =
                        DoctorAppointmentListFragmentDirections.actionDoctorAppointmentListFragmentToDoctorAppointmentDetailFragment(
                            it
                        )

                    findNavController().safeNavigate(action)

                }


                //sorting wrt date and time

                val sortedAppointments = appointments.sortedWith(
                    compareBy({ it.date },
                        {
                            SimpleDateFormat(
                                "HH:mm",
                                Locale.US
                            ).parse(it.startTime.toString())
                        })
                )

                adapter.submitList(sortedAppointments)

                binding.appointmentsRv.adapter = adapter
            }, { appointments ->

                adapterPast = AppointmentListAdapter(true) {

                    val action =
                        DoctorAppointmentListFragmentDirections.actionDoctorAppointmentListFragmentToDoctorAppointmentDetailFragment(
                            it,
                            false
                        )

                    findNavController().safeNavigate(action)

                }


                //sorting wrt date and time

                val sortedAppointments = appointments.sortedWith(
                    compareBy({ it.date },
                        {
                            SimpleDateFormat(
                                "HH:mm",
                                Locale.US
                            ).parse(it.startTime.toString())
                        })
                )

                adapterPast.submitList(sortedAppointments)

                binding.pastAppointmentsRv.adapter = adapterPast

            })
        }

        return binding.root

    }

    private fun getAppointmentsForPatient(
        doctorId: String,
        callback: (List<Appointment>) -> Unit,
        callbackPast: (List<Appointment>) -> Unit
    ) {
        val appointmentsQuery: Query = database.reference.child("appointments")
            .orderByChild("doctorId")
            .equalTo(doctorId)

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

                    callback(appointments)
                    callbackPast(appointmentsPast)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                callback(emptyList())
            }
        })
    }

    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }
}