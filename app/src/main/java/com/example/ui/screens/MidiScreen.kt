package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Cable
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CdpS100Catalog
import com.example.model.MidiEventType
import com.example.ui.MidiUiState
import com.example.ui.components.CodePill
import com.example.ui.components.PianoKeyboardVisualizer
import com.example.ui.components.SectionHeader
import com.example.ui.components.StudioCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioAmberContainer
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioError
import com.example.ui.theme.StudioPanel
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.StudioSuccess
import com.example.ui.theme.StudioTeal
import com.example.ui.theme.StudioTealContainer
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MidiScreen(
    state: MidiUiState,
    onRefreshDevices: () -> Unit,
    onSelectDevice: (Int) -> Unit,
    onDisconnectDevice: () -> Unit,
    onSendTestNote: () -> Unit,
    onSendAllNotesOff: () -> Unit,
    onClearLog: () -> Unit,
    onToggleOtgStep: (Int) -> Unit,
    onResetOtgChecklist: () -> Unit,
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
        item(key = "live_midi_monitor") {
            LiveMidiMonitorCard(
                state = state,
                onRefreshDevices = onRefreshDevices,
                onSelectDevice = onSelectDevice,
                onDisconnectDevice = onDisconnectDevice,
                onSendTestNote = onSendTestNote,
                onSendAllNotesOff = onSendAllNotesOff,
                onClearLog = onClearLog
            )
        }

        item(key = "android_otg_header") {
            SectionHeader(
                eyebrow = "07 / Mobile Integration",
                title = "Android phone integration",
                subtitle = "Connect an Android device to the CDP-S100 using a wired USB connection. A suitable USB OTG adapter and data-capable cable are required."
            )
        }

        item(key = "otg_connection_chain") {
            StudioCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Connection chain",
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ChainNode(
                            icon = Icons.Rounded.Smartphone,
                            title = "Android",
                            subtitle = "USB-C / Host",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = null,
                            tint = StudioTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        ChainNode(
                            icon = Icons.Rounded.Cable,
                            title = "OTG + USB",
                            subtitle = "Data cable",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Rounded.SwapHoriz,
                            contentDescription = null,
                            tint = StudioTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        ChainNode(
                            icon = Icons.Rounded.Piano,
                            title = "CDP-S100",
                            subtitle = "USB Type-B",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "Actual cable arrangement depends on the phone connector and adapter. Use a data-capable USB cable.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioTextMuted,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }

        item(key = "otg_checklist") {
            OtgSetupChecklistCard(
                completedStepIds = state.completedOtgStepIds,
                onToggleStep = onToggleOtgStep,
                onResetChecklist = onResetOtgChecklist
            )
        }

        item(key = "casio_music_space") {
            StudioCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CASIO MUSIC SPACE",
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary
                    )
                    Text(
                        text = "Casio's companion application provides supported piano control and learning functions.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = StudioTextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CdpS100Catalog.musicSpaceFeatures.forEach { feature ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(StudioPanelElevated, MaterialTheme.shapes.small)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = feature.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = StudioTeal
                                )
                                Text(
                                    text = feature.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StudioTextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "connectivity_header") {
            SectionHeader(
                eyebrow = "04 / Hardware Interfaces",
                title = "Connectivity explained",
                subtitle = "Understanding the difference between MIDI and audio is essential to using the CDP-S100 with external equipment."
            )
        }

        item(key = "midi_vs_audio_cards") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StudioCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("USB TYPE-B", style = MaterialTheme.typography.labelSmall, color = StudioTeal)
                        Text(
                            text = "MIDI connection (Instructions)",
                            style = MaterialTheme.typography.titleLarge,
                            color = StudioTextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Communicates musical performance data to and from compatible computers and mobile devices.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StudioTextSecondary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        val items = listOf(
                            "Note On and Note Off events",
                            "Note velocity & performance timing",
                            "Supported MIDI control messages",
                            "External MIDI instrument playback"
                        )
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items.forEach { line ->
                                Text(
                                    text = "• $line",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StudioTextPrimary
                                )
                            }
                        }
                    }
                }

                StudioCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AUDIO", style = MaterialTheme.typography.labelSmall, color = StudioAmber)
                        Text(
                            text = "Sound connections (Waveforms)",
                            style = MaterialTheme.typography.titleLarge,
                            color = StudioTextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Handles actual sound signals rather than musical event data.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StudioTextSecondary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        val items = listOf(
                            "Audio In: external sound input",
                            "Phones/Output: headphone and audio output",
                            "External amplification",
                            "Audio recording through suitable equipment"
                        )
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items.forEach { line ->
                                Text(
                                    text = "• $line",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StudioTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "physical_ports") {
            StudioCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Physical connection reference",
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CdpS100Catalog.portReferences.forEach { port ->
                            Column(
                                modifier = Modifier
                                    .background(StudioPanelElevated, MaterialTheme.shapes.small)
                                    .padding(10.dp)
                            ) {
                                CodePill(text = port.badge, isAudioDomain = port.isAudioDomain)
                                Text(
                                    text = port.purpose,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StudioTextSecondary,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item(key = "midi_capabilities") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    eyebrow = "05 / MIDI Architecture",
                    title = "MIDI capabilities",
                    subtitle = "MIDI allows the keyboard to communicate with external music applications without transmitting its actual audio."
                )

                StudioCard {
                    CdpS100Catalog.midiCapabilities.forEachIndexed { idx, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.capability,
                                style = MaterialTheme.typography.bodyMedium,
                                color = StudioTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (row.isSupported) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                                    contentDescription = null,
                                    tint = if (row.isSupported) StudioSuccess else StudioError,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = row.support,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily
                                    ),
                                    color = if (row.isSupported) StudioTextPrimary else StudioTextMuted
                                )
                            }
                        }
                        if (idx < CdpS100Catalog.midiCapabilities.lastIndex) {
                            HorizontalDivider(color = StudioBorderSubtle, thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveMidiMonitorCard(
    state: MidiUiState,
    onRefreshDevices: () -> Unit,
    onSelectDevice: (Int) -> Unit,
    onDisconnectDevice: () -> Unit,
    onSendTestNote: () -> Unit,
    onSendAllNotesOff: () -> Unit,
    onClearLog: () -> Unit
) {
    StudioCard(
        borderColor = if (state.isConnected) StudioTeal else StudioBorder
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    state.isConnected -> StudioSuccess
                                    state.devices.isNotEmpty() -> StudioAmber
                                    else -> StudioTextMuted
                                }
                            )
                    )
                    Column {
                        Text(
                            text = "LIVE USB MIDI MONITOR",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTeal
                        )
                        Text(
                            text = state.statusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onRefreshDevices,
                    modifier = Modifier.testTag("midi_scan_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.midi_refresh),
                        tint = StudioTeal
                    )
                }
            }

            if (state.devices.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.devices.forEach { device ->
                        val selected = state.selectedDeviceId == device.id && state.isConnected
                        FilterChip(
                            selected = selected,
                            onClick = {
                                if (selected) onDisconnectDevice() else onSelectDevice(device.id)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Usb,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(device.name) },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("midi_device_${device.id}"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioTealContainer,
                                selectedLabelColor = StudioTeal,
                                selectedLeadingIconColor = StudioTeal,
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            PianoKeyboardVisualizer(activeNotes = state.activeNotes)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSendTestNote,
                    enabled = state.isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("midi_test_c4_button"),
                    border = BorderStroke(1.dp, StudioBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = StudioTeal,
                        disabledContentColor = StudioTextMuted
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Icons.Rounded.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test C4")
                }

                OutlinedButton(
                    onClick = onSendAllNotesOff,
                    enabled = state.isConnected,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("midi_panic_button"),
                    border = BorderStroke(1.dp, StudioBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = StudioAmber,
                        disabledContentColor = StudioTextMuted
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Icons.Rounded.VolumeOff, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("All Off")
                }

                IconButton(
                    onClick = onClearLog,
                    enabled = state.recentEvents.isNotEmpty(),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("midi_clear_log_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ClearAll,
                        contentDescription = stringResource(R.string.midi_clear_log),
                        tint = if (state.recentEvents.isNotEmpty()) StudioTextPrimary else StudioTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.recentEvents.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StudioPanelElevated, MaterialTheme.shapes.medium)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Usb,
                        contentDescription = null,
                        tint = StudioTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (state.isConnected) {
                            "Listening for keystrokes, velocity & damper pedal…"
                        } else {
                            "Plug in your Casio CDP-S100 via USB OTG to monitor real-time Note On/Off, Velocity, and CC messages."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioTextMuted,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StudioPanelElevated, MaterialTheme.shapes.medium)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    state.recentEvents.take(15).forEach { event ->
                        val typeColor = when (event.type) {
                            MidiEventType.NOTE_ON -> StudioTeal
                            MidiEventType.NOTE_OFF -> StudioTextMuted
                            MidiEventType.CONTROL_CHANGE -> StudioAmber
                            else -> StudioTextPrimary
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CH${event.channel} · ${event.summary}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily
                                ),
                                color = typeColor,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = event.rawHex,
                                style = MaterialTheme.typography.labelSmall,
                                color = StudioTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChainNode(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(StudioPanelElevated, MaterialTheme.shapes.medium)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = StudioTeal,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = StudioTextPrimary,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = StudioTextMuted
        )
    }
}

@Composable
private fun OtgSetupChecklistCard(
    completedStepIds: Set<Int>,
    onToggleStep: (Int) -> Unit,
    onResetChecklist: () -> Unit
) {
    val totalSteps = CdpS100Catalog.otgSetupSteps.size
    val doneCount = completedStepIds.size
    val progress = if (totalSteps > 0) doneCount.toFloat() / totalSteps else 0f

    StudioCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SETUP INSTRUCTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTeal
                    )
                    Text(
                        text = "Android USB OTG checklist ($doneCount/$totalSteps)",
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (doneCount > 0) {
                    TextButton(
                        onClick = onResetChecklist,
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("otg_reset_button")
                    ) {
                        Text("Reset", color = StudioTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = StudioTeal,
                trackColor = StudioPanelElevated
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CdpS100Catalog.otgSetupSteps.forEach { step ->
                    val completed = completedStepIds.contains(step.stepNumber)
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (completed) StudioTealContainer.copy(alpha = 0.45f) else StudioPanelElevated,
                        border = BorderStroke(
                            1.dp,
                            if (completed) StudioTeal.copy(alpha = 0.5f) else StudioBorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleStep(step.stepNumber) }
                            .testTag("otg_step_${step.stepNumber}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (completed) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = if (completed) "Completed" else "Not completed",
                                tint = if (completed) StudioTeal else StudioTextMuted,
                                modifier = Modifier.padding(top = 2.dp).size(22.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = step.numberLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = StudioTeal
                                    )
                                    Text(
                                        text = step.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = StudioTextPrimary
                                    )
                                }
                                Text(
                                    text = step.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StudioTextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
