package it.spindox.data.repository

import it.spindox.data.model.ThemeAppearance
import it.spindox.data.repository.abstraction.DataStoreRepository
import it.spindox.datastore.datasource.abstraction.DataStoreDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStoreDataSource: DataStoreDataSource
) : DataStoreRepository {
    override suspend fun setPin(value: String) {
        dataStoreDataSource.setPin(value)
    }

    override fun validatePin(pin: String): Flow<Boolean> {
        return dataStoreDataSource.validatePin(pin)
    }

    override suspend fun resetDataStore() {
        dataStoreDataSource.resetDataStore()
    }

    override fun getTheme(): StateFlow<ThemeAppearance> {
        return dataStoreDataSource.getThemeAppearance().map {
            ThemeAppearance.getByValue(it)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = ThemeAppearance.AUTO
        )
    }

    override suspend fun setTheme(theme: ThemeAppearance) {
        dataStoreDataSource.setThemeAppearance(theme.value)
    }
}