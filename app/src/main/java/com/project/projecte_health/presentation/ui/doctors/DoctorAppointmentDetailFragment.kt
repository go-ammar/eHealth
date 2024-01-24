package com.project.projecte_health.presentation.ui.doctors

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.bookings.Appointment
import com.project.projecte_health.data.local.medicines.PrescriptionModel
import com.project.projecte_health.data.local.users.ProfileModel
import com.project.projecte_health.databinding.FragmentDoctorAppointmentDetailBinding
import com.project.projecte_health.presentation.ui.adapters.PrescriptionListAdapter
import com.project.projecte_health.presentation.ui.patients.PrescriptionsActivity
import com.project.projecte_health.utils.Utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        if (!args.isInPast){
            binding.prescribedMedicinesTv.visibility = View.GONE
            binding.upcomingRv.visibility = View.GONE
            binding.appointmentsBtn.text = "Delete Appointment"
        }

        binding.apply {
            binding.detailsTv.text = args.appointmentdetail.details
            appointmentTimeTv.text = "Appointment Time: " + args.appointmentdetail.date?.let {
                formatTimestamp(
                    it
                )
            } + "\n" + args.appointmentdetail.startTime + "-" + args.appointmentdetail.endTime


            appointmentsBtn.setOnClickListener {
                if (args.isInPast){
                    val action = DoctorAppointmentDetailFragmentDirections.actionDoctorAppointmentDetailFragmentToDoctorGivePrescriptionFragment(args.appointmentdetail.patientId.toString())
                    findNavController().safeNavigate(action)
                } else {
                   val appointmentsQuery: Query = database.reference.child("appointments")
                    .orderByChild("doctorId")
                        .equalTo(args.appointmentdetail.doctorId)

                    appointmentsQuery.addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (appointmentSnapshot in snapshot.children) {
                                val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                                if (appointment?.patientId == args.appointmentdetail.patientId
                                    && appointment?.date == args.appointmentdetail.date
                                    && appointment?.startTime == args.appointmentdetail.startTime){
                                    appointmentSnapshot.ref.removeValue()
                                        .addOnSuccessListener {
                                            // Deletion successful
                                            val intent = Intent(requireContext(), DoctorsDashboardActivity::class.java)
                                            activity?.startActivity(intent)
                                            activity?.finish()
                                            // You can add your logic here if needed
                                        }
                                        .addOnFailureListener {
                                            // Handle the failure
                                        }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

                }
            }
        }

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

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    private fun getPrescriptions() {
        val prescriptionsRef =
            (activity as DoctorAppointmentActivity).database.reference.child("prescriptions")
                .child(args.appointmentdetail.patientId.toString())


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


    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(timestamp)
        return dateFormat.format(date)
    }
}

data class PM (
    val key: String?="",
    val pres: PrescriptionModel?=null
)