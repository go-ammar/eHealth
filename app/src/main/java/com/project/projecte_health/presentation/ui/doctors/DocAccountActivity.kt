package com.project.projecte_health.presentation.ui.doctors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.ActivityDocAccountBinding
import com.project.projecte_health.presentation.ui.patients.AccountActivity
import com.project.projecte_health.presentation.ui.patients.AppointmentsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocAccountActivity : BaseActivity() {

    private lateinit var binding :ActivityDocAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDocAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavigations()
    }


    private fun setBottomNavigations() {

        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.selectedItemId = R.id.account_button

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home_button -> {
                    val intent = Intent(this, DoctorsDashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.feedback_button ->{
                    val intent = Intent(this, DoctorFeedbackActivity::class.java)
                    startActivity(intent)
                    finish()
                    false
                }
                R.id.appointments_button ->{
                    val intent = Intent(this, DoctorAppointmentActivity::class.java)
                    startActivity(intent)
                    finish()
                    false
                }
                R.id.account_button ->{
                   true
                }
                else -> {
                    false
                }
            }
        }

    }

}