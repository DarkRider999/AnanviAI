package com.aiuniverse.app.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.AIModel
import com.aiuniverse.app.domain.model.MediaType
import com.aiuniverse.app.domain.model.Message
import com.aiuniverse.app.domain.model.MessageRole
import com.aiuniverse.app.domain.repository.AIProviderRepository
import com.aiuniverse.app.domain.repository.ChatRepository
import com.aiuniverse.app.domain.usecase.CreateChatSessionUseCase
import com.aiuniverse.app.domain.usecase.GetMessagesUseCase
import com.aiuniverse.app.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val providerRepository: AIProviderRepository,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val createChatSessionUseCase: CreateChatSessionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var streamingJob: Job? = null
    private val sessionIdArg: String? = savedStateHandle.get<String>("sessionId")

    init {
        loadSession(sessionIdArg)
        loadModels()
    }

    private fun loadSession(sessionId: String?) {
        viewModelScope.launch {
            if (sessionId != null) {
                chatRepository.getSession(sessionId).collect { session ->
                    _uiState.update { it.copy(session = session) }
                    if (session != null) {
                        _uiState.update {
                            it.copy(
                                selectedProviderId = session.providerId,
                                selectedModelId = session.modelId
                            )
                        }
                        observeMessages(session.id)
                    }
                }
            } else {
                val newSession = createChatSessionUseCase()
                _uiState.update { it.copy(session = newSession) }
                observeMessages(newSession.id)
            }
        }
    }

    private fun observeMessages(sessionId: String) {
        viewModelScope.launch {
            getMessagesUseCase(sessionId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    private fun loadModels() {
        viewModelScope.launch {
            val models = providerRepository.getAllAvailableModels()
            _uiState.update { it.copy(availableModels = models) }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onAttachmentSelected(uri: String, type: MediaType) {
        _uiState.update { it.copy(attachedUri = uri, attachedMediaType = type) }
    }

    fun removeAttachment() {
        _uiState.update { it.copy(attachedUri = null, attachedMediaType = MediaType.NONE) }
    }

    fun switchModel(model: AIModel) {
        _uiState.update {
            it.copy(
                selectedProviderId = model.providerId,
                selectedModelId = model.id
            )
        }
    }

    fun sendMessage() {
        val currentState = _uiState.value
        val prompt = currentState.inputText.trim()
        val session = currentState.session ?: return

        if (prompt.isEmpty() && currentState.attachedUri == null) return

        _uiState.update {
            it.copy(
                inputText = "",
                isStreaming = true,
                errorMessage = null
            )
        }

        streamingJob = viewModelScope.launch {
            try {
                sendMessageUseCase.execute(
                    sessionId = session.id,
                    userPrompt = prompt,
                    providerId = currentState.selectedProviderId,
                    modelId = currentState.selectedModelId,
                    systemPrompt = session.systemPrompt,
                    mediaUri = currentState.attachedUri,
                    mediaType = currentState.attachedMediaType,
                    onDelta = {},
                    onError = { err ->
                        _uiState.update { it.copy(errorMessage = err.localizedMessage) }
                    },
                    onFinish = {
                        _uiState.update { it.copy(isStreaming = false, attachedUri = null, attachedMediaType = MediaType.NONE) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isStreaming = false,
                        errorMessage = e.localizedMessage
                    )
                }
            }
        }
    }

    fun stopStreaming() {
        streamingJob?.cancel()
        _uiState.update { it.copy(isStreaming = false) }
    }

    fun regenerateLastResponse() {
        val lastUserMessage = _uiState.value.messages.lastOrNull { it.role == MessageRole.USER } ?: return
        _uiState.update { it.copy(inputText = lastUserMessage.content) }
        sendMessage()
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            chatRepository.deleteMessage(messageId)
        }
    }

    fun toggleVoiceModal(isOpen: Boolean) {
        _uiState.update { it.copy(isVoiceModalOpen = isOpen) }
    }
}
