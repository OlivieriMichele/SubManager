package com.example.submanager.data.database

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.submanager.ui.theme.AccentColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
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
                        prepopulateDatabase(database.categoryDao(), database.subscriptionDao())
                    }
                }
            }

            private suspend fun prepopulateDatabase(
                categoryDAOs: CategoryDAOs,
                subscriptionDAOs: SubscriptionDAOs
            ){
                val defaultCategories = listOf(
                    CategoryEntity("Intrattenimento", "Tv", 20.0, "", 0),
                    CategoryEntity("Fitness", "FitnessCenter", 40.0, "", 2),
                    CategoryEntity("Shopping", "ShoppingCart", 50.0, "", 0),
                    CategoryEntity("Casa", "AccountBalance", 400.0, "Generiche spese per la casa", 0)
                )
                categoryDAOs.insertAll(defaultCategories)

                val subscriptions = listOf(
                    // --- GENNAIO 2026 ---
                    SubscriptionEntity(0, "Netflix", 13.99, AccentColors.pastelPurple.toArgb(), LocalDate.of(2026, 1, 15).toEpochDay(), "Intrattenimento"),
                    SubscriptionEntity(0, "Disney+", 8.99, AccentColors.cerulean.toArgb(), LocalDate.of(2026, 1, 20).toEpochDay(), "Intrattenimento"),
                    SubscriptionEntity(0, "Spotify", 11.99, AccentColors.pastelGreen.toArgb(), LocalDate.of(2026, 1, 5).toEpochDay(), "Intrattenimento"),
                    SubscriptionEntity(0, "Fibra Ottica", 29.90, AccentColors.pastelIndigo.toArgb(), LocalDate.of(2026, 1, 1).toEpochDay(), "Casa"),
                    SubscriptionEntity(0, "Plus", 13.99, 0xFF003087.toInt(), LocalDate.of(2026, 1, 10).toEpochDay(), "Shopping"),

                    // --- FEBBRAIO 2026 ---
                    SubscriptionEntity(0, "Now Gym", 45.00, AccentColors.pastelYellow.toArgb(), LocalDate.of(2026, 2, 1).toEpochDay(), "Fitness"),
                    SubscriptionEntity(0, "Calisthenics Class", 25.00, AccentColors.magenta.toArgb(), LocalDate.of(2026, 2, 12).toEpochDay(), "Fitness"),
                    SubscriptionEntity(0, "Amazon Prime", 7.99, AccentColors.bue.toArgb(), LocalDate.of(2026, 2, 28).toEpochDay(), "Shopping"),
                    SubscriptionEntity(0, "Affitto", 250.00, AccentColors.azure.toArgb(), LocalDate.of(2026, 2, 1).toEpochDay(), "Casa")
                )

                subscriptionDAOs.insertAll(subscriptions)
            }
        }
    }
}