package it.spindox.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.spindox.database.database.SampleDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesSampleDatabase(
        @ApplicationContext context: Context,
    ): SampleDatabase = Room.databaseBuilder(
        context,
        SampleDatabase::class.java,
        "sample-database",
    ).build()
}