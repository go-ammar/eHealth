package com.project.projecte_health.data.local.users.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.projecte_health.data.local.users.model.UsersModel

@Dao
interface UsersDao {


//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert(entity: UsersModel)
//
//    @Query("SELECT * FROM usersTable")
//    fun getAll(): LiveData<List<UsersModel>>

}