package com.project.projecte_health.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.project.projecte_health.data.local.dashboard.dao.DashboardDao
import com.project.projecte_health.data.local.dashboard.model.DashboardModel
import com.project.projecte_health.data.local.users.dao.UsersDao
import com.project.projecte_health.data.local.users.model.UsersModel
import com.project.projecte_health.utils.Constants
import com.project.projecte_health.utils.JsonConverter
import timber.log.Timber

@Database(
    entities = [DashboardModel::class],
    version = 2,
    exportSchema = false
)
//@TypeConverters(value = [JsonConverter::class])
abstract class AppDatabase : RoomDatabase(){

//    abstract fun getUsersDao(): UsersDao

    abstract fun getDashboardDao(): DashboardDao


    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation()
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Timber.d("buildDataclasses")
                    }
                })
                .build()
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add migration steps here
            }
        }
    }

}