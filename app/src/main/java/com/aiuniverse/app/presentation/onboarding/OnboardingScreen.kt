package com.aiuniverse.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aiuniverse.app.presentation.components.CosmicGradientButton
import com.aiuniverse.app.presentation.theme.CosmicViolet
import com.aiuniverse.app.presentation.theme.DeepObsidian
import com.aiuniverse.app.presentation.theme.ElectricCyan
import com.aiuniverse.app.presentation.theme.NeonEmerald
import com.aiuniverse.app.presentation.theme.SpaceBlack
import com.aiuniverse.app.presentation.theme.TextPrimary
import com.aiuniverse.app.presentation.theme.TextSecondary
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val slides = listOf(
        OnboardingSlide(
            title = "All Top AI Models in One Hub",
            subtitle = "Access Hugging Face, OpenRouter, Groq, Ollama, and OpenAI without switching multiple applications.",
            icon = Icons.Default.AutoAwesome,
            tint = CosmicViolet
        ),
        OnboardingSlide(
            title = "Ultra-Fast Groq & Free Models",
            subtitle = "Enjoy zero-cost Llama 3.3, Qwen 2.5, DeepSeek R1, and Gemma models streaming at over 300 tokens/sec.",
            icon = Icons.Default.Bolt,
            tint = ElectricCyan
        ),
        OnboardingSlide(
            title = "Side-by-Side Model Battle",
            subtitle = "Compare any two models concurrently in real-time to pick the sharpest answer.",
            icon = Icons.Default.Compare,
            tint = NeonEmerald
        ),
        OnboardingSlide(
            title = "Hardware Encrypted Privacy",
            subtitle = "Your API keys and conversations are stored exclusively on your device using Android Keystore AES-256.",
            icon = Icons.Default.Security,
            tint = CosmicViolet
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepObsidian, SpaceBlack)
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(slide.tint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = slide.icon,
                            contentDescription = slide.title,
                            tint = slide.tint,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = slide.title,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = slide.subtitle,
                        color = TextSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            // Bottom Indicators and Next Button
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 28.dp)
                ) {
                    repeat(slides.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .size(if (isSelected) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) ElectricCyan else Color(0x338EA6D1))
                        )
                    }
                }

                CosmicGradientButton(
                    text = if (pagerState.currentPage == slides.size - 1) "Enter AI Universe" else "Continue",
                    onClick = {
                        if (pagerState.currentPage < slides.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            viewModel.completeOnboarding(onFinish)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
