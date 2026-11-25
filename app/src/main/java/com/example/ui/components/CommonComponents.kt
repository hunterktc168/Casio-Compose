package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioAmberContainer
import com.example.ui.theme.StudioBlackKey
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioKeybedBg
import com.example.ui.theme.StudioPanel
import com.example.ui.theme.StudioTeal
import com.example.ui.theme.StudioTealContainer
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioWhiteKey

@Composable
fun SectionHeader(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = StudioTeal
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = StudioTextPrimary,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun StudioCard(
    modifier: Modifier = Modifier,
    borderColor: Color = StudioBorder,
    containerColor: Color = StudioPanel,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        tonalElevation = 2.dp
    ) {
        Column(content = content)
    }
}

@Composable
fun SpecRowView(
    label: String,
    value: String,
    isAlternating: Boolean,
    showDivider: Boolean
) {
    val bg = if (isAlternating) Color.White.copy(alpha = 0.025f) else Color.Transparent
    Column(modifier = Modifier.fillMaxWidth().background(bg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = StudioTextMuted,
                modifier = Modifier.weight(1f).padding(end = 12.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontFamily = JetBrainsMonoFontFamily
                ),
                color = StudioTextPrimary
            )
        }
        if (showDivider) {
            HorizontalDivider(color = StudioBorderSubtle, thickness = 1.dp)
        }
    }
}

@Composable
fun CodePill(
    text: String,
    isAudioDomain: Boolean = false,
    modifier: Modifier = Modifier
) {
    val accent = if (isAudioDomain) StudioAmber else StudioTeal
    val bg = if (isAudioDomain) StudioAmberContainer.copy(alpha = 0.6f) else StudioTealContainer.copy(alpha = 0.6f)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

/**
 * Draws a compact 88-key piano strip (MIDI notes 21 [A0] .. 108 [C8])
 * highlighting any active pressed MIDI notes in glowing teal.
 */
@Composable
fun PianoKeyboardVisualizer(
    activeNotes: Set<Int>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(StudioKeybedBg, RoundedCornerShape(10.dp))
            .padding(6.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(52.dp)) {
            val firstMidiNote = 21 // A0
            val lastMidiNote = 108 // C8
            val whiteNotes = (firstMidiNote..lastMidiNote).filter { !isBlackKey(it) }
            val whiteCount = whiteNotes.size.coerceAtLeast(1)
            val whiteWidth = size.width / whiteCount
            val whiteGap = 1.2f

            // Draw 52 white keys
            whiteNotes.forEachIndexed { index, midiNote ->
                val isActive = activeNotes.contains(midiNote)
                val x = index * whiteWidth
                drawRoundRect(
                    color = if (isActive) StudioTeal else StudioWhiteKey,
                    topLeft = Offset(x + whiteGap * 0.5f, 0f),
                    size = Size((whiteWidth - whiteGap).coerceAtLeast(1f), size.height),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }

            // Draw 36 black keys
            var whiteIndex = -1
            for (midiNote in firstMidiNote..lastMidiNote) {
                if (!isBlackKey(midiNote)) {
                    whiteIndex++
                } else {
                    val isActive = activeNotes.contains(midiNote)
                    val blackWidth = whiteWidth * 0.65f
                    val blackHeight = size.height * 0.62f
                    val centerX = (whiteIndex + 1) * whiteWidth
                    drawRoundRect(
                        color = if (isActive) StudioAmber else StudioBlackKey,
                        topLeft = Offset(centerX - blackWidth / 2f, 0f),
                        size = Size(blackWidth, blackHeight),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                }
            }
        }
    }
}

private fun isBlackKey(midiNote: Int): Boolean {
    return when (midiNote % 12) {
        1, 3, 6, 8, 10 -> true
        else -> false
    }
}
