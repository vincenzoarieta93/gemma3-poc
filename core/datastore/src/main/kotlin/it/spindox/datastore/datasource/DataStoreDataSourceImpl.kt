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
}