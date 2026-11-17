package com.aiuniverse.app.presentation.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.AiToolType
import com.aiuniverse.app.domain.usecase.ExecuteAiToolUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ToolsUiState(
    val selectedTool: AiToolType = AiToolType.SUMMARIZE,
    val inputText: String = "",
    val outputText: String = "",
    val additionalParam: String = "",
    val isRunning: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AiToolsViewModel @Inject constructor(
    private val executeAiToolUseCase: ExecuteAiToolUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolsUiState())
    val uiState: StateFlow<ToolsUiState> = _uiState.asStateFlow()

    private var toolJob: Job? = null

    fun selectTool(tool: AiToolType) {
        _uiState.update { it.copy(selectedTool = tool, outputText = "") }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onParamChanged(param: String) {
        _uiState.update { it.copy(additionalParam = param) }
    }

    fun execute() {
        val state = _uiState.value
        val text = state.inputText.trim()
        if (text.isEmpty()) return

        _uiState.update { it.copy(isRunning = true, outputText = "", errorMessage = null) }

        val accumulated = StringBuilder()

        toolJob = viewModelScope.launch {
            executeAiToolUseCase.execute(
                tool = state.selectedTool,
                input = text,
                targetLanguageOrOptions = state.additionalParam
            ).catch { e ->
                _uiState.update { it.copy(isRunning = false, errorMessage = e.localizedMessage) }
            }.onCompletion {
                _uiState.update { it.copy(isRunning = false) }
            }.collect { token ->
                accumulated.append(token)
                _uiState.update { it.copy(outputText = accumulated.toString()) }
            }
        }
    }
}
