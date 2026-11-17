package com.aiuniverse.app.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiuniverse.app.presentation.components.GlassCard
import com.aiuniverse.app.presentation.theme.CosmicViolet
import com.aiuniverse.app.presentation.theme.DeepObsidian
import com.aiuniverse.app.presentation.theme.ElectricCyan
import com.aiuniverse.app.presentation.theme.SpaceBlack
import com.aiuniverse.app.presentation.theme.TextPrimary
import com.aiuniverse.app.presentation.theme.TextSecondary

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToApiKeys: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepObsidian, SpaceBlack)))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Preferences & Settings",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Navigation shortcuts
                item {
                    GlassCard(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("User Profile & Personas", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("System prompt customization & token telemetry", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    GlassCard(
                        onClick = onNavigateToApiKeys,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Manage Provider API Keys", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text("Hugging Face, Groq, OpenRouter, Ollama", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Text("Inference Options", color = ElectricCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Stream Responses", color = TextPrimary, fontWeight = FontWeight.Medium)
                                Text("Real-time token delivery", color = TextSecondary, fontSize = 12.sp)
                            }
                            Switch(
                                checked = settings.streamResponses,
                                onCheckedChange = viewModel::toggleStreaming,
                                colors = SwitchDefaults.colors(checkedThumbColor = CosmicViolet)
                            )
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Haptic Feedback", color = TextPrimary, fontWeight = FontWeight.Medium)
                                Text("Tactile pulses on token arrivals", color = TextSecondary, fontSize = 12.sp)
                            }
                            Switch(
                                checked = settings.hapticFeedback,
                                onCheckedChange = viewModel::toggleHaptics,
                                colors = SwitchDefaults.colors(checkedThumbColor = CosmicViolet)
                            )
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sampling Temperature", color = TextPrimary, fontWeight = FontWeight.Medium)
                                Text(String.format("%.2f", settings.temperature), color = ElectricCyan, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = settings.temperature,
                                onValueChange = viewModel::updateTemperature,
                                valueRange = 0.0f..1.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CosmicViolet,
                                    activeTrackColor = CosmicViolet
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
