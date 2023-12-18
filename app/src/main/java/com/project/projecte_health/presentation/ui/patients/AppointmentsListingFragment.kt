package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppointmentsListingFragment : BaseFragment() {


    lateinit var binding: FragmentAppointmentsListingBinding
    private lateinit var adapter: AppointmentListAdapter
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentsListingBinding.inflate(layoutInflater, container, false)


        lifecycleScope.launch {
            userId = (activity as AppointmentsActivity).prefsManager.getUserId().toString()
            getAppointmentsForPatient(userId) { appointments ->
                // Use the list of appointments here

                adapter = AppointmentListAdapter {

                }

                adapter.submitList(appointments)

                binding.appointmentsRv.adapter = adapter


            }
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    private fun getAppointmentsForPatient(
        patientId: String,
        callback: (List<Appointment>) -> Unit
    ) {
        val appointmentsQuery: Query = database.reference.child("appointments")
            .orderByChild("patientId")
            .equalTo(patientId)

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