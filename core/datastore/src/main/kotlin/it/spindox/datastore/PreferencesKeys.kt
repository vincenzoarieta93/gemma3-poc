package it.spindox.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {

    const val USER_PREFERENCES = "user_preferences"

    val PIN = stringPreferencesKey("PIN")
    val THEME_APPEARANCE = intPreferencesKey("THEME_APPEARANCE")
}