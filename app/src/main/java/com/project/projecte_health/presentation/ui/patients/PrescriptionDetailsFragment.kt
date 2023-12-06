package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.projecte_health.databinding.FragmentPrescriptionDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrescriptionDetailsFragment : Fragment() {

    lateinit var binding: FragmentPrescriptionDetailsBinding
    private val args : PrescriptionDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPrescriptionDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbar.backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            toolbar.headingTitle.text = "Prescription Details"

            doctorTv.text = "Prescribed by ${args.prescription.doctorName}"
            medicineTv.text = "${args.prescription.name}"
            detailsTv.text = "${args.prescription.details}"
            dosage.text = "${args.prescription.dosage}"


        }
    }
}