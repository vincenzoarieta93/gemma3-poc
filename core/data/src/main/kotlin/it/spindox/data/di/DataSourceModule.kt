package it.spindox.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.spindox.data.datasource.ModelsDataSourceImpl
import it.spindox.data.datasource.ModelsDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceBindsModule {
    @Binds
    abstract fun bindModelsDataSource(impl: ModelsDataSourceImpl): ModelsDataSource
}