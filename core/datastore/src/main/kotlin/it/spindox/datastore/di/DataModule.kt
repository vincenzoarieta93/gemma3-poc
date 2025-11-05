package it.spindox.datastore.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.spindox.datastore.datasource.DataStoreDataSourceImpl
import it.spindox.datastore.datasource.abstraction.DataStoreDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsDataStoreDataSource(dataStoreDataSource: DataStoreDataSourceImpl): DataStoreDataSource
}