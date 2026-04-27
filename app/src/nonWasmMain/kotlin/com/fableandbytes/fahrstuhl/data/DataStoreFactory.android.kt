package com.fableandbytes.fahrstuhl.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_settings")

// Wir brauchen eine Möglichkeit, den Context zu setzen. 
// In einer echten App würde man Dependency Injection nutzen.
lateinit var appContext: Context

actual fun createDataStore(): DataStore<Preferences> {
    return appContext.dataStore
}
