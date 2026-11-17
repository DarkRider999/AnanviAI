package com.aiuniverse.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiuniverse.app.presentation.theme.CodeBackground
import com.aiuniverse.app.presentation.theme.CodeComment
import com.aiuniverse.app.presentation.theme.CodeFunction
import com.aiuniverse.app.presentation.theme.CodeKeyword
import com.aiuniverse.app.presentation.theme.CodeString
import com.aiuniverse.app.presentation.theme.NeonEmerald
import com.aiuniverse.app.presentation.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeBlockView(
    code: String,
    language: String = "code",
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val highlightedCode = remember(code) {
        highlightSyntax(code)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CodeBackground)
    ) {
        // Top Code Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161B28))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.lowercase(),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(
                    visible = isCopied,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Copied!",
                        color = NeonEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code))
                        isCopied = true
                        scope.launch {
                            delay(2000)
                            isCopied = false
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy Code",
                        tint = if (isCopied) NeonEmerald else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Code Content Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            Text(
                text = highlightedCode,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

/**
 * Lightweight syntax highlighter applying SpanStyle rules for keywords, strings, comments.
 */
private fun highlightSyntax(code: String): AnnotatedString {
    return buildAnnotatedString {
        val keywords = setOf(
            "val", "var", "fun", "class", "interface", "import", "package", "return",
            "if", "else", "when", "for", "while", "data", "sealed", "def", "async",
            "await", "const", "let", "function", "export", "from", "public", "private"
        )

        val lines = code.split("\n")
        lines.forEachIndexed { index, line ->
            var cursor = 0
            val tokens = line.split(Regex("(?<=\\s)|(?=\\s)|(?<=[(),;{}])|(?=[(),;{}])"))

            for (token in tokens) {
                when {
                    token.trim().startsWith("//") || token.trim().startsWith("#") -> {
                        val remaining = line.substring(cursor)
                        pushStyle(SpanStyle(color = CodeComment))
                        append(remaining)
                        pop()
                        break
                    }
                    keywords.contains(token.trim()) -> {
                        pushStyle(SpanStyle(color = CodeKeyword, fontWeight = FontWeight.SemiBold))
                        append(token)
                        pop()
                    }
                    token.startsWith("\"") || token.endsWith("\"") -> {
                        pushStyle(SpanStyle(color = CodeString))
                        append(token)
                        pop()
                    }
                    token.endsWith("()") || token.contains(Regex("[a-zA-Z0-9_]+\\(")) -> {
                        pushStyle(SpanStyle(color = CodeFunction))
                        append(token)
                        pop()
                    }
                    else -> {
                        pushStyle(SpanStyle(color = Color(0xFFE2E8F0)))
                        append(token)
                        pop()
                    }
                }
                cursor += token.length
            }
            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
}
