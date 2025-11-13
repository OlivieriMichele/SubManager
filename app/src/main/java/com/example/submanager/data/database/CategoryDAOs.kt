package com.example.submanager.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAOs {
    /**
     * Osserva TUTTE le categorie (reattivo: Flow si aggiorna al cambiamento del db)
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    /**
    * Ottiene la categoria per nome
    */
    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getByName(name: String): CategoryEntity?

    /**
    * Ottiene tutte le categorie
    */
    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<CategoryEntity>

    /**
    * Inserisce categoria
    */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    /**
    * Inserisce categorie di default
    */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    /**
    * Aggiorna categoria
    */
    @Update
    suspend fun update(category: CategoryEntity)

    /**
     * Elimina categoria per nome
     */
    @Query("DELETE FROM categories WHERE name = :name")
    suspend fun deleteByName(name: String)

    /**
     * Elimina tutte le categorie
     */
    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    /**
     * Conta le categorie
     */
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    /**
     * Verifica se categoria esiste
     */
    @Query("SELECT EXISTS(SELECT 1 FROM categories WHERE name = :name COLLATE NOCASE)")
    suspend fun exist(name: String): Boolean
}