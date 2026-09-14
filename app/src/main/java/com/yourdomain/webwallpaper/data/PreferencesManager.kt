package com.yourdomain.webwallpaper.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a single instance of DataStore for the application
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wallpaper_settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val ACTIVE_MODE = stringPreferencesKey("active_mode")
        val SOURCE_URL = stringPreferencesKey("source_url")
        val ZOOM_SIZE = floatPreferencesKey("zoom_size")
        val SCALE_MODE = stringPreferencesKey("scale_mode")
    }

    val activeModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ACTIVE_MODE] ?: "web"
    }

    val sourceUrlFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SOURCE_URL] ?: ""
    }

    val zoomSizeFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[ZOOM_SIZE] ?: 1.0f
    }

    val scaleModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SCALE_MODE] ?: "Center Crop"
    }

    suspend fun saveSettings(mode: String, url: String, zoom: Float, scale: String) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_MODE] = mode
            preferences[SOURCE_URL] = url
            preferences[ZOOM_SIZE] = zoom
            preferences[SCALE_MODE] = scale
        }
    }
}
