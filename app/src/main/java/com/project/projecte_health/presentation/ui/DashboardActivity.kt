package com.project.projecte_health.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.ActivityDashboardBinding

class DashboardActivity : BaseActivity() {

    private lateinit var binding : ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigations()
    }

    private fun setBottomNavigations() {

        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.selectedItemId = R.id.home_button

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home_button -> {
                    true
                }
                R.id.prescriptions_button ->{
                    val intent = Intent(this, PrescriptionsActivity::class.java)
                    startActivity(intent)
                    false
                }
                R.id.appointments_button ->{
                    val intent = Intent(this, AppointmentsActivity::class.java)
                    startActivity(intent)
                    false
                }
                R.id.account_button ->{
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    false
                }


                else -> {


                    false
                }
            }
        }

    }

}