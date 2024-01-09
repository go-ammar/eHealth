package com.project.projecte_health.utils

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.projecte_health.R
import com.project.projecte_health.data.local.bookings.Appointment
import timber.log.Timber

object Utils {

    fun distanceBetween(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Double {
        val radiusOfEarth = 6371 // Earth's radius in kilometers

        val latDistance = Math.toRadians(endLat - startLat)
        val lngDistance = Math.toRadians(endLng - startLng)

        val a = kotlin.math.sin(latDistance / 2) * kotlin.math.sin(latDistance / 2) +
                kotlin.math.cos(Math.toRadians(startLat)) * kotlin.math.cos(Math.toRadians(endLat)) *
                kotlin.math.sin(lngDistance / 2) * kotlin.math.sin(lngDistance / 2)

        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return radiusOfEarth * c
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        Timber.d("NAVIGATION :: Click happened")
        currentDestination?.getAction(direction.actionId)?.run {
            Timber.d("NAVIGATION :: Click Propagated")
            navigate(direction)
        }
    }

    fun NavController.safeNavigate(actionId: Int, bundle: Bundle? = null) {
        Timber.d("NAVIGATION :: Click happened")
        currentDestination?.getAction(actionId)?.run {
            Timber.d("NAVIGATION :: Click Propagated")
            bundle?.let {
                navigate(actionId, bundle)
            } ?: navigate(actionId)

        }
    }

    fun displayCustomLoaderView(activity: Activity?, loading: Boolean): Int {
        var showLoader = 0
        //loading true -> visible
        when (loading) {
            true -> {
                val window = activity!!.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = activity.resources.getColor(R.color.colorDim)
                showLoader = View.VISIBLE
            }
            false -> {
                val window = activity!!.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor = activity.resources.getColor(R.color.colorOnPrimary)
                showLoader = View.GONE
            }
        }
        return showLoader
    }

}