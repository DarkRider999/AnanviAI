package com.aiuniverse.app.presentation.imagegen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aiuniverse.app.presentation.components.CosmicGradientButton
import com.aiuniverse.app.presentation.components.GlassCard
import com.aiuniverse.app.presentation.theme.CosmicViolet
import com.aiuniverse.app.presentation.theme.DeepObsidian
import com.aiuniverse.app.presentation.theme.ElectricCyan
import com.aiuniverse.app.presentation.theme.SpaceBlack
import com.aiuniverse.app.presentation.theme.TextPrimary
import com.aiuniverse.app.presentation.theme.TextSecondary

@Composable
fun ImageGenerationScreen(
    onNavigateBack: () -> Unit,
    viewModel: ImageGenViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val recentGenerations by viewModel.recentGenerations.collectAsState()

    val models = listOf(
        "FLUX.1 Schnell" to "black-forest-labs/FLUX.1-schnell",
        "SDXL Base 1.0" to "stabilityai/stable-diffusion-xl-base-1.0",
        "Stable Diffusion 2.1" to "stabilityai/stable-diffusion-2-1"
    )

    val aspectRatios = listOf("1:1", "16:9", "9:16")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepObsidian, SpaceBlack)))
            .statusBarsPadding()
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
                    text = "AI Image Studio",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Model Selection Chips
            Text(text = "Diffusion Model", color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(models) { (label, id) ->
                    val isSelected = state.selectedModel == id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) CosmicViolet else Color(0x1F222F47))
                            .clickable { viewModel.onModelSelected(id) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Aspect Ratio
            Text(text = "Aspect Ratio", color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                aspectRatios.forEach { ratio ->
                    val isSelected = state.aspectRatio == ratio
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricCyan.copy(alpha = 0.2f) else Color(0x1F222F47))
                            .clickable { viewModel.onAspectRatioSelected(ratio) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = ratio,
                            color = if (isSelected) ElectricCyan else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prompt Input
            OutlinedTextField(
                value = state.prompt,
                onValueChange = viewModel::onPromptChanged,
                placeholder = { Text("A futuristic cyberpunk city with neon reflections and holographic AI towers...", color = TextSecondary) },
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
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(14.dp))

            CosmicGradientButton(
                text = if (state.isGenerating) "Generating Masterpiece..." else "Generate Image",
                enabled = !state.isGenerating && state.prompt.isNotBlank(),
                onClick = viewModel::generate,
                modifier = Modifier.fillMaxWidth()
            )

            // Preview Container
            Spacer(modifier = Modifier.height(24.dp))

            if (state.isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x181F273E)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = CosmicViolet)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Synthesizing latent diffusion tensors...", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            } else if (state.currentResult != null) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = state.currentResult!!.imageUrl,
                            contentDescription = state.currentResult!!.prompt,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.currentResult!!.prompt,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            maxLines = 2
                        )
                    }
                }
            }

            // Recent Gallery
            if (recentGenerations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Recent Creations", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(recentGenerations) { item ->
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
