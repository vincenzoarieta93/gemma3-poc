package it.spindox.gemma3.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.spindox.gemma3.DefaultAppNavigator
import it.spindox.navigation.AppNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigatorModule {

    @Provides
    @Singleton
    fun provideAppNavigator(@ApplicationContext context: Context): AppNavigator {
        return DefaultAppNavigator(context)
    }
}
