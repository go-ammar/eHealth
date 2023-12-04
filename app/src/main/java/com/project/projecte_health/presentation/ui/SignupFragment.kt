package com.project.projecte_health.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.project.projecte_health.R
import com.project.projecte_health.databinding.FragmentHomeBinding
import com.project.projecte_health.databinding.FragmentSignupBinding
import com.project.projecte_health.utils.Utils.safeNavigate


class SignupFragment : Fragment() {

    lateinit var binding : FragmentSignupBinding
    //ASK USER WHAT TYPE HE IS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextBtn.setOnClickListener {
            if (binding.doctor.isChecked){
                val action = SignupFragmentDirections.actionSignupFragmentToPatientInfoSignupFragment("Doctor")
                findNavController().safeNavigate(action)
            } else {
                val action = SignupFragmentDirections.actionSignupFragmentToPatientInfoSignupFragment("Patient")
                findNavController().safeNavigate(action)
            }

        }
    }
}