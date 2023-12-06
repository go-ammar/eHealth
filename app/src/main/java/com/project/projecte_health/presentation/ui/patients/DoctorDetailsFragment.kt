package com.project.projecte_health.presentation.ui.patients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.projecte_health.databinding.FragmentDoctorDetailsBinding
import com.project.projecte_health.utils.Utils.safeNavigate


class DoctorDetailsFragment : Fragment() {

    lateinit var binding: FragmentDoctorDetailsBinding
    private val args: DoctorDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoctorDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.headingTitle.text = "Doctor Details"

        val docInfo = args.docInfo
        binding.doctorNameTv.text = docInfo.name.replaceFirstChar {
            it.uppercaseChar()
        }
        binding.doctorTypeTv.text = docInfo.speciality
        binding.addressTv.text = docInfo.address + "\n" + docInfo.postCode



        binding.showOnMapBtn.setOnClickListener {
            val action =
                DoctorDetailsFragmentDirections.actionDoctorDetailsFragmentToDoctorsMapsFragment(
                    docInfo
                )
            findNavController().safeNavigate(action)
        }

        binding.appointmentsBtn.setOnClickListener {
            val action =
                DoctorDetailsFragmentDirections.actionDoctorDetailsFragmentToBookAppointmentFragment(
                    docInfo
                )
            findNavController().safeNavigate(action)
        }

    }
}