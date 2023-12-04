package com.project.projecte_health.di

import android.content.Context
import com.project.projecte_health.data.local.PrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providePrefsManager(@ApplicationContext appContext: Context): PrefsManager {
        return PrefsManager(appContext)
    }
}