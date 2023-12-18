package com.project.projecte_health.presentation.ui.doctors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.util.Locale

@AndroidEntryPoint
class DoctorAppointmentListFragment : BaseFragment() {

    private lateinit var binding: FragmentDoctorAppointmentListBinding

    private lateinit var adapter: AppointmentListAdapter
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoctorAppointmentListBinding.inflate(layoutInflater, container, false)


        lifecycleScope.launch {
            userId = (activity as DoctorAppointmentActivity).prefsManager.getUserId().toString()
            getAppointmentsForPatient(userId) { appointments ->
                // Use the list of appointments here

                adapter = AppointmentListAdapter (true){

                    val action = DoctorAppointmentListFragmentDirections.actionDoctorAppointmentListFragmentToDoctorAppointmentDetailFragment(
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
            }
        }

        return binding.root

    }

    private fun getAppointmentsForPatient(
        doctorId: String,
        callback: (List<Appointment>) -> Unit
    ) {
        val appointmentsQuery: Query = database.reference.child("appointments")
            .orderByChild("doctorId")
            .equalTo(doctorId)

        appointmentsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointments = mutableListOf<Appointment>()

                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    appointment?.let { appointments.add(it) }
                }

                callback(appointments)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                callback(emptyList())
            }
        })
    }

}