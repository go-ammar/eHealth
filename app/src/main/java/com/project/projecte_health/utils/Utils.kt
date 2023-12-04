package com.project.projecte_health.utils

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import timber.log.Timber
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt

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
}