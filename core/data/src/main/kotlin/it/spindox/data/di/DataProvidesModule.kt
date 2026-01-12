package it.spindox.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.spindox.data.helper.ModelPathHelper
import it.spindox.data.repository.InferenceModelRepositoryImpl
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.data.voicecontroller.SpeechRecognizerController
import it.spindox.data.voicecontroller.SpeechRecognizerControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataProvidesModule {

    @Provides
    @Singleton
    fun provideModelPathHelper(
        @ApplicationContext context: Context
    ): ModelPathHelper {
        return ModelPathHelper(context)
    }

    @Provides
    @Singleton
    fun provideInferenceModelRepository(
        @ApplicationContext context: Context,
        modelPathHelper: ModelPathHelper
    ): InferenceModelRepository {
        return InferenceModelRepositoryImpl(context, modelPathHelper)
    }

    @Provides
    @Singleton
    fun provideSpeechRecognizer(
        @ApplicationContext context: Context,
    ): SpeechRecognizerController {
        return SpeechRecognizerControllerImpl(context)
    }
}