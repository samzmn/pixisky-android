package com.pixisky.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PixiDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pixi: Pixi)

    @Update
    suspend fun update(pixi: Pixi)

    @Delete
    suspend fun delete(pixi: Pixi)

    @Query("SELECT * from pixi_table WHERE id = :id")
    fun getItem(id: Int): Flow<Pixi>  // Flow makes the fun suspendable

    @Query("SELECT * from pixi_table ORDER BY id ASC")
    fun getAllItems(): Flow<List<Pixi>>
}