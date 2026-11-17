package com.aiuniverse.app.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.AIModel
import com.aiuniverse.app.domain.model.AIProvider
import com.aiuniverse.app.domain.repository.AIProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelSelectionViewModel @Inject constructor(
    private val providerRepository: AIProviderRepository
) : ViewModel() {

    val providers: StateFlow<List<AIProvider>> = providerRepository.getProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _allModels = MutableStateFlow<List<AIModel>>(emptyList())
    val selectedProviderFilter = MutableStateFlow<String?>(null)
    val searchQuery = MutableStateFlow("")

    val filteredModels: StateFlow<List<AIModel>> = combine(
        _allModels,
        selectedProviderFilter,
        searchQuery
    ) { models, provider, query ->
        models.filter { model ->
            (provider == null || model.providerId.equals(provider, ignoreCase = true)) &&
            (query.isBlank() || model.name.contains(query, ignoreCase = true) || model.id.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            _allModels.value = providerRepository.getAllAvailableModels()
        }
    }

    fun setProviderFilter(providerId: String?) {
        selectedProviderFilter.value = providerId
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }
}
