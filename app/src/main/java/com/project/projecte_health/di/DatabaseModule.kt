package com.project.projecte_health.di

import android.content.Context
import androidx.room.Room
import com.project.projecte_health.data.AppDatabase
import com.project.projecte_health.data.local.users.dao.UsersDao
import com.project.projecte_health.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .enableMultiInstanceInvalidation() // maybe room database create multi instance. add this line solved my problem
            .build()
    }

//    @Provides
//    fun provideUsersDao(db: AppDatabase) = db.getUsersDao()

    @Provides
    fun provideDashboardDao(db: AppDatabase) = db.getDashboardDao()

}