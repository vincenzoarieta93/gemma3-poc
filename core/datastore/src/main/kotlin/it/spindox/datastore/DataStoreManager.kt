package it.spindox.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import it.spindox.datastore.PreferencesKeys.USER_PREFERENCES
import it.spindox.datastore.utility.CryptoManager
import it.spindox.qualifiers.MD5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.security.MessageDigest
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(USER_PREFERENCES)

class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @MD5 private val hashing: MessageDigest
) {
    private val cryptoManager = CryptoManager()
    private val settingsDataStore = context.dataStore
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun resetDataStore() {
        settingsDataStore.edit {
            it.clear()
        }
    }

    suspend fun setPIN(value: String) {
        val pinHash = hashing.digest(value.toByteArray())
        cryptoManager.encrypt(pinHash)?.let {
            settingsDataStore.edit { settings ->
                settings[PreferencesKeys.PIN] = it
            }
        }
    }

    private val encryptedPinFlow: StateFlow<String> = settingsDataStore.data.map { preferences ->
        preferences[PreferencesKeys.PIN].orEmpty()
    }.stateIn(coroutineScope, SharingStarted.Lazily, "")

    suspend fun setThemeAppearance(theme: Int) {
        settingsDataStore.edit { settings ->
            settings[PreferencesKeys.THEME_APPEARANCE] = theme
        }
    }

    val themeAppearance: StateFlow<Int> = settingsDataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_APPEARANCE] ?: 0
    }.stateIn(coroutineScope, SharingStarted.Eagerly, 0)

    fun validatePin(pin: String): Flow<Boolean> {
        val pinHash = hashing.digest(pin.toByteArray())
        return encryptedPinFlow.map { encryptedPin ->
            val decryptedPinHash = cryptoManager.decryptPin(encryptedPin)
            decryptedPinHash?.contentEquals(pinHash) ?: false
        }
    }
}