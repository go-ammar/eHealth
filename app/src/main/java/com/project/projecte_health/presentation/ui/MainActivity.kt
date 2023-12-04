package com.project.projecte_health.presentation.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.project.projecte_health.base.BaseActivity
import com.project.projecte_health.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var keepSplashOnScreen = true
        val delay = 2000L
        installSplashScreen().setKeepOnScreenCondition { keepSplashOnScreen }
        Handler(Looper.getMainLooper()).postDelayed({
            keepSplashOnScreen = false
            checkLocationPermission()
        }, delay)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionViews()
    }

    private fun actionViews() {

        binding.loginBtn.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun checkLocationPermission() {
        // Check if the permission is not granted
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions granted, proceed with location-related tasks
//            startLocationUpdates()
        } else {
            // Request permissions
            requestStoragePermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            requestStoragePermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        }
    }


    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
//                imagePickerIntent()
            } else {
//                checkPermissions()
            }
        }

}