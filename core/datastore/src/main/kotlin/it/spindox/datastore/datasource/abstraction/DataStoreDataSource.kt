package it.spindox.datastore.datasource.abstraction

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DataStoreDataSource {
    suspend fun setPin(value: String)
    fun validatePin(pin: String): Flow<Boolean>
    suspend fun resetDataStore()
    fun getThemeAppearance(): StateFlow<Int>
    suspend fun setThemeAppearance(value: Int)
}