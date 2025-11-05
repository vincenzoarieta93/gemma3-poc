package it.spindox.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.spindox.network.BuildConfig
import it.spindox.network.api.PokemonApiService
import it.spindox.network.di.qualifiers.ApiClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    @ApiClient
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    if (BuildConfig.DEBUG) {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                }
            )
            .build()

    @Provides
    @Singleton
    @ApiClient
    fun provideRetrofit(@ApiClient okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    fun providePokemonApiService(@ApiClient retrofit: Retrofit): PokemonApiService =
        retrofit.create(PokemonApiService::class.java)
}