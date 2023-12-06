package com.project.projecte_health.data.local.dashboard.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.projecte_health.data.local.dashboard.model.DashboardModel

@Dao
interface DashboardDao {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert(entity: DashboardModel)
//
//    @Query("SELECT * FROM dashboardTable")
//    fun getAll(): LiveData<List<DashboardModel>>

}