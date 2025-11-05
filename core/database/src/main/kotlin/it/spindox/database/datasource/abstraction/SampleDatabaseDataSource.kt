package it.spindox.database.datasource.abstraction

import it.spindox.database.model.SampleEntity
import kotlinx.coroutines.flow.Flow

interface SampleDatabaseDataSource {
    fun getAllItems(): Flow<List<SampleEntity>>
    suspend fun insertItem(item: SampleEntity)
    suspend fun deleteItem(name: String)
}