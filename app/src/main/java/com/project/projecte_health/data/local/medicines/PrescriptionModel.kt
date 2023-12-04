package com.project.projecte_health.data.local.medicines

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrescriptionModel(
    val name: String? = "",
    val details: String? = "",
    val dosage: String? = "",
    val doctorName: String? = "",
    val doctorId: String? = ""
) : Parcelable