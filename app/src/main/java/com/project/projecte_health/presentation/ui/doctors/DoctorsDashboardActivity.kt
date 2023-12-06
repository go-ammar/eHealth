package com.project.projecte_health.presentation.ui.doctors

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.ActivityDoctorsDashboardBinding
import com.project.projecte_health.databinding.FragmentPrescriptionDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoctorsDashboardActivity : BaseActivity() {

    lateinit var binding: ActivityDoctorsDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorsDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}