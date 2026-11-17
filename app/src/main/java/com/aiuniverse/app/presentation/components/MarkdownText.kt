package com.aiuniverse.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiuniverse.app.presentation.theme.CosmicViolet
import com.aiuniverse.app.presentation.theme.ElectricCyan
import com.aiuniverse.app.presentation.theme.TextPrimary

@Composable
fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier,
    color: Color = TextPrimary
) {
    val blocks = remember(content) {
        parseMarkdownBlocks(content)
    }

    Column(modifier = modifier) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Code -> {
                    CodeBlockView(
                        code = block.code,
                        language = block.language,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
                is MarkdownBlock.Header -> {
                    Text(
                        text = block.text,
                        style = when (block.level) {
                            1 -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = ElectricCyan)
                            2 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, color = CosmicViolet)
                            else -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                is MarkdownBlock.Paragraph -> {
                    val annotated = remember(block.text) {
                        parseInlineMarkdown(block.text, color)
                    }
                    Text(
                        text = annotated,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                is MarkdownBlock.Bullet -> {
                    val annotated = remember(block.text) {
                        parseInlineMarkdown("•  ${block.text}", color)
                    }
                    Text(
                        text = annotated,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

sealed class MarkdownBlock {
    data class Paragraph(val text: String) : MarkdownBlock()
    data class Header(val level: Int, val text: String) : MarkdownBlock()
    data class Code(val language: String, val code: String) : MarkdownBlock()
    data class Bullet(val text: String) : MarkdownBlock()
}

private fun parseMarkdownBlocks(raw: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = raw.split("\n")

    var inCodeBlock = false
    var codeLang = ""
    val codeAccumulator = StringBuilder()

    for (line in lines) {
        if (line.trim().startsWith("```")) {
            if (inCodeBlock) {
                blocks.add(MarkdownBlock.Code(language = codeLang.ifEmpty { "text" }, code = codeAccumulator.toString().trimEnd()))
                codeAccumulator.clear()
                inCodeBlock = false
                codeLang = ""
            } else {
                inCodeBlock = true
                codeLang = line.trim().removePrefix("```").trim()
            }
            continue
        }

        if (inCodeBlock) {
            codeAccumulator.append(line).append("\n")
            continue
        }

        val trimmed = line.trim()
        when {
            trimmed.startsWith("### ") -> blocks.add(MarkdownBlock.Header(3, trimmed.removePrefix("### ")))
            trimmed.startsWith("## ") -> blocks.add(MarkdownBlock.Header(2, trimmed.removePrefix("## ")))
            trimmed.startsWith("# ") -> blocks.add(MarkdownBlock.Header(1, trimmed.removePrefix("# ")))
            trimmed.startsWith("- ") || trimmed.startsWith("* ") -> blocks.add(MarkdownBlock.Bullet(trimmed.substring(2)))
            trimmed.isNotEmpty() -> blocks.add(MarkdownBlock.Paragraph(line))
        }
    }

    if (inCodeBlock && codeAccumulator.isNotEmpty()) {
        blocks.add(MarkdownBlock.Code(language = codeLang.ifEmpty { "text" }, code = codeAccumulator.toString()))
    }

    return blocks
}

private fun parseInlineMarkdown(text: String, defaultColor: Color): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            if (text.startsWith("**", i)) {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor))
                    append(text.substring(i + 2, end))
                    pop()
                    i = end + 2
                    continue
                }
            } else if (text.startsWith("*", i) && !text.startsWith("**", i)) {
                val end = text.indexOf("*", i + 1)
                if (end != -1) {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = defaultColor))
                    append(text.substring(i + 1, end))
                    pop()
                    i = end + 1
                    continue
                }
            } else if (text.startsWith("`", i)) {
                val end = text.indexOf("`", i + 1)
                if (end != -1) {
                    pushStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0x334B5563),
                            color = ElectricCyan,
                            fontSize = 13.sp
                        )
                    )
                    append(" ${text.substring(i + 1, end)} ")
                    pop()
                    i = end + 1
                    continue
                }
            }

            append(text[i])
            i++
        }
    }
}
