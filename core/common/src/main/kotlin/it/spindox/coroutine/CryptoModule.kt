package it.spindox.coroutine

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.spindox.qualifiers.MD5
import java.security.MessageDigest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    @Provides
    @MD5
    fun provideMd5Hashing(): MessageDigest {
        return MessageDigest.getInstance("MD5")
    }
}