package com.example.submanager.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDAOs {
    /**
     * Osserva TUTTE le subscriptions (reattivo: Flow si aggiorna al cambiamento del db)
     */
    @Query("SELECT * FROM subscriptions ORDER BY name ASC")
    fun observeAll(): Flow<List<SubscriptionEntity>>

    /**
     * Osserva subscriptions per categoria
     */
    @Query("SELECT * FROM subscriptions WHERE category = :categoryName ORDER BY name ASC")
    fun observeByCategory(categoryName: String): Flow<List<SubscriptionEntity>>

    /**
     * Ottiene subscription per ID (one-shot, non reattivo)
     */
    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Int): SubscriptionEntity?

    /**
     * Ottiene tutte le subscriptions (one-shot)
     */
    @Query("SELECT * FROM subscriptions")
    suspend fun getAll(): List<SubscriptionEntity>

    /**
     * Inserisce subscription
     * OnConflictStrategy.REPLACE: se esiste già, sovrascrive
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity): Long  // Ritorna ID generato

    /**
     * Inserisce multiple subscriptions
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subscriptions: List<SubscriptionEntity>)

    /**
     * Aggiorna subscription
     */
    @Update
    suspend fun update(subscription: SubscriptionEntity)

    /**
     * Elimina subscription per ID
     */
    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Elimina tutte le subscriptions di una categoria
     */
    @Query("DELETE FROM subscriptions WHERE category = :categoryName")
    suspend fun deleteByCategory(categoryName: String)

    /**
     * Elimina tutte le subscriptions
     */
    @Query("DELETE FROM subscriptions")
    suspend fun deleteAll()

    /**
     * Conta subscriptions per categoria
     */
    @Query("SELECT COUNT(*) FROM subscriptions WHERE category = :categoryName")
    suspend fun countByCategory(categoryName: String): Int

    /**
     * Calcola totale mensile per categoria
     */
    @Query("SELECT SUM(price) FROM subscriptions WHERE category = :categoryName")
    suspend fun getTotalByCategory(categoryName: String): Double?

    /**
     * Calcola totale mensile globale
     */
    @Query("SELECT SUM(price) FROM subscriptions")
    fun observeTotalMonthly(): Flow<Double?>
}