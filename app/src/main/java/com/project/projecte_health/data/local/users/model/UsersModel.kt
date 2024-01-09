package com.project.projecte_health.data.local.users.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize


@Parcelize
data class UsersModel(
    val name: String,
    val userId: String,
    val speciality: String,
    val address: String,
    val postCode: String,
    val distance: Double? = 0.0,
    val latLng: LatLng,
    val rating : Double? = 0.0,
    val imageUrl : String?= "",
    val bio : String ? = ""
) : Parcelable