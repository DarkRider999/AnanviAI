package com.aiuniverse.app.presentation.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiuniverse.app.presentation.components.CosmicGradientButton
import com.aiuniverse.app.presentation.components.GlassCard
import com.aiuniverse.app.presentation.components.MarkdownText
import com.aiuniverse.app.presentation.theme.CosmicViolet
import com.aiuniverse.app.presentation.theme.DeepObsidian
import com.aiuniverse.app.presentation.theme.ElectricCyan
import com.aiuniverse.app.presentation.theme.NeonEmerald
import com.aiuniverse.app.presentation.theme.SpaceBlack
import com.aiuniverse.app.presentation.theme.TextPrimary
import com.aiuniverse.app.presentation.theme.TextSecondary

@Composable
fun ModelComparisonScreen(
    onNavigateBack: () -> Unit,
    viewModel: ModelComparisonViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepObsidian, SpaceBlack)))
            .statusBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Side-by-Side Model Battle",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Models selected banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    borderColor = CosmicViolet.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Model Alpha", color = CosmicViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = state.modelA?.name ?: "Select Model",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    borderColor = ElectricCyan.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Model Beta", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = state.modelB?.name ?: "Select Model",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.prompt,
                onValueChange = viewModel::onPromptChanged,
                placeholder = { Text("Enter prompt to evaluate concurrently...", color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x1F1C253B)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CosmicViolet,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(14.dp))

            CosmicGradientButton(
                text = if (state.isLoading) "Generating Parallel Responses..." else "Run Battle",
                enabled = !state.isLoading && state.prompt.isNotBlank(),
                onClick = viewModel::runComparison,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ElectricCyan)
                }
            }

            // Results comparison cards
            if (state.result != null) {
                Spacer(modifier = Modifier.height(24.dp))

                val res = state.result!!
                Text(
                    text = "Evaluation Results",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Model A Output
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CosmicViolet.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = res.modelA.name, color = CosmicViolet, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "${res.latencyMsA} ms", color = NeonEmerald, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        MarkdownText(content = res.responseA)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Model B Output
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ElectricCyan.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = res.modelB.name, color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "${res.latencyMsB} ms", color = NeonEmerald, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        MarkdownText(content = res.responseB)
                    }
                }
            }
        }
    }
}
