package it.spindox.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.spindox.network.datasource.abstraction.ApiDataSource
import it.spindox.network.ApiDataSourceImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindsModule {
    @Binds
    abstract fun bindApiDataSource(impl: ApiDataSourceImpl): ApiDataSource
}