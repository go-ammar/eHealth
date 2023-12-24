package com.project.projecte_health.data.local.users.model

import android.os.Parcelable
import com.project.projecte_health.data.local.bookings.Appointment
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileModel(
    val height: String? = "",
    val weight: String? = "",
    val allergies: String? = "",
    val bloodType: String? = "",
    val imageUrl: String? = "",
    val name: String? = "",
    val description: String? = "",
    val availability: Availability? = null
) : Parcelable
