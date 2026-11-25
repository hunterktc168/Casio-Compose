package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.StudioTeal
import com.example.ui.theme.StudioTealContainer
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

sealed class MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class Paragraph(val text: String) : MarkdownBlock()
    data class BulletItem(val indentLevel: Int, val text: String) : MarkdownBlock()
    data class OrderedItem(val number: Int, val text: String) : MarkdownBlock()
    data class TableBlock(val headers: List<String>, val rows: List<List<String>>) : MarkdownBlock()
    data class CodeBlock(val code: String) : MarkdownBlock()
    data object Divider : MarkdownBlock()
}

fun parseMarkdownBlocks(markdown: String): List<MarkdownBlock> {
    val lines = markdown.replace("\r\n", "\n").lines()
    val blocks = mutableListOf<MarkdownBlock>()
    var i = 0

    while (i < lines.size) {
        val rawLine = lines[i]
        val trimmed = rawLine.trim()

        when {
            trimmed.isEmpty() -> {
                i++
            }

            trimmed.startsWith("```") -> {
                val codeLines = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].trim().startsWith("```")) {
                    codeLines += lines[i]
                    i++
                }
                if (i < lines.size) i++
                blocks += MarkdownBlock.CodeBlock(codeLines.joinToString("\n"))
            }

            trimmed == "---" || trimmed == "***" -> {
                blocks += MarkdownBlock.Divider
                i++
            }

            trimmed.startsWith("### ") -> {
                blocks += MarkdownBlock.Heading(3, trimmed.removePrefix("### ").trim())
                i++
            }

            trimmed.startsWith("## ") -> {
                blocks += MarkdownBlock.Heading(2, trimmed.removePrefix("## ").trim())
                i++
            }

            trimmed.startsWith("# ") -> {
                blocks += MarkdownBlock.Heading(1, trimmed.removePrefix("# ").trim())
                i++
            }

            trimmed.startsWith("|") && i + 1 < lines.size && isTableSeparator(lines[i + 1]) -> {
                val headers = parseTableRow(trimmed)
                i += 2 // Skip header + separator
                val rows = mutableListOf<List<String>>()
                while (i < lines.size && lines[i].trim().startsWith("|")) {
                    rows += parseTableRow(lines[i].trim())
                    i++
                }
                blocks += MarkdownBlock.TableBlock(headers, rows)
            }

            trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                val leadingSpaces = rawLine.takeWhile { it == ' ' }.length
                val indentLevel = (leadingSpaces / 2).coerceAtMost(2)
                blocks += MarkdownBlock.BulletItem(indentLevel, trimmed.drop(2).trim())
                i++
            }

            isOrderedListLine(trimmed) -> {
                val dotIndex = trimmed.indexOf('.')
                val num = trimmed.substring(0, dotIndex).toIntOrNull() ?: 1
                val body = trimmed.substring(dotIndex + 1).trim()
                blocks += MarkdownBlock.OrderedItem(num, body)
                i++
            }

            else -> {
                val paragraphLines = mutableListOf(trimmed)
                i++
                while (i < lines.size) {
                    val next = lines[i].trim()
                    if (next.isEmpty() ||
                        next.startsWith("#") ||
                        next.startsWith("- ") ||
                        next.startsWith("* ") ||
                        next.startsWith("|") ||
                        next.startsWith("```") ||
                        next == "---" ||
                        isOrderedListLine(next)
                    ) {
                        break
                    }
                    paragraphLines += next
                    i++
                }
                blocks += MarkdownBlock.Paragraph(paragraphLines.joinToString(" "))
            }
        }
    }

    return blocks
}

private fun isTableSeparator(line: String): Boolean {
    val t = line.trim()
    if (!t.startsWith("|")) return false
    val cells = parseTableRow(t)
    return cells.isNotEmpty() && cells.all { cell ->
        val cleaned = cell.replace(":", "").replace("-", "").trim()
        cell.contains("-") && cleaned.isEmpty()
    }
}

private fun parseTableRow(line: String): List<String> {
    return line
        .trim()
        .removePrefix("|")
        .removeSuffix("|")
        .split("|")
        .map { it.trim() }
}

private fun isOrderedListLine(trimmed: String): Boolean {
    val dotIndex = trimmed.indexOf('.')
    if (dotIndex <= 0 || dotIndex > 3) return false
    val prefix = trimmed.substring(0, dotIndex)
    return prefix.all { it.isDigit() } && dotIndex + 1 < trimmed.length && trimmed[dotIndex + 1] == ' '
}

@Composable
fun MarkdownBlockView(block: MarkdownBlock) {
    when (block) {
        is MarkdownBlock.Heading -> {
            when (block.level) {
                1 -> {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
                        Text(
                            text = "DOCUMENTATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTeal
                        )
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.headlineLarge,
                            color = StudioTextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                2 -> {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 2.dp)) {
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.headlineMedium,
                            color = StudioTeal
                        )
                        HorizontalDivider(
                            color = StudioTeal.copy(alpha = 0.25f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }

                else -> {
                    Text(
                        text = block.text,
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        is MarkdownBlock.Paragraph -> {
            Text(
                text = formatInlineMarkdown(block.text),
                style = MaterialTheme.typography.bodyLarge,
                color = StudioTextSecondary
            )
        }

        is MarkdownBlock.BulletItem -> {
            val startPad = (block.indentLevel * 18).dp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = startPad),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 9.dp)
                        .size(if (block.indentLevel == 0) 6.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (block.indentLevel == 0) StudioTeal else StudioTextSecondary)
                )
                Text(
                    text = formatInlineMarkdown(block.text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudioTextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        is MarkdownBlock.OrderedItem -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioPanelElevated, MaterialTheme.shapes.small)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudioTealContainer)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = block.number.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.labelMedium,
                        color = StudioTeal
                    )
                }
                Text(
                    text = formatInlineMarkdown(block.text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = StudioTextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        is MarkdownBlock.TableBlock -> {
            StudioCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioPanelElevated)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        block.headers.forEachIndexed { idx, header ->
                            val weight = if (idx == 0) 0.36f else 0.64f
                            Text(
                                text = header.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioTeal,
                                modifier = Modifier.weight(weight)
                            )
                        }
                    }
                    HorizontalDivider(color = StudioBorder, thickness = 1.dp)
                    block.rows.forEachIndexed { rowIdx, row ->
                        val bg = if (rowIdx % 2 == 1) Color.White.copy(alpha = 0.025f) else Color.Transparent
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bg)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            block.headers.indices.forEach { colIdx ->
                                val cell = row.getOrElse(colIdx) { "" }
                                val weight = if (colIdx == 0) 0.36f else 0.64f
                                Text(
                                    text = formatInlineMarkdown(cell),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (colIdx == 0) StudioTextPrimary else StudioTextSecondary,
                                    modifier = Modifier.weight(weight)
                                )
                            }
                        }
                        if (rowIdx < block.rows.lastIndex) {
                            HorizontalDivider(color = StudioBorderSubtle, thickness = 1.dp)
                        }
                    }
                }
            }
        }

        is MarkdownBlock.CodeBlock -> {
            StudioCard(containerColor = StudioPanelElevated) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(14.dp)
                ) {
                    Text(
                        text = block.code,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = JetBrainsMonoFontFamily
                        ),
                        color = StudioTeal
                    )
                }
            }
        }

        is MarkdownBlock.Divider -> {
            Spacer(modifier = Modifier.height(2.dp))
            HorizontalDivider(color = StudioBorderSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

fun formatInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            when {
                text.startsWith("**", i) -> {
                    val end = text.indexOf("**", i + 2)
                    if (end != -1) {
                        pushStyle(
                            SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = StudioTextPrimary
                            )
                        )
                        append(text.substring(i + 2, end))
                        pop()
                        i = end + 2
                    } else {
                        append(text[i])
                        i++
                    }
                }

                text[i] == '`' -> {
                    val end = text.indexOf('`', i + 1)
                    if (end != -1) {
                        pushStyle(
                            SpanStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                color = StudioTeal,
                                background = StudioTealContainer.copy(alpha = 0.65f)
                            )
                        )
                        append(" ${text.substring(i + 1, end)} ")
                        pop()
                        i = end + 1
                    } else {
                        append(text[i])
                        i++
                    }
                }

                text[i] == '*' -> {
                    val end = text.indexOf('*', i + 1)
                    if (end != -1 && end > i + 1) {
                        pushStyle(
                            SpanStyle(
                                fontStyle = FontStyle.Italic,
                                color = StudioTextPrimary
                            )
                        )
                        append(text.substring(i + 1, end))
                        pop()
                        i = end + 1
                    } else {
                        append(text[i])
                        i++
                    }
                }

                else -> {
                    append(text[i])
                    i++
                }
            }
        }
    }
}
