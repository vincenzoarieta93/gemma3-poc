package it.spindox.database.datasource

import it.spindox.database.dao.SampleDao
import it.spindox.database.datasource.abstraction.SampleDatabaseDataSource
import it.spindox.database.model.SampleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class SampleDatabaseDataSourceImpl @Inject constructor(
    private val sampleDao: SampleDao,
) : SampleDatabaseDataSource  {

    override fun getAllItems(): Flow<List<SampleEntity>> {
        return sampleDao.getAllItems()
    }

    override suspend fun insertItem(item: SampleEntity) {
        sampleDao.insertItem(item)
    }

    override suspend fun deleteItem(name: String) {
        sampleDao.deleteItem(name)
    }

}