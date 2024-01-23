package com.project.projecte_health.data.local.users

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Availability(
    val days: List<String> = emptyList(),
    val startTime: String? = "",
    val endTime: String? = ""
) : Parcelable