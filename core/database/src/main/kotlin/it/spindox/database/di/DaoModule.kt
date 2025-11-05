package it.spindox.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.spindox.database.dao.SampleDao
import it.spindox.database.database.SampleDatabase

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    fun providesSampleDao(
        database: SampleDatabase,
    ): SampleDao = database.sampleDao()
}