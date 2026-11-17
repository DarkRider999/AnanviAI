package com.aiuniverse.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_SELECTED_PROVIDER_ID = stringPreferencesKey("selected_provider_id")
        private val KEY_SELECTED_MODEL_ID = stringPreferencesKey("selected_model_id")
        private val KEY_THEME = stringPreferencesKey("app_theme")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    val selectedProviderId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_PROVIDER_ID] ?: "gemini"
    }

    val selectedModelId: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_MODEL_ID] ?: "gemini-3.8-flash"
    }

    suspend fun saveSelectedModel(providerId: String, modelId: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_PROVIDER_ID] = providerId
            preferences[KEY_SELECTED_MODEL_ID] = modelId
        }
    }

    val appTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME] ?: "SYSTEM"
    }

    suspend fun setAppTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = theme
        }
    }
}
