package com.project.projecte_health.data.local.bookings

data class Feedback(
    val rating: Int? = 0,
    val feedback: String?,
    val patientId: String? = ""
)