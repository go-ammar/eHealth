package com.project.projecte_health.data.local.users.model

import android.media.Rating
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.project.projecte_health.presentation.ui.bottomsheets.BottomSheetSpeciality
import kotlinx.parcelize.Parcelize

@Parcelize
data class DoctorDetailModel (
    val name: String,
    val speciality: String,
    val rating: Double = 0.0,
    val userId: String,
    val address: String,
    val postCode: String,
    val latLng: LatLng
) : Parcelable