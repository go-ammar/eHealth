package com.project.projecte_health.repositories

import android.util.Log
import com.project.projecte_health.network.services.ApiNetworkService
import timber.log.Timber
import javax.inject.Inject

class TestRepository @Inject constructor(
    private val userDetailsService: ApiNetworkService,
//    private val database: UsersDatabase
) {




    suspend fun refreshUserDetails(user: String) {
        try {
            val userDetails = userDetailsService.getUserList()
//            database.usersDao.insertUserDetails(userDetails.asDatabaseModel())
        } catch (e: Exception) {
            Timber.w(e)
        }
    }

}