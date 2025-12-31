package it.spindox.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.spindox.data.repository.InferenceModelRepositoryImpl
import it.spindox.data.repository.abstraction.InferenceModelRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InferenceModelProvidesModule {

    @Provides
    @Singleton
    fun provideInferenceModelRepository(
        @ApplicationContext context: Context,
    ): InferenceModelRepository {
        return InferenceModelRepositoryImpl(context)
    }
}