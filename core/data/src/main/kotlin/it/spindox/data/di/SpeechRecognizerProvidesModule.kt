package it.spindox.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.spindox.data.voicecontroller.SpeechRecognizerController
import it.spindox.data.voicecontroller.SpeechRecognizerControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeechRecognizerProvidesModule {

    @Provides
    @Singleton
    fun provideSpeechRecognizer(
        @ApplicationContext context: Context,
    ): SpeechRecognizerController {
        return SpeechRecognizerControllerImpl(context)
    }
}