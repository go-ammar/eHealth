package com.project.projecte_health.presentation.ui.doctors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.data.local.medicines.PrescriptionModel
import com.project.projecte_health.data.local.users.model.ProfileModel
import com.project.projecte_health.databinding.FragmentDoctorAppointmentDetailBinding
import com.project.projecte_health.presentation.ui.adapters.PrescriptionListAdapter
import com.project.projecte_health.presentation.ui.patients.DoctorDetailsFragmentArgs
import com.project.projecte_health.presentation.ui.patients.PrescriptionListFragmentDirections
import com.project.projecte_health.presentation.ui.patients.PrescriptionsActivity
import com.project.projecte_health.utils.Utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoctorAppointmentDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentDoctorAppointmentDetailBinding
    private val args: DoctorAppointmentDetailFragmentArgs by navArgs()
    val prescriptions = mutableListOf<PrescriptionModel>()
    private lateinit var upcomingAdapter: PrescriptionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoctorAppointmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPrescriptions()
        getPatientDetails()

        binding.detailsTv.text = args.appointmentdetail.details

    }

    private fun getPatientDetails() {
        val appointmentsQuery: Query =
            database.reference.child("users").child(args.appointmentdetail.patientId.toString())


        appointmentsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val userInfo = snapshot.getValue(ProfileModel::class.java)

                binding.apply {

                    patientNameTv.text = userInfo?.name.toString()
                    detailsTv.text = userInfo?.name.toString()
                    patientNameTv.text = userInfo?.name.toString()
                    patientNameTv.text = userInfo?.name.toString()
                    patientNameTv.text = userInfo?.name.toString()

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    private fun getPrescriptions() {
        val prescriptionsRef =
            (activity as PrescriptionsActivity).database.reference.child(args.appointmentdetail.patientId.toString())
                .child("prescriptions")

        prescriptionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (prescriptionSnapshot in snapshot.children) {
                    val prescription = prescriptionSnapshot.getValue(PrescriptionModel::class.java)
                    prescription?.let { prescriptions.add(it) }
                }

                setUpcomingAdapter(prescriptions)

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error

            }
        })


    }

    private fun setUpcomingAdapter(prescriptions: MutableList<PrescriptionModel>) {

        upcomingAdapter = PrescriptionListAdapter {

        }

        binding.upcomingRv.adapter = upcomingAdapter

        upcomingAdapter.submitList(prescriptions)

    }
}