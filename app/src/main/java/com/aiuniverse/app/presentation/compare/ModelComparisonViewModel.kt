package com.aiuniverse.app.presentation.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.AIModel
import com.aiuniverse.app.domain.model.ModelComparisonResult
import com.aiuniverse.app.domain.repository.AIProviderRepository
import com.aiuniverse.app.domain.usecase.CompareModelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompareUiState(
    val models: List<AIModel> = emptyList(),
    val modelA: AIModel? = null,
    val modelB: AIModel? = null,
    val prompt: String = "",
    val result: ModelComparisonResult? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ModelComparisonViewModel @Inject constructor(
    private val compareModelsUseCase: CompareModelsUseCase,
    private val providerRepository: AIProviderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            val list = providerRepository.getAllAvailableModels()
            _uiState.update {
                it.copy(
                    models = list,
                    modelA = list.getOrNull(0),
                    modelB = list.getOrNull(1)
                )
            }
        }
    }

    fun onPromptChanged(newPrompt: String) {
        _uiState.update { it.copy(prompt = newPrompt) }
    }

    fun selectModelA(model: AIModel) {
        _uiState.update { it.copy(modelA = model) }
    }

    fun selectModelB(model: AIModel) {
        _uiState.update { it.copy(modelB = model) }
    }

    fun runComparison() {
        val state = _uiState.value
        val mA = state.modelA ?: return
        val mB = state.modelB ?: return
        val p = state.prompt.trim()
        if (p.isEmpty()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val res = compareModelsUseCase.execute(p, mA, mB)
                _uiState.update { it.copy(result = res, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}
