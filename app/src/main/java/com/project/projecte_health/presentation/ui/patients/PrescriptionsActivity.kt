package com.project.projecte_health.presentation.ui.patients

import android.content.Intent
import android.os.Bundle
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.ActivityPrescriptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrescriptionsActivity : BaseActivity() {


    private lateinit var binding: ActivityPrescriptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrescriptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigations()
    }

    private fun setBottomNavigations() {

        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.selectedItemId = R.id.prescriptions_button

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home_button -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.prescriptions_button -> {
                    true
                }

                R.id.appointments_button -> {
                    val intent = Intent(this, AppointmentsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.account_button -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }


                else -> {
                    false
                }
            }
        }
    }


}