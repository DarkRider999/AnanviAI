package com.aiuniverse.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiuniverse.app.domain.model.ChatSession
import com.aiuniverse.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    val query: StateFlow<String> = searchQuery.asStateFlow()

    val sessions: StateFlow<List<ChatSession>> = searchQuery.flatMapLatest { q ->
        if (q.isBlank()) {
            chatRepository.getSessions()
        } else {
            chatRepository.searchSessions(q)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun pinSession(sessionId: String, isPinned: Boolean) {
        viewModelScope.launch {
            chatRepository.pinSession(sessionId, isPinned)
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
        }
    }
}
