package com.project.projecte_health.presentation.ui.doctors

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.ActivityDoctorAppointmentBinding
import com.project.projecte_health.databinding.ActivityDoctorsDashboardBinding
import com.project.projecte_health.presentation.ui.patients.AccountActivity
import com.project.projecte_health.presentation.ui.patients.AppointmentsActivity
import com.project.projecte_health.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoctorAppointmentActivity : BaseActivity() {

    private lateinit var binding: ActivityDoctorAppointmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavigations()
    }

    override fun displayProgressBar(loading: Boolean) {
        binding.loader.root.visibility = Utils.displayCustomLoaderView(this, loading)
    }


    private fun setBottomNavigations() {

        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.selectedItemId = R.id.appointments_button

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
                    true
                }
                R.id.account_button ->{
                    val intent = Intent(this, DocAccountActivity::class.java)
                    startActivity(intent)
                    finish()
                    false
                }
                else -> {
                    false
                }
            }
        }

    }

}