package it.spindox.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.spindox.data.repository.DataStoreRepositoryImpl
import it.spindox.data.repository.abstraction.MainRepository
import it.spindox.data.repository.MainRepositoryImpl
import it.spindox.data.repository.abstraction.DataStoreRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindsModule {

    @Binds
    @Singleton
    abstract fun bindsDataStoreRepository(dataStoreRepository: DataStoreRepositoryImpl): DataStoreRepository

    @Binds
    @Singleton
    abstract fun bindsMainRepository(mainRepository: MainRepositoryImpl): MainRepository
}