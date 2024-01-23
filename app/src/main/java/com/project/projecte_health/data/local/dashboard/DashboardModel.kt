package com.project.projecte_health.data.local.dashboard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashboardTable")
data class DashboardModel (
    @PrimaryKey (autoGenerate = true)
    val notifications : Int
)