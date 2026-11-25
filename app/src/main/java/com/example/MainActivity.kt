package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Usb
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppTab
import com.example.ui.CdpS100ViewModel
import com.example.ui.screens.DocumentationModal
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.MidiScreen
import com.example.ui.screens.SoundsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.CdpS100Theme
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioInk
import com.example.ui.theme.StudioPanel
import com.example.ui.theme.StudioPanelElevated
import com.example.ui.theme.StudioSuccess
import com.example.ui.theme.StudioTeal
import com.example.ui.theme.StudioTealContainer
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioTextPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            CdpS100Theme {
                CdpS100App()
            }
        }
    }
}

@Composable
fun CdpS100App(
    viewModel: CdpS100ViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val guideState by viewModel.guideState.collectAsStateWithLifecycle()
    val soundsState by viewModel.soundsState.collectAsStateWithLifecycle()
    val midiState by viewModel.midiState.collectAsStateWithLifecycle()
    val studioState by viewModel.studioState.collectAsStateWithLifecycle()
    var showDocsModal by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = currentTab != AppTab.GUIDE && !showDocsModal) {
        viewModel.selectTab(AppTab.GUIDE)
    }

    if (showDocsModal) {
        DocumentationModal(onDismiss = { showDocsModal = false })
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioInk)
    ) {
        val useNavRail = maxWidth >= 700.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = StudioInk,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                StudioTopBar(
                    isMidiConnected = midiState.isConnected,
                    metronomePlaying = soundsState.metronomePlaying,
                    metronomeBpm = soundsState.metronomeBpm,
                    onClickBrand = { viewModel.selectTab(AppTab.GUIDE) },
                    onClickMidiBadge = { viewModel.selectTab(AppTab.MIDI) },
                    onClickTempoBadge = { viewModel.selectTab(AppTab.SOUNDS) },
                    onClickDocs = { showDocsModal = true }
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(color = StudioBorderSubtle, thickness = 1.dp)
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_navigation_bar"),
                            containerColor = StudioPanel,
                            contentColor = StudioTextPrimary,
                            windowInsets = WindowInsets.navigationBars,
                            tonalElevation = 0.dp
                        ) {
                            AppTab.entries.forEach { tab ->
                                val selected = currentTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.selectTab(tab) },
                                    alwaysShowLabel = true,
                                    icon = {
                                        Icon(
                                            imageVector = tabIcon(tab, selected),
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = StudioTeal,
                                        selectedTextColor = StudioTeal,
                                        indicatorColor = StudioTealContainer,
                                        unselectedIconColor = StudioTextMuted,
                                        unselectedTextColor = StudioTextMuted
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                if (useNavRail) {
                    NavigationRail(
                        containerColor = StudioPanel,
                        contentColor = StudioTextPrimary,
                        modifier = Modifier
                            .fillMaxHeight()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        Spacer(modifier = Modifier.weight(0.2f))
                        AppTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = tabIcon(tab, selected),
                                        contentDescription = tab.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = StudioTeal,
                                    selectedTextColor = StudioTeal,
                                    indicatorColor = StudioTealContainer,
                                    unselectedIconColor = StudioTextMuted,
                                    unselectedTextColor = StudioTextMuted
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
                    Crossfade(
                        targetState = currentTab,
                        modifier = Modifier.widthIn(max = 860.dp).fillMaxSize(),
                        label = "tab_crossfade"
                    ) { tab ->
                        when (tab) {
                            AppTab.GUIDE -> GuideScreen(
                                state = guideState,
                                onSearchQueryChange = viewModel::updateSpecSearch,
                                onNavigateTab = viewModel::selectTab,
                                onOpenDocs = { showDocsModal = true },
                                contentPadding = contentPadding
                            )

                            AppTab.SOUNDS -> SoundsScreen(
                                state = soundsState,
                                onSelectCategory = viewModel::selectToneCategory,
                                onSelectPrimaryTone = viewModel::selectPrimaryTone,
                                onSelectSecondaryTone = viewModel::selectSecondaryTone,
                                onSetLayerEnabled = viewModel::setLayerEnabled,
                                onSetReverbLevel = viewModel::setReverbLevel,
                                onSetChorusLevel = viewModel::setChorusLevel,
                                onSendToneToMidi = viewModel::sendToneConfigToConnectedPiano,
                                onToggleMetronome = viewModel::toggleMetronome,
                                onSetMetronomeBpm = viewModel::setMetronomeBpm,
                                onSetMetronomeBeats = viewModel::setMetronomeBeatsPerBar,
                                onTapTempo = viewModel::onTapTempo,
                                contentPadding = contentPadding
                            )

                            AppTab.MIDI -> MidiScreen(
                                state = midiState,
                                onRefreshDevices = viewModel::refreshMidiDevices,
                                onSelectDevice = viewModel::selectMidiDevice,
                                onDisconnectDevice = viewModel::disconnectMidi,
                                onSendTestNote = viewModel::sendTestNoteC4,
                                onSendAllNotesOff = viewModel::sendAllNotesOff,
                                onClearLog = viewModel::clearMidiLog,
                                onToggleOtgStep = viewModel::toggleOtgStep,
                                onResetOtgChecklist = viewModel::resetOtgChecklist,
                                contentPadding = contentPadding
                            )

                            AppTab.STUDIO -> StudioScreen(
                                state = studioState,
                                onSelectWorkflow = viewModel::selectProductionWorkflow,
                                contentPadding = contentPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioTopBar(
    isMidiConnected: Boolean,
    metronomePlaying: Boolean,
    metronomeBpm: Int,
    onClickBrand: () -> Unit,
    onClickMidiBadge: () -> Unit,
    onClickTempoBadge: () -> Unit,
    onClickDocs: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StudioInk.copy(alpha = 0.96f))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onClickBrand)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.brand_prefix))
                        withStyle(SpanStyle(color = StudioTeal)) {
                            append(stringResource(R.string.brand_suffix))
                        }
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = stringResource(R.string.brand_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onClickTempoBadge)
                        .padding(vertical = 6.dp)
                        .testTag("topbar_tempo_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (metronomePlaying) StudioTealContainer else StudioPanelElevated,
                        border = BorderStroke(
                            1.dp,
                            if (metronomePlaying) StudioTeal else StudioBorder
                        )
                    ) {
                        Text(
                            text = "$metronomeBpm BPM",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily
                            ),
                            color = if (metronomePlaying) StudioTeal else StudioTextMuted,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onClickMidiBadge)
                        .padding(vertical = 6.dp)
                        .testTag("topbar_midi_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = if (isMidiConnected) StudioTealContainer else StudioPanelElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isMidiConnected) StudioTeal else StudioBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isMidiConnected) StudioSuccess else StudioTextMuted)
                            )
                            Text(
                                text = if (isMidiConnected) "ON" else "MIDI",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isMidiConnected) StudioTeal else StudioTextMuted,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onClickDocs)
                        .padding(vertical = 6.dp)
                        .testTag("open_docs_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = StudioTealContainer,
                        border = BorderStroke(1.dp, StudioTeal.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Description,
                                contentDescription = "Open user guide and documentation",
                                tint = StudioTeal,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Docs",
                                style = MaterialTheme.typography.labelMedium,
                                color = StudioTeal,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = StudioBorderSubtle, thickness = 1.dp)
    }
}

private fun tabIcon(tab: AppTab, selected: Boolean): ImageVector = when (tab) {
    AppTab.GUIDE -> if (selected) Icons.Rounded.MenuBook else Icons.Outlined.MenuBook
    AppTab.SOUNDS -> if (selected) Icons.Rounded.LibraryMusic else Icons.Outlined.LibraryMusic
    AppTab.MIDI -> if (selected) Icons.Rounded.Usb else Icons.Outlined.Usb
    AppTab.STUDIO -> if (selected) Icons.Rounded.Album else Icons.Outlined.Album
}
