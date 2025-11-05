package it.spindox.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.spindox.database.model.SampleEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface SampleDao {

    @Query("SELECT (SELECT COUNT(*) FROM SampleEntity) == 0")
    fun isDatabaseEmpty(): Flow<Boolean>

    @Query("SELECT * FROM SampleEntity")
    fun getAllItems(): Flow<List<SampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(vararg items: SampleEntity)

    @Delete
    suspend fun deleteItem(item: SampleEntity)

    @Query("DELETE FROM SampleEntity WHERE name = :name")
    suspend fun deleteItem(name: String)

    @Query("DELETE FROM SampleEntity")
    suspend fun deleteAllItems()
}