package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Cable
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CdpS100Catalog
import com.example.model.ProductionWorkflow
import com.example.model.SoftwareIntegration
import com.example.ui.StudioUiState
import com.example.ui.components.CodePill
import com.example.ui.components.SectionHeader
import com.example.ui.components.StudioCard
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioAmberContainer
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioPanel
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.StudioTeal
import com.example.ui.theme.StudioTealContainer
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary
import com.example.ui.theme.StudioTextSecondary

@Composable
fun StudioScreen(
    state: StudioUiState,
    onSelectWorkflow: (String) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val selectedWorkflow = CdpS100Catalog.productionWorkflows
        .firstOrNull { it.id == state.selectedWorkflowId }
        ?: CdpS100Catalog.productionWorkflows.first()

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
        item(key = "production_header") {
            SectionHeader(
                eyebrow = "08 / Workflow",
                title = "Music production workflows",
                subtitle = "Because the CDP-S100 transmits MIDI over USB without acting as a USB audio interface, recording workflows depend on whether you want to capture virtual instruments or the piano's internal tones."
            )
        }

        item(key = "workflow_switcher") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CdpS100Catalog.productionWorkflows.forEach { wf ->
                        val selected = wf.id == selectedWorkflow.id
                        val accent = if (wf.isAudioPath) StudioAmber else StudioTeal
                        val container = if (wf.isAudioPath) StudioAmberContainer else StudioTealContainer
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectWorkflow(wf.id) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (wf.isAudioPath) Icons.Rounded.Cable else Icons.Rounded.Usb,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = if (wf.isAudioPath) "Analog Audio" else "USB MIDI to DAW",
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .testTag("workflow_tab_${wf.id}"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = container,
                                selectedLabelColor = accent,
                                selectedLeadingIconColor = accent,
                                containerColor = StudioPanel,
                                labelColor = StudioTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = StudioBorder,
                                selectedBorderColor = accent
                            )
                        )
                    }
                }

                AnimatedContent(
                    targetState = selectedWorkflow,
                    label = "workflow_detail"
                ) { workflow ->
                    WorkflowDetailCard(workflow = workflow)
                }
            }
        }

        item(key = "software_header") {
            SectionHeader(
                eyebrow = "06 / Software Ecosystem",
                title = "Software integrations",
                subtitle = "The CDP-S100 can work with a broad range of MIDI-compatible music applications. The software provides capabilities that are not built into the piano itself."
            )
        }

        item(key = "software_grid") {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val columns = if (maxWidth >= 600.dp) 2 else 1
                val rows = CdpS100Catalog.softwareIntegrations.chunked(columns)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { software ->
                                SoftwareIntegrationCard(
                                    item = software,
                                    onOpenUrl = { url -> runCatching { uriHandler.openUri(url) } },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                            if (rowItems.size < columns) {
                                repeat(columns - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkflowDetailCard(workflow: ProductionWorkflow) {
    val accent = if (workflow.isAudioPath) StudioAmber else StudioTeal
    StudioCard(borderColor = accent.copy(alpha = 0.55f)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = workflow.badge,
                style = MaterialTheme.typography.labelSmall,
                color = accent
            )
            Text(
                text = workflow.title,
                style = MaterialTheme.typography.headlineMedium,
                color = StudioTextPrimary,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = workflow.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = StudioTextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SIGNAL FLOW",
                style = MaterialTheme.typography.labelSmall,
                color = StudioTextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                workflow.signalChain.forEachIndexed { idx, node ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioPanelElevated, MaterialTheme.shapes.small)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "0${idx + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = accent
                            )
                            Text(
                                text = node,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily
                                ),
                                color = StudioTextPrimary
                            )
                        }
                        if (idx < workflow.signalChain.lastIndex) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Album,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoftwareIntegrationCard(
    item: SoftwareIntegration,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    StudioCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.vendorTag,
                style = MaterialTheme.typography.labelSmall,
                color = StudioTeal
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                color = StudioTextPrimary,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = StudioTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            if (item.url != null) {
                OutlinedButton(
                    onClick = { onOpenUrl(item.url) },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("software_link_${item.id}"),
                    border = BorderStroke(1.dp, StudioTeal.copy(alpha = 0.45f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioTeal),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(stringResource(R.string.open_website))
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                CodePill(text = item.footerLabel)
            }
        }
    }
}
