package com.project.projecte_health.presentation.ui.patients

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.project.projecte_health.R
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.ActivityAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : BaseActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigations()

    }

    override fun onPause() {
        super.onPause()
        Log.d("ACTIVITY ACCOUNT ", "onPause: ")
    }

    private fun setBottomNavigations() {

        binding.bottomNavigation.itemIconTintList = null
        binding.bottomNavigation.selectedItemId = R.id.account_button

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home_button -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.prescriptions_button -> {
                    val intent = Intent(this, PrescriptionsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.appointments_button -> {
                    val intent = Intent(this, AppointmentsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.account_button -> {
                    true
                }


                else -> {


                    false
                }
            }
        }
    }
}