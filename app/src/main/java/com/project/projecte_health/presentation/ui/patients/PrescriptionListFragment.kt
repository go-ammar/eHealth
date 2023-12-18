package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.data.local.medicines.PrescriptionModel
import com.project.projecte_health.databinding.FragmentPrescriptionListBinding
import com.project.projecte_health.presentation.ui.adapters.PrescriptionListAdapter
import com.project.projecte_health.utils.Utils.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PrescriptionListFragment : BaseFragment() {

    private lateinit var binding: FragmentPrescriptionListBinding
    private var userId : String = ""
    val prescriptions = mutableListOf<PrescriptionModel>()

    private lateinit var upcomingAdapter: PrescriptionListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPrescriptionListBinding.inflate(layoutInflater, container, false)

        lifecycleScope.launch {
            userId = (activity as PrescriptionsActivity).prefsManager.getUserId().toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prescription = PrescriptionModel(
            name = "Brexin",
            details = "1 with water",
            dosage = "Morning and Night",
            doctorName = "Ammar Ahsan",
            doctorId = "yBO5AHAKbIeycpXHC8tazNacScW2",
            userId
        )

//        savePrescription(userId, prescription)
        getPrescriptions(userId)

    }

    fun getPrescriptions(patientId: String) {
        val prescriptionsRef = (activity as PrescriptionsActivity).database.reference.child(patientId).child("prescriptions")

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

            val action = PrescriptionListFragmentDirections.actionPrescriptionListFragmentToPrescriptionDetailsFragment(it)
            findNavController().safeNavigate(action)

        }

        binding.upcomingRv.adapter = upcomingAdapter

        upcomingAdapter.submitList(prescriptions)

    }

    // Save prescription to Firebase
    fun savePrescription(patientId: String, prescription: PrescriptionModel) {

        val prescriptionsRef = (activity as PrescriptionsActivity).database.reference.child(patientId).child("prescriptions")
        val prescriptionKey = prescriptionsRef.push().key
        prescriptionsRef.child(prescriptionKey!!).setValue(prescription)
    }
}