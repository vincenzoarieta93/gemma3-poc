package it.spindox.data.repository.abstraction

import it.spindox.data.model.ThemeAppearance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DataStoreRepository {
    suspend fun setPin(value: String)
    fun validatePin(pin: String): Flow<Boolean>
    suspend fun resetDataStore()
    fun getTheme(): StateFlow<ThemeAppearance>
    suspend fun setTheme(theme: ThemeAppearance)
}