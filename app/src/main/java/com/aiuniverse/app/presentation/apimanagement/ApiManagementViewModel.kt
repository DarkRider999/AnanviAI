package com.aiuniverse.app.presentation.apimanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.AIProvider
import com.aiuniverse.app.domain.usecase.ManageApiKeysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApiManagementUiState(
    val selectedProvider: AIProvider? = null,
    val currentKeyInput: String = "",
    val testStatus: String? = null,
    val isTesting: Boolean = false
)

@HiltViewModel
class ApiManagementViewModel @Inject constructor(
    private val manageApiKeysUseCase: ManageApiKeysUseCase
) : ViewModel() {

    val providers: StateFlow<List<AIProvider>> = manageApiKeysUseCase.getProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(ApiManagementUiState())
    val uiState: StateFlow<ApiManagementUiState> = _uiState.asStateFlow()

    fun selectProvider(provider: AIProvider) {
        val existingKey = manageApiKeysUseCase.getApiKey(provider.id)
        _uiState.update {
            it.copy(
                selectedProvider = provider,
                currentKeyInput = existingKey,
                testStatus = null
            )
        }
    }

    fun onKeyInputChanged(input: String) {
        _uiState.update { it.copy(currentKeyInput = input) }
    }

    fun saveKey() {
        val provider = _uiState.value.selectedProvider ?: return
        val key = _uiState.value.currentKeyInput.trim()
        viewModelScope.launch {
            manageApiKeysUseCase.saveApiKey(provider.id, key)
            _uiState.update { it.copy(testStatus = "API Key saved securely via Android Keystore AES-256") }
        }
    }

    fun removeKey() {
        val provider = _uiState.value.selectedProvider ?: return
        viewModelScope.launch {
            manageApiKeysUseCase.removeApiKey(provider.id)
            _uiState.update { it.copy(currentKeyInput = "", testStatus = "Key removed") }
        }
    }

    fun testConnection() {
        val provider = _uiState.value.selectedProvider ?: return
        _uiState.update { it.copy(isTesting = true) }
        viewModelScope.launch {
            val success = manageApiKeysUseCase.testConnection(provider.id)
            _uiState.update {
                it.copy(
                    isTesting = false,
                    testStatus = if (success) "Connection Verified: Active & Online!" else "Connection Failed: Check API Key."
                )
            }
        }
    }
}
