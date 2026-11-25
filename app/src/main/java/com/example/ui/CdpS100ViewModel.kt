package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MetronomeEngine
import com.example.data.CdpS100Catalog
import com.example.data.PersistedStudioSettings
import com.example.data.StudioPreferencesRepository
import com.example.midi.MidiMonitorService
import com.example.model.AppTab
import com.example.model.MidiDeviceSummary
import com.example.model.MidiEventLog
import com.example.model.PianoTone
import com.example.model.SpecGroup
import com.example.model.ToneCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GuideUiState(
    val searchQuery: String = "",
    val filteredSpecGroups: List<SpecGroup> = CdpS100Catalog.specGroups
)

data class SoundsUiState(
    val selectedCategory: ToneCategory = ToneCategory.ALL,
    val filteredTones: List<PianoTone> = CdpS100Catalog.presetTones,
    val primaryToneId: Int = 1,
    val secondaryToneId: Int = 8,
    val layerEnabled: Boolean = false,
    val reverbLevel: Int = 2,
    val chorusLevel: Int = 0,
    val metronomePlaying: Boolean = false,
    val metronomeBpm: Int = 120,
    val metronomeBeatsPerBar: Int = 4,
    val currentBeatIndex: Int = 0,
    val beatTickCounter: Long = 0L,
    val isMidiConnected: Boolean = false,
    val midiDispatchMessage: String? = null
)

data class MidiUiState(
    val isMidiSupported: Boolean = false,
    val devices: List<MidiDeviceSummary> = emptyList(),
    val selectedDeviceId: Int? = null,
    val isConnected: Boolean = false,
    val statusMessage: String = "",
    val activeNotes: Set<Int> = emptySet(),
    val recentEvents: List<MidiEventLog> = emptyList(),
    val completedOtgStepIds: Set<Int> = emptySet()
)

data class StudioUiState(
    val selectedWorkflowId: String = CdpS100Catalog.productionWorkflows.first().id
)

class CdpS100ViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = StudioPreferencesRepository(application)
    private val metronomeEngine = MetronomeEngine()
    private val midiService = MidiMonitorService(application)

    private val _currentTab = MutableStateFlow(AppTab.GUIDE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _specSearchQuery = MutableStateFlow("")
    private val _selectedToneCategory = MutableStateFlow(ToneCategory.ALL)
    private val _midiDispatchMessage = MutableStateFlow<String?>(null)
    private val _selectedWorkflowId = MutableStateFlow(CdpS100Catalog.productionWorkflows.first().id)

    private val persistedSettings: StateFlow<PersistedStudioSettings> = prefsRepo.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PersistedStudioSettings()
        )

    private val tapTimestamps = ArrayDeque<Long>(6)

    val guideState: StateFlow<GuideUiState> = _specSearchQuery
        .combine(MutableStateFlow(CdpS100Catalog.specGroups)) { query, groups ->
            val trimmed = query.trim()
            if (trimmed.isEmpty()) {
                GuideUiState(searchQuery = query, filteredSpecGroups = groups)
            } else {
                val filtered = groups.mapNotNull { group ->
                    val matchingRows = group.rows.filter { row ->
                        row.label.contains(trimmed, ignoreCase = true) ||
                            row.value.contains(trimmed, ignoreCase = true) ||
                            group.title.contains(trimmed, ignoreCase = true)
                    }
                    if (matchingRows.isNotEmpty()) group.copy(rows = matchingRows) else null
                }
                GuideUiState(searchQuery = query, filteredSpecGroups = filtered)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GuideUiState()
        )

    private val metronomeFlow = combine(
        metronomeEngine.isPlaying,
        metronomeEngine.currentBeatIndex,
        metronomeEngine.beatTickCounter
    ) { playing, beat, tick -> Triple(playing, beat, tick) }

    val soundsState: StateFlow<SoundsUiState> = combine(
        persistedSettings,
        _selectedToneCategory,
        metronomeFlow,
        midiService.isConnected,
        _midiDispatchMessage
    ) { settings, category, (playing, currentBeat, tickCounter), midiConnected, dispatchMsg ->
        val filteredTones = if (category == ToneCategory.ALL) {
            CdpS100Catalog.presetTones
        } else {
            CdpS100Catalog.presetTones.filter { it.category == category }
        }
        metronomeEngine.updateConfig(settings.metronomeBpm, settings.metronomeBeatsPerBar)
        SoundsUiState(
            selectedCategory = category,
            filteredTones = filteredTones,
            primaryToneId = settings.primaryToneId,
            secondaryToneId = settings.secondaryToneId,
            layerEnabled = settings.layerEnabled,
            reverbLevel = settings.reverbLevel,
            chorusLevel = settings.chorusLevel,
            metronomePlaying = playing,
            metronomeBpm = settings.metronomeBpm,
            metronomeBeatsPerBar = settings.metronomeBeatsPerBar,
            currentBeatIndex = currentBeat,
            beatTickCounter = tickCounter,
            isMidiConnected = midiConnected,
            midiDispatchMessage = dispatchMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SoundsUiState()
    )

    val midiState: StateFlow<MidiUiState> = combine(
        midiService.devices,
        midiService.selectedDeviceId,
        midiService.isConnected,
        midiService.statusMessage,
        midiService.activeNotes
    ) { devices, selectedId, connected, status, notes ->
        MidiUiState(
            isMidiSupported = midiService.isMidiSupported,
            devices = devices,
            selectedDeviceId = selectedId,
            isConnected = connected,
            statusMessage = status,
            activeNotes = notes
        )
    }.combine(midiService.events) { state, events ->
        state.copy(recentEvents = events)
    }.combine(persistedSettings) { state, settings ->
        state.copy(completedOtgStepIds = settings.completedOtgStepIds)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MidiUiState(
            isMidiSupported = midiService.isMidiSupported,
            statusMessage = midiService.statusMessage.value
        )
    )

    val studioState: StateFlow<StudioUiState> = _selectedWorkflowId
        .combine(MutableStateFlow(Unit)) { workflowId, _ ->
            StudioUiState(selectedWorkflowId = workflowId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StudioUiState()
        )

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun updateSpecSearch(query: String) {
        _specSearchQuery.value = query
    }

    fun selectToneCategory(category: ToneCategory) {
        _selectedToneCategory.value = category
    }

    fun selectPrimaryTone(toneId: Int) {
        _midiDispatchMessage.value = null
        viewModelScope.launch { prefsRepo.savePrimaryTone(toneId) }
    }

    fun selectSecondaryTone(toneId: Int, enableLayer: Boolean? = null) {
        _midiDispatchMessage.value = null
        viewModelScope.launch { prefsRepo.saveSecondaryTone(toneId, enableLayer) }
    }

    fun setLayerEnabled(enabled: Boolean) {
        _midiDispatchMessage.value = null
        viewModelScope.launch { prefsRepo.saveLayerEnabled(enabled) }
    }

    fun setReverbLevel(level: Int) {
        _midiDispatchMessage.value = null
        viewModelScope.launch { prefsRepo.saveReverbLevel(level) }
    }

    fun setChorusLevel(level: Int) {
        _midiDispatchMessage.value = null
        viewModelScope.launch { prefsRepo.saveChorusLevel(level) }
    }

    fun sendToneConfigToConnectedPiano() {
        val current = soundsState.value
        val tone = CdpS100Catalog.presetTones.find { it.id == current.primaryToneId } ?: return
        val ok = midiService.sendProgramChange(
            programIndex = tone.midiProgramIndex,
            reverbLevel = current.reverbLevel,
            chorusLevel = current.chorusLevel
        )
        _midiDispatchMessage.value = if (ok) {
            "Sent Program #${tone.midiProgramIndex + 1} (${tone.name}) & FX over USB MIDI"
        } else {
            "Connect a USB MIDI keyboard with an input port to send settings"
        }
    }

    fun toggleMetronome() {
        val current = soundsState.value
        if (current.metronomePlaying) {
            metronomeEngine.stop()
        } else {
            metronomeEngine.start(current.metronomeBpm, current.metronomeBeatsPerBar)
        }
    }

    fun setMetronomeBpm(bpm: Int) {
        val clamped = bpm.coerceIn(20, 255)
        metronomeEngine.updateConfig(clamped, persistedSettings.value.metronomeBeatsPerBar)
        viewModelScope.launch {
            prefsRepo.saveMetronomeBpm(clamped)
        }
    }

    fun setMetronomeBeatsPerBar(beats: Int) {
        val clamped = beats.coerceIn(0, 9)
        metronomeEngine.updateConfig(persistedSettings.value.metronomeBpm, clamped)
        viewModelScope.launch {
            prefsRepo.saveMetronomeBeats(clamped)
        }
    }

    fun onTapTempo() {
        val now = System.currentTimeMillis()
        val last = tapTimestamps.lastOrNull()
        if (last != null && now - last > 2_500L) {
            tapTimestamps.clear()
        }
        tapTimestamps.addLast(now)
        if (tapTimestamps.size > 5) {
            tapTimestamps.removeFirst()
        }
        if (tapTimestamps.size >= 2) {
            val intervals = tapTimestamps.zipWithNext { a, b -> b - a }.filter { it > 180L }
            if (intervals.isNotEmpty()) {
                val avgMs = intervals.average()
                val calculatedBpm = (60_000.0 / avgMs).toInt().coerceIn(20, 255)
                setMetronomeBpm(calculatedBpm)
            }
        }
    }

    fun refreshMidiDevices() {
        midiService.refreshDevices(autoConnect = true)
    }

    fun selectMidiDevice(deviceId: Int) {
        midiService.connectToDevice(deviceId)
    }

    fun disconnectMidi() {
        midiService.disconnect()
    }

    fun sendTestNoteC4() {
        midiService.sendTestNoteC4()
    }

    fun sendAllNotesOff() {
        midiService.sendAllNotesOff()
    }

    fun clearMidiLog() {
        midiService.clearLog()
    }

    fun toggleOtgStep(stepNumber: Int) {
        viewModelScope.launch {
            prefsRepo.toggleOtgStep(stepNumber)
        }
    }

    fun resetOtgChecklist() {
        viewModelScope.launch {
            prefsRepo.resetOtgChecklist()
        }
    }

    fun selectProductionWorkflow(workflowId: String) {
        _selectedWorkflowId.value = workflowId
    }

    override fun onCleared() {
        super.onCleared()
        metronomeEngine.release()
        midiService.release()
    }
}
