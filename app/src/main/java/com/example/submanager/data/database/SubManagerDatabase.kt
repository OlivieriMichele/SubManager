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
                    CategoryEntity("Intrattenimento", "Tv", 20.0, "", 0),
                    CategoryEntity("Software", "Code", 50.0, "", 1),
                    CategoryEntity("Fitness", "FitnessCenter", 40.0, "", 2),
                    CategoryEntity("Shopping", "ShoppingCart", 50.0, "", 0),
                    CategoryEntity("Casa", "AccountBalance", 400.0, "Generiche spese per la casa", 0)
                )
                categoryDAOs.insertAll(defaultCategories)
            }
        }
    }
}