package com.project.projecte_health.utils

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    fun isDoctorAvailable(database: FirebaseDatabase, doctorId: String, desiredTime: String, callback: (Boolean) -> Unit) {
        // Query appointments for the specified doctor and time
        val databaseReference  = database.reference.child("appointments")
        val query = databaseReference.orderByChild("doctorId").equalTo(doctorId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isAvailable = true

                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)

                    // Check if there is an appointment at the desired time
                    if (appointment != null && appointment.startTime == desiredTime) {
                        isAvailable = false
                        break
                    }
                }

                // Callback with the result
                callback.invoke(isAvailable)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                callback.invoke(false)
            }
        })
    }
}