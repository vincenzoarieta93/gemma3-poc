package it.spindox.datastore.datasource.abstraction

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DataStoreDataSource {
    suspend fun setPin(value: String)
    fun validatePin(pin: String): Flow<Boolean>
    suspend fun resetDataStore()
    fun getThemeAppearance(): StateFlow<Int>
    suspend fun setThemeAppearance(value: Int)

    // Website auth methods
    suspend fun saveCodeVerifier(value: String)
    fun getCodeVerifier(): Flow<String>
    suspend fun saveToken(value: String)
    fun getToken(): Flow<String>
    suspend fun removeToken()
}