package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cable
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.LaptopMac
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.CdpS100Catalog
import com.example.model.AppTab
import com.example.model.CapabilityItem
import com.example.ui.GuideUiState
import com.example.ui.components.SectionHeader
import com.example.ui.components.SpecRowView
import com.example.ui.components.StudioCard
import com.example.ui.theme.JetBrainsMonoFontFamily
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
fun GuideScreen(
    state: GuideUiState,
    onSearchQueryChange: (String) -> Unit,
    onNavigateTab: (AppTab) -> Unit,
    onOpenDocs: () -> Unit,
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
        item(key = "hero") {
            HeroOverviewCard(
                onNavigateTab = onNavigateTab,
                onOpenDocs = onOpenDocs
            )
        }

        item(key = "capabilities_header") {
            SectionHeader(
                eyebrow = "01 / Introduction",
                title = "Understanding the CDP-S100",
                subtitle = "The Casio CDP-S100 combines a weighted 88-key keyboard with internal piano sounds and USB MIDI connectivity. It can operate independently or communicate with compatible external devices and music software."
            )
        }

        item(key = "capabilities_grid") {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val columns = if (maxWidth >= 600.dp) 3 else 2
                val rows = CdpS100Catalog.capabilities.chunked(columns)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Max),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { item ->
                                CapabilityCard(
                                    item = item,
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

        item(key = "specs_header") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SectionHeader(
                    eyebrow = "02 / Technical Data",
                    title = "Technical specifications"
                )
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("specs_search_input"),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_specs_hint),
                            color = StudioTextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = StudioTeal
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.testTag("clear_specs_search")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.clear_search),
                                    tint = StudioTextMuted
                                )
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioTeal,
                        unfocusedBorderColor = StudioBorder,
                        focusedContainerColor = StudioPanel,
                        unfocusedContainerColor = StudioPanel,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary,
                        cursorColor = StudioTeal
                    )
                )
            }
        }

        if (state.filteredSpecGroups.isEmpty()) {
            item(key = "specs_empty") {
                StudioCard {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = StudioTextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching specifications",
                            style = MaterialTheme.typography.titleMedium,
                            color = StudioTextPrimary
                        )
                        Text(
                            text = "Try searching for keys, polyphony, 10.5 kg, or damper.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StudioTextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(
                count = state.filteredSpecGroups.size,
                key = { idx -> "spec_group_${state.filteredSpecGroups[idx].id}" }
            ) { idx ->
                val group = state.filteredSpecGroups[idx]
                StudioCard {
                    Text(
                        text = group.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = StudioTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                    group.rows.forEachIndexed { rowIndex, row ->
                        SpecRowView(
                            label = row.label,
                            value = row.value,
                            isAlternating = rowIndex % 2 == 1,
                            showDivider = rowIndex < group.rows.lastIndex
                        )
                    }
                }
            }
        }

        item(key = "specs_footnote") {
            Text(
                text = "Specifications are based on the CDP-S100 model documentation. Regional configurations and supplied accessories may vary.",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextMuted
            )
        }
    }
}

@Composable
private fun HeroOverviewCard(
    onNavigateTab: (AppTab) -> Unit,
    onOpenDocs: () -> Unit
) {
    StudioCard(containerColor = StudioPanel) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_piano),
                contentDescription = stringResource(R.string.hero_image_cd),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                StudioInk.copy(alpha = 0.55f),
                                StudioPanel
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StudioTealContainer.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, StudioTeal.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = "PRODUCT PROFILE",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "Compact digital piano",
                    style = MaterialTheme.typography.titleLarge,
                    color = StudioTextPrimary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
            Text(
                text = stringResource(R.string.hero_kicker),
                style = MaterialTheme.typography.labelSmall,
                color = StudioTeal
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "CASIO ",
                    style = MaterialTheme.typography.headlineLarge,
                    color = StudioTextPrimary
                )
                Text(
                    text = "CDP-S100",
                    style = MaterialTheme.typography.headlineLarge,
                    color = StudioTeal
                )
            }
            Text(
                text = stringResource(R.string.hero_description),
                style = MaterialTheme.typography.bodyLarge,
                color = StudioTextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNavigateTab(AppTab.SOUNDS) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("hero_explore_sounds_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioTeal,
                        contentColor = StudioInk
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Sounds & Tools",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                OutlinedButton(
                    onClick = { onNavigateTab(AppTab.MIDI) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("hero_explore_midi_button"),
                    border = BorderStroke(1.dp, StudioBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioTextPrimary),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "USB MIDI Setup",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            OutlinedButton(
                onClick = onOpenDocs,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .heightIn(min = 48.dp)
                    .testTag("hero_open_docs_button"),
                border = BorderStroke(1.dp, StudioTeal.copy(alpha = 0.45f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StudioTeal),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Read App Docs & User Guide",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudioPanelElevated, MaterialTheme.shapes.medium)
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CdpS100Catalog.heroMetrics.forEach { metric ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = metric.value,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily
                            ),
                            color = StudioTextPrimary
                        )
                        Text(
                            text = metric.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CapabilityCard(
    item: CapabilityItem,
    modifier: Modifier = Modifier
) {
    StudioCard(
        modifier = modifier,
        containerColor = StudioPanel
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = StudioTealContainer,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = iconForCapability(item.iconKey),
                        contentDescription = null,
                        tint = StudioTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = StudioTextPrimary
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun iconForCapability(key: String): ImageVector = when (key) {
    "piano" -> Icons.Rounded.Piano
    "usb" -> Icons.Rounded.Cable
    "headphones" -> Icons.Rounded.Headphones
    "smartphone" -> Icons.Rounded.Smartphone
    "laptop" -> Icons.Rounded.LaptopMac
    else -> Icons.Rounded.MusicNote
}
