package com.project.projecte_health.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.projecte_health.data.local.PrefsManager.PreferencesKeys.USER_ID
import com.project.projecte_health.data.local.PrefsManager.PreferencesKeys.USER_NAME
import com.project.projecte_health.utils.Constants.AUTO_AUTH_PREFS
import com.project.projecte_health.utils.Constants.USER_ID_PREF
import com.project.projecte_health.utils.Constants.USER_NAME_PREF
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject


class PrefsManager @Inject constructor(@ApplicationContext context: Context) {

    private val Context._dataStore by preferencesDataStore(name = AUTO_AUTH_PREFS)
//        private val dataStore = context.createDataStore(name = AUTO_AUTH_PREFS)


    private val dataStore : DataStore<Preferences> = context._dataStore


    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey(USER_ID_PREF)
        val USER_NAME = stringPreferencesKey(USER_NAME_PREF)
//        val USER_PHONE_NUMBER = intPreferencesKey(USER_PHONE_NUMBER_PREF)
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit {
            it[USER_ID] = userId
        }
    }

    suspend fun getUserId(): String? {
        val preferences = dataStore.data.first()
        return preferences[USER_ID]
    }

    suspend fun saveName(name: String) {
        dataStore.edit {
            it[USER_NAME] = name
        }
    }

    suspend fun getUserName(): String? {
        val preferences = dataStore.data.first()
        return preferences[USER_NAME]
    }

}
