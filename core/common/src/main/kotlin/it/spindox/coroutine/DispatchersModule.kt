package it.spindox.coroutine

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DispatchersModule {
    @Binds
    @Singleton
    fun bindDispatcherProvider(provider: DefaultDispatcherProvider): DispatcherProvider
}
