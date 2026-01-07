package it.spindox.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {

    const val USER_PREFERENCES = "user_preferences"

    val PIN = stringPreferencesKey("PIN")
    val THEME_APPEARANCE = intPreferencesKey("THEME_APPEARANCE")

    // Keys for website auth
    val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
    val KEY_CODE_VERIFIER = stringPreferencesKey("code_verifier")
}