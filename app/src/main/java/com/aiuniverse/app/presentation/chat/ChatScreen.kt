package com.aiuniverse.app.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiuniverse.app.domain.model.Message
import com.aiuniverse.app.domain.model.MessageRole
import com.aiuniverse.app.presentation.components.GlassCard
import com.aiuniverse.app.presentation.components.MarkdownText
import com.aiuniverse.app.presentation.components.ModelBadge
import com.aiuniverse.app.presentation.theme.AssistantBubbleBackground
import com.aiuniverse.app.presentation.theme.CosmicViolet
import com.aiuniverse.app.presentation.theme.DeepObsidian
import com.aiuniverse.app.presentation.theme.ElectricCyan
import com.aiuniverse.app.presentation.theme.NeonEmerald
import com.aiuniverse.app.presentation.theme.SpaceBlack
import com.aiuniverse.app.presentation.theme.TextPrimary
import com.aiuniverse.app.presentation.theme.TextSecondary
import com.aiuniverse.app.presentation.theme.TextTertiary
import com.aiuniverse.app.presentation.theme.UserBubbleGradientEnd
import com.aiuniverse.app.presentation.theme.UserBubbleGradientStart
import com.aiuniverse.app.presentation.voice.VoiceModalSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current
    var showModelSheet by remember { mutableStateOf(false) }

    LaunchedEffect(state.messages.size, state.messages.lastOrNull()?.content?.length) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DeepObsidian, SpaceBlack))
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Model Selector Pill in Top Bar
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x2E1E283E))
                        .clickable { showModelSheet = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = state.selectedModelId.substringAfterLast("/"),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(onClick = { viewModel.regenerateLastResponse() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = TextSecondary)
                }
            }

            // Message Stream Area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(CosmicViolet.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "How can I assist you today?",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Choose a model, speak, or upload files to begin.",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                items(state.messages) { message ->
                    ChatMessageItem(
                        message = message,
                        onCopy = { clipboardManager.setText(AnnotatedString(message.content)) },
                        onDelete = { viewModel.deleteMessage(message.id) },
                        onRegenerate = { viewModel.regenerateLastResponse() }
                    )
                }
            }

            // Error Banner if network fails
            if (state.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33FF5252))
                        .padding(10.dp)
                ) {
                    Text(
                        text = state.errorMessage ?: "",
                        color = Color(0xFFFF8A80),
                        fontSize = 12.sp
                    )
                }
            }

            // Bottom Input Bar
            ChatInputBar(
                text = state.inputText,
                isStreaming = state.isStreaming,
                onTextChanged = viewModel::onInputChanged,
                onSend = viewModel::sendMessage,
                onStop = viewModel::stopStreaming,
                onMicClick = { viewModel.toggleVoiceModal(true) }
            )
        }

        // Model Switcher Sheet
        if (showModelSheet) {
            ModalBottomSheet(
                onDismissRequest = { showModelSheet = false },
                containerColor = DeepObsidian,
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Switch AI Model",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.availableModels) { model ->
                            val isSelected = model.id == state.selectedModelId
                            GlassCard(
                                onClick = {
                                    viewModel.switchModel(model)
                                    showModelSheet = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = if (isSelected) ElectricCyan else Color(0x228EA6D1)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = model.name, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                        Text(text = "${model.providerId.uppercase()} • ${model.category}", color = TextSecondary, fontSize = 12.sp)
                                    }
                                    if (model.isFree) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(NeonEmerald.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("FREE", color = NeonEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Voice Conversation Modal
        if (state.isVoiceModalOpen) {
            VoiceModalSheet(
                onDismiss = { viewModel.toggleVoiceModal(false) },
                onTranscriptionResult = { spokenText ->
                    viewModel.onInputChanged(spokenText)
                    viewModel.sendMessage()
                }
            )
        }
    }
}

@Composable
fun ChatMessageItem(
    message: Message,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onRegenerate: () -> Unit
) {
    val isUser = message.role == MessageRole.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser && message.modelName != null) {
            ModelBadge(
                modelName = message.modelName.substringAfterLast("/"),
                isFree = message.modelName.contains("free", ignoreCase = true) || message.modelName.contains("llama-3.1-8b", ignoreCase = true),
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .background(
                    if (isUser) {
                        Brush.horizontalGradient(listOf(UserBubbleGradientStart, UserBubbleGradientEnd))
                    } else {
                        Brush.verticalGradient(listOf(AssistantBubbleBackground, Color(0xFF0F1420)))
                    }
                )
                .padding(14.dp)
        ) {
            Column {
                MarkdownText(
                    content = message.content.ifEmpty { if (message.isStreaming) "Thinking..." else "" }
                )
            }
        }

        // Action icons (Copy, Delete, Regenerate)
        Row(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onCopy, modifier = Modifier.size(26.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextTertiary, modifier = Modifier.size(14.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(26.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    isStreaming: Boolean,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onMicClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMicClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0x22FFFFFF))
        ) {
            Icon(Icons.Default.Mic, contentDescription = "Voice", tint = ElectricCyan)
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = onTextChanged,
            placeholder = { Text("Ask anything...", color = TextSecondary, fontSize = 14.sp) },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0x1F1C253B)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CosmicViolet,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { if (isStreaming) onStop() else onSend() },
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isStreaming) Color(0xFFFF4757) else CosmicViolet)
        ) {
            if (isStreaming) {
                Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White)
            } else {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}
