package it.spindox.datastore.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.spindox.datastore.DataStoreManager
import it.spindox.qualifiers.MD5
import java.security.MessageDigest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @MD5 hashing: MessageDigest
    ): DataStoreManager = DataStoreManager(
        context,
        hashing
    )
}