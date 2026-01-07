package it.spindox.datastore.datasource

import it.spindox.datastore.DataStoreManager
import it.spindox.datastore.datasource.abstraction.DataStoreDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DataStoreDataSourceImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
): DataStoreDataSource {

    override suspend fun setPin(value: String) {
        dataStoreManager.setPIN(value)
    }

    override fun validatePin(pin: String): Flow<Boolean> {
        return dataStoreManager.validatePin(pin)
    }

    override suspend fun resetDataStore() {
        dataStoreManager.resetDataStore()
    }

    override fun getThemeAppearance(): StateFlow<Int> {
        return dataStoreManager.themeAppearance
    }

    override suspend fun setThemeAppearance(value: Int) {
        dataStoreManager.setThemeAppearance(value)
    }

    override suspend fun saveCodeVerifier(value: String) {
        dataStoreManager.saveCodeVerifier(value)
    }

    override fun getCodeVerifier(): Flow<String> = dataStoreManager.getCodeVerifier()


    override suspend fun saveToken(value: String) {
        dataStoreManager.saveToken(value)
    }

    override fun getToken(): Flow<String> = dataStoreManager.getToken()

    override suspend fun removeToken() {
        dataStoreManager.removeToken()
    }
}