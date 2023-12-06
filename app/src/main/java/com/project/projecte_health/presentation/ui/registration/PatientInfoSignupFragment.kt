package com.project.projecte_health.presentation.ui.registration

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.projecte_health.databinding.FragmentPatientInfoSignupBinding
import com.project.projecte_health.utils.Utils.safeNavigate


class PatientInfoSignupFragment : Fragment() {

    lateinit var binding: FragmentPatientInfoSignupBinding
    private val args : PatientInfoSignupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPatientInfoSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.emailEt.addTextChangedListener {
            enableBtn()
        }

        binding.passwordEt.addTextChangedListener {
            enableBtn()
        }

        binding.confirmPasswordEt.addTextChangedListener {
            enableBtn()
        }

        binding.nextBtn.setOnClickListener {
            val action =
                PatientInfoSignupFragmentDirections.actionPatientInfoSignupFragmentToPatientInfoFragment(
                    email = binding.emailEt.text.toString(),
                    password = binding.passwordEt.text.toString(),
                    userType = args.userType
                )
            findNavController().safeNavigate(action)
        }
    }

    private fun enableBtn() {
        if (Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.text.toString()).matches()
            && binding.passwordEt.text.toString() == binding.confirmPasswordEt.text.toString() &&
            binding.passwordEt.text.toString()
                .isNotEmpty() && binding.confirmPasswordEt.text.toString().isNotEmpty()
            && binding.emailEt.text.toString().isNotEmpty()
        ) {
            binding.nextBtn.isEnabled = true
            binding.nextBtn.alpha = 1f
        } else {
            binding.nextBtn.isEnabled = false
            binding.nextBtn.alpha = 0.7f
        }
    }
}