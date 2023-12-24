package com.project.projecte_health.presentation.ui.doctors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.medicines.PrescriptionModel
import com.project.projecte_health.databinding.FragmentDoctorGivePrescriptionBinding
import com.project.projecte_health.databinding.FragmentDoctorsHomeBinding
import com.project.projecte_health.presentation.ui.patients.PrescriptionsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DoctorGivePrescriptionFragment : BaseFragment() {


    private lateinit var binding: FragmentDoctorGivePrescriptionBinding
    private val args: DoctorGivePrescriptionFragmentArgs by navArgs()
    private var userId = ""
    private var userName = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoctorGivePrescriptionBinding.inflate(layoutInflater, container, false)
        lifecycleScope.launch {
            userId = (activity as DoctorAppointmentActivity).prefsManager.getUserId().toString()
            userName = (activity as DoctorAppointmentActivity).prefsManager.getUserName().toString()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.nextBtn.setOnClickListener {

            val prescription = PrescriptionModel(
                name = binding.medicineEt.text.toString(),
                details = binding.detailsEt.text.toString(),
                dosage = binding.dosageEt.text.toString(),
                doctorName = userName,
                doctorId = userId,
                patientId = args.patientId
            )

            savePrescription(args.patientId, prescription)

        }
    }

    private fun savePrescription(patientId: String, prescription: PrescriptionModel) {

        val prescriptionsRef =
            (activity as PrescriptionsActivity).database.reference.child(patientId)
                .child("prescriptions")
        val prescriptionKey = prescriptionsRef.push().key
        prescriptionsRef.child(prescriptionKey!!).setValue(prescription)
    }

}