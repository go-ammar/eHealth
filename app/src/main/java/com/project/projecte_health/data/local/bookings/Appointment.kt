package com.project.projecte_health.data.local.bookings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Appointment(
    val doctorId: String? = "",
    val doctorName: String? = "",
    val patientId: String? = "",
    val patientName: String? = "",
    val date: Long? = 0,
    val startTime: String? = "",
    val endTime: String? = "",
    val details: String? = ""
) : Parcelable