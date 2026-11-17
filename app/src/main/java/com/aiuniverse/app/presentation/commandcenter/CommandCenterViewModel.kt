package com.aiuniverse.app.presentation.commandcenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.data.local.datastore.UserPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommandCenterViewModel @Inject constructor(
    private val preferences: UserPreferencesDataStore
) : ViewModel() {
    val jarvisBaseUrl: Flow<String> = preferences.jarvisBaseUrl
    val hermesBaseUrl: Flow<String> = preferences.hermesBaseUrl

    fun saveJarvisBaseUrl(url: String) {
        viewModelScope.launch { preferences.saveJarvisBaseUrl(url) }
    }

    fun saveHermesBaseUrl(url: String) {
        viewModelScope.launch { preferences.saveHermesBaseUrl(url) }
    }
}
