package com.project.projecte_health.data.local.dashboard.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashboardTable")
data class DashboardModel (
    @PrimaryKey
    val notifications : String
)