package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CdpS100Catalog
import com.example.model.PianoTone
import com.example.model.ToneCategory
import com.example.ui.SoundsUiState
import com.example.ui.components.CodePill
import com.example.ui.components.SectionHeader
import com.example.ui.components.StudioCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioAmberContainer
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioInk
import com.example.ui.theme.StudioPanel
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.StudioTeal
import com.example.ui.theme.StudioTealContainer
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun SoundsScreen(
    state: SoundsUiState,
    onSelectCategory: (ToneCategory) -> Unit,
    onSelectPrimaryTone: (Int) -> Unit,
    onSelectSecondaryTone: (Int, Boolean?) -> Unit,
    onSetLayerEnabled: (Boolean) -> Unit,
    onSetReverbLevel: (Int) -> Unit,
    onSetChorusLevel: (Int) -> Unit,
    onSendToneToMidi: () -> Unit,
    onToggleMetronome: () -> Unit,
    onSetMetronomeBpm: (Int) -> Unit,
    onSetMetronomeBeats: (Int) -> Unit,
    onTapTempo: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item(key = "metronome_card") {
            MetronomeStudioCard(
                bpm = state.metronomeBpm,
                beatsPerBar = state.metronomeBeatsPerBar,
                isPlaying = state.metronomePlaying,
                currentBeat = state.currentBeatIndex,
                beatTickCounter = state.beatTickCounter,
                onTogglePlay = onToggleMetronome,
                onBpmChange = onSetMetronomeBpm,
                onBeatsChange = onSetMetronomeBeats,
                onTapTempo = onTapTempo
            )
        }

        item(key = "layer_fx_explorer") {
            ToneLayerAndEffectsCard(
                state = state,
                onSelectSecondaryTone = onSelectSecondaryTone,
                onSetLayerEnabled = onSetLayerEnabled,
                onSetReverbLevel = onSetReverbLevel,
                onSetChorusLevel = onSetChorusLevel,
                onSendToneToMidi = onSendToneToMidi
            )
        }

        item(key = "tones_header") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    eyebrow = "03 / Sound Engine",
                    title = "Internal sounds & effects",
                    subtitle = "10 preset tones with Layer capability and onboard Reverb & Chorus. Tap any tone below to load it into the Tone Explorer."
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ToneCategory.entries.forEach { category ->
                        val selected = state.selectedCategory == category
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectCategory(category) },
                            label = { Text(category.label) },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("tone_filter_${category.name.lowercase()}"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioTealContainer,
                                selectedLabelColor = StudioTeal,
                                containerColor = StudioPanel,
                                labelColor = StudioTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = StudioBorder,
                                selectedBorderColor = StudioTeal
                            )
                        )
                    }
                }
            }
        }

        items(
            count = state.filteredTones.size,
            key = { index -> "tone_${state.filteredTones[index].id}" }
        ) { index ->
            val tone = state.filteredTones[index]
            val isPrimary = state.primaryToneId == tone.id
            val isSecondary = state.layerEnabled && state.secondaryToneId == tone.id
            PresetToneCard(
                tone = tone,
                isPrimary = isPrimary,
                isSecondary = isSecondary,
                onSelectPrimary = { onSelectPrimaryTone(tone.id) },
                onSelectSecondary = { onSelectSecondaryTone(tone.id, true) }
            )
        }
    }
}

@Composable
private fun MetronomeStudioCard(
    bpm: Int,
    beatsPerBar: Int,
    isPlaying: Boolean,
    currentBeat: Int,
    beatTickCounter: Long,
    onTogglePlay: () -> Unit,
    onBpmChange: (Int) -> Unit,
    onBeatsChange: (Int) -> Unit,
    onTapTempo: () -> Unit
) {
    StudioCard {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STUDIO METRONOME",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTeal
                    )
                    Text(
                        text = "20–255 BPM precision click",
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                CodePill(text = if (beatsPerBar == 0) "FREE" else "$beatsPerBar / 4")
            }

            Spacer(modifier = Modifier.height(16.dp))

            val dotCount = if (beatsPerBar <= 0) 1 else beatsPerBar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioPanelElevated, MaterialTheme.shapes.medium)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (beat in 1..dotCount) {
                        val isActive = if (beatsPerBar <= 0) {
                            isPlaying && (beatTickCounter % 2L == 1L)
                        } else {
                            isPlaying && currentBeat == beat
                        }
                        val targetColor = when {
                            beatsPerBar <= 0 && isPlaying && (beatTickCounter % 2L == 0L) -> StudioAmber
                            isActive && beat == 1 && dotCount > 1 -> StudioAmber
                            isActive -> StudioTeal
                            else -> StudioBorder
                        }
                        val color by animateColorAsState(
                            targetValue = targetColor,
                            animationSpec = spring(),
                            label = "beat_dot_color"
                        )
                        Box(
                            modifier = Modifier
                                .size(if (isActive) 16.dp else 12.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                Text(
                    text = "$bpm BPM",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily
                    ),
                    color = if (isPlaying) StudioTeal else StudioTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedIconButton(
                    onClick = { onBpmChange(bpm - 1) },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("metronome_bpm_minus"),
                    border = BorderStroke(1.dp, StudioBorder),
                    colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = StudioTextPrimary)
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Decrease BPM")
                }

                Slider(
                    value = bpm.toFloat(),
                    onValueChange = { onBpmChange(it.toInt()) },
                    valueRange = 20f..255f,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metronome_bpm_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = StudioTeal,
                        activeTrackColor = StudioTeal,
                        inactiveTrackColor = StudioBorder
                    )
                )

                OutlinedIconButton(
                    onClick = { onBpmChange(bpm + 1) },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("metronome_bpm_plus"),
                    border = BorderStroke(1.dp, StudioBorder),
                    colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = StudioTextPrimary)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Increase BPM")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Beats per measure (CDP-S100 range: 0, 1–9)",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (b in 0..9) {
                    val selected = beatsPerBar == b
                    FilterChip(
                        selected = selected,
                        onClick = { onBeatsChange(b) },
                        label = {
                            Text(
                                text = if (b == 0) "Off" else b.toString(),
                                fontFamily = JetBrainsMonoFontFamily
                            )
                        },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("metronome_beat_$b"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StudioTealContainer,
                            selectedLabelColor = StudioTeal,
                            containerColor = StudioPanelElevated,
                            labelColor = StudioTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = StudioBorder,
                            selectedBorderColor = StudioTeal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onTapTempo,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("metronome_tap_button"),
                    border = BorderStroke(1.dp, StudioTeal.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioTeal),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Icons.Rounded.TouchApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tap Tempo")
                }

                Button(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("metronome_toggle_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlaying) StudioAmber else StudioTeal,
                        contentColor = StudioInk
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "Stop" else "Start",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ToneLayerAndEffectsCard(
    state: SoundsUiState,
    onSelectSecondaryTone: (Int, Boolean?) -> Unit,
    onSetLayerEnabled: (Boolean) -> Unit,
    onSetReverbLevel: (Int) -> Unit,
    onSetChorusLevel: (Int) -> Unit,
    onSendToneToMidi: () -> Unit
) {
    val primaryTone = CdpS100Catalog.presetTones.firstOrNull { it.id == state.primaryToneId }
        ?: CdpS100Catalog.presetTones.first()
    val secondaryTone = CdpS100Catalog.presetTones.firstOrNull { it.id == state.secondaryToneId }
        ?: CdpS100Catalog.presetTones.last()
    val reverbSetting = CdpS100Catalog.reverbSettings.firstOrNull { it.level == state.reverbLevel }
        ?: CdpS100Catalog.reverbSettings.first()
    val chorusSetting = CdpS100Catalog.chorusSettings.firstOrNull { it.level == state.chorusLevel }
        ?: CdpS100Catalog.chorusSettings.first()

    StudioCard {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TONE LAYER & FX EXPLORER",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTeal
                    )
                    Text(
                        text = "Combine tones & studio effects",
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.Layers,
                    contentDescription = null,
                    tint = StudioTeal
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioPanelElevated, MaterialTheme.shapes.medium)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PRIMARY TONE", style = MaterialTheme.typography.labelSmall, color = StudioTextMuted)
                    CodePill(text = primaryTone.hardwareShortcut)
                }
                Text(
                    text = "${primaryTone.numberLabel} · ${primaryTone.name}",
                    style = MaterialTheme.typography.titleMedium,
                    color = StudioTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Layer Mode (Dual Tone)",
                            style = MaterialTheme.typography.titleMedium,
                            color = StudioTextPrimary
                        )
                        Text(
                            text = if (state.layerEnabled) {
                                "Layered with ${secondaryTone.numberLabel} · ${secondaryTone.name}"
                            } else {
                                "Single tone active"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.layerEnabled) StudioAmber else StudioTextMuted
                        )
                    }
                    Switch(
                        checked = state.layerEnabled,
                        onCheckedChange = onSetLayerEnabled,
                        modifier = Modifier.testTag("layer_mode_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StudioInk,
                            checkedTrackColor = StudioTeal,
                            uncheckedThumbColor = StudioTextMuted,
                            uncheckedTrackColor = StudioPanel
                        )
                    )
                }
            }

            AnimatedVisibility(visible = state.layerEnabled) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Secondary Layer Tone",
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioAmber
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CdpS100Catalog.presetTones.forEach { tone ->
                            val selected = tone.id == state.secondaryToneId
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectSecondaryTone(tone.id, true) },
                                label = { Text("${tone.numberLabel} ${tone.name}") },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("secondary_tone_${tone.id}"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StudioAmberContainer,
                                    selectedLabelColor = StudioAmber,
                                    containerColor = StudioPanelElevated,
                                    labelColor = StudioTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selected,
                                    borderColor = StudioBorder,
                                    selectedBorderColor = StudioAmber
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Reverb (${reverbSetting.label}) — ${reverbSetting.description}",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CdpS100Catalog.reverbSettings.forEach { fx ->
                    val selected = fx.level == state.reverbLevel
                    FilterChip(
                        selected = selected,
                        onClick = { onSetReverbLevel(fx.level) },
                        label = { Text(fx.label) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("reverb_level_${fx.level}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StudioTealContainer,
                            selectedLabelColor = StudioTeal,
                            containerColor = StudioPanelElevated,
                            labelColor = StudioTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = StudioBorder,
                            selectedBorderColor = StudioTeal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Chorus (${chorusSetting.label}) — ${chorusSetting.description}",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CdpS100Catalog.chorusSettings.forEach { fx ->
                    val selected = fx.level == state.chorusLevel
                    FilterChip(
                        selected = selected,
                        onClick = { onSetChorusLevel(fx.level) },
                        label = { Text(fx.label) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("chorus_level_${fx.level}"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StudioTealContainer,
                            selectedLabelColor = StudioTeal,
                            containerColor = StudioPanelElevated,
                            labelColor = StudioTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = StudioBorder,
                            selectedBorderColor = StudioTeal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSendToneToMidi,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("send_tone_midi_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isMidiConnected) StudioTeal else StudioPanelElevated,
                    contentColor = if (state.isMidiConnected) StudioInk else StudioTextPrimary
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Rounded.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isMidiConnected) {
                        "Send Tone & FX over USB MIDI"
                    } else {
                        "Send Tone & FX (Requires USB MIDI)"
                    }
                )
            }

            if (!state.midiDispatchMessage.isNullOrBlank()) {
                Text(
                    text = state.midiDispatchMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (state.isMidiConnected) StudioTeal else StudioAmber,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PresetToneCard(
    tone: PianoTone,
    isPrimary: Boolean,
    isSecondary: Boolean,
    onSelectPrimary: () -> Unit,
    onSelectSecondary: () -> Unit
) {
    val borderColor = when {
        isPrimary -> StudioTeal
        isSecondary -> StudioAmber
        else -> StudioBorder
    }
    StudioCard(borderColor = borderColor) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSelectPrimary)
                .padding(16.dp)
                .testTag("tone_card_${tone.id}")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tone.numberLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = StudioTeal
                    )
                    if (isPrimary) {
                        CodePill(text = "PRIMARY")
                    }
                    if (isSecondary) {
                        CodePill(text = "LAYER", isAudioDomain = true)
                    }
                }
                Text(
                    text = tone.category.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioTextMuted
                )
            }

            Text(
                text = tone.name,
                style = MaterialTheme.typography.titleLarge,
                color = StudioTextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = tone.description,
                style = MaterialTheme.typography.bodyMedium,
                color = StudioTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = null,
                        tint = StudioTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = tone.hardwareShortcut,
                        style = MaterialTheme.typography.labelMedium,
                        color = StudioTextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StudioPanelElevated,
                    border = BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onSelectSecondary)
                        .testTag("tone_layer_pick_${tone.id}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    ) {
                        Text(
                            text = "+ Layer",
                            style = MaterialTheme.typography.labelMedium,
                            color = StudioAmber
                        )
                    }
                }
            }
        }
    }
}
