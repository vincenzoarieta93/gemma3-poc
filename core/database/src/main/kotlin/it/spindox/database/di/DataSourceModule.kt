package it.spindox.database.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.spindox.database.datasource.SampleDatabaseDataSourceImpl
import it.spindox.database.datasource.abstraction.SampleDatabaseDataSource


@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindApiDataSource(impl: SampleDatabaseDataSourceImpl): SampleDatabaseDataSource
}