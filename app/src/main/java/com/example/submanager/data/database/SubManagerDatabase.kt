package com.example.submanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile

@Database(
    entities = [SubscriptionEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SubManagerDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDAOs
    abstract fun categoryDao(): CategoryDAOs

    companion object{
        @Volatile
        private var INSTANCE: SubManagerDatabase? = null

        fun getDatabase(context: Context): SubManagerDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SubManagerDatabase::class.java,
                    "sub_manager_database"
                )
                    .addCallback(DatabaseCallback()) // Callback per prepolare il db
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDatabase(database.categoryDao())
                    }
                }
            }

            private suspend fun prepopulateDatabase(categoryDAOs: CategoryDAOs){
                val defaultCategories = listOf(
                    CategoryEntity(
                        name = "Intrattenimento",
                        iconName = "Tv",
                        budget = 20.0,
                        description = "",
                        gradientIndex = 0
                    ),
                    CategoryEntity(
                        name = "Software",
                        iconName = "Code",
                        budget = 50.0,
                        description = "",
                        gradientIndex = 1
                    ),
                    CategoryEntity(
                        name = "Fitness",
                        iconName = "FitnessCenter",
                        budget = 40.0,
                        description = "",
                        gradientIndex = 2
                    ),
                    CategoryEntity(
                        name = "Shopping",
                        iconName = "ShoppingCart",
                        budget = 50.0,
                        description = "",
                        gradientIndex = 0
                    ),
                    CategoryEntity(
                        name = "Casa",
                        iconName = "AccountBalance",
                        budget = 400.0,
                        description = "Generiche spese per la casa",
                        gradientIndex = 0
                    )
                )

                categoryDAOs.insertAll(defaultCategories)
            }
        }
    }
}