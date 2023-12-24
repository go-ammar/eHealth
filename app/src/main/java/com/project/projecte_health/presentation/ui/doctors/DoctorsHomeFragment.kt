package com.project.projecte_health.presentation.ui.doctors

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseFragment
import com.project.projecte_health.databinding.FragmentDoctorAppointmentDetailBinding
import com.project.projecte_health.databinding.FragmentDoctorsHomeBinding
import com.project.projecte_health.presentation.ui.registration.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DoctorsHomeFragment : BaseFragment() {


    lateinit var binding: FragmentDoctorsHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoctorsHomeBinding.inflate(layoutInflater, container, false)

        lifecycleScope.launch {
            binding.heading.text =
                "Welcome To HealthConnect, " + (activity as DoctorsDashboardActivity).prefsManager.getUserName()
                    .toString()
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            logoutBtn.setOnClickListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }
}