package com.aiuniverse.app.presentation.imagegen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.ImageGenRequest
import com.aiuniverse.app.domain.model.ImageGenResult
import com.aiuniverse.app.domain.usecase.GenerateImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ImageGenUiState(
    val prompt: String = "",
    val negativePrompt: String = "",
    val selectedModel: String = "black-forest-labs/FLUX.1-schnell",
    val aspectRatio: String = "1:1",
    val currentResult: ImageGenResult? = null,
    val isGenerating: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ImageGenViewModel @Inject constructor(
    private val generateImageUseCase: GenerateImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImageGenUiState())
    val uiState: StateFlow<ImageGenUiState> = _uiState.asStateFlow()

    val recentGenerations: StateFlow<List<ImageGenResult>> = generateImageUseCase.getRecentGenerations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onPromptChanged(newPrompt: String) {
        _uiState.update { it.copy(prompt = newPrompt) }
    }

    fun onNegativePromptChanged(negPrompt: String) {
        _uiState.update { it.copy(negativePrompt = negPrompt) }
    }

    fun onModelSelected(modelId: String) {
        _uiState.update { it.copy(selectedModel = modelId) }
    }

    fun onAspectRatioSelected(ratio: String) {
        _uiState.update { it.copy(aspectRatio = ratio) }
    }

    fun generate() {
        val state = _uiState.value
        val p = state.prompt.trim()
        if (p.isEmpty()) return

        _uiState.update { it.copy(isGenerating = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val (w, h) = when (state.aspectRatio) {
                    "16:9" -> Pair(1280, 720)
                    "9:16" -> Pair(720, 1280)
                    else -> Pair(1024, 1024)
                }

                val req = ImageGenRequest(
                    prompt = p,
                    negativePrompt = state.negativePrompt,
                    model = state.selectedModel,
                    width = w,
                    height = h
                )

                val result = generateImageUseCase.execute(req)
                _uiState.update { it.copy(currentResult = result, isGenerating = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false, errorMessage = e.localizedMessage) }
            }
        }
    }
}
