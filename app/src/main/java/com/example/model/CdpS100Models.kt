package com.example.model

enum class AppTab(val label: String) {
    GUIDE("Guide"),
    SOUNDS("Sounds"),
    MIDI("MIDI"),
    STUDIO("Studio")
}

enum class ToneCategory(val label: String) {
    ALL("All"),
    ACOUSTIC_PIANO("Acoustic Piano"),
    ELECTRIC_PIANO("Electric Piano"),
    ORGAN("Organ"),
    ENSEMBLE_KEYBOARD("Keys & Strings")
}

data class HeroMetric(
    val value: String,
    val label: String
)

data class CapabilityItem(
    val id: String,
    val title: String,
    val description: String,
    val iconKey: String
)

data class SpecRow(
    val label: String,
    val value: String
)

data class SpecGroup(
    val id: String,
    val title: String,
    val rows: List<SpecRow>
)

data class PianoTone(
    val id: Int,
    val numberLabel: String,
    val name: String,
    val category: ToneCategory,
    val description: String,
    val hardwareShortcut: String,
    val midiProgramIndex: Int
)

data class EffectSetting(
    val level: Int,
    val label: String,
    val description: String
)

data class PortReference(
    val badge: String,
    val purpose: String,
    val isAudioDomain: Boolean
)

data class MidiCapabilityRow(
    val capability: String,
    val support: String,
    val isSupported: Boolean
)

data class OtgSetupStep(
    val stepNumber: Int,
    val numberLabel: String,
    val title: String,
    val description: String
)

data class MusicSpaceFeature(
    val title: String,
    val description: String
)

data class SoftwareIntegration(
    val id: String,
    val vendorTag: String,
    val title: String,
    val description: String,
    val url: String?,
    val footerLabel: String
)

data class ProductionWorkflow(
    val id: String,
    val badge: String,
    val title: String,
    val summary: String,
    val signalChain: List<String>,
    val isAudioPath: Boolean
)

data class MidiDeviceSummary(
    val id: Int,
    val name: String,
    val manufacturer: String,
    val outputPortCount: Int,
    val inputPortCount: Int
)

enum class MidiEventType {
    NOTE_ON,
    NOTE_OFF,
    CONTROL_CHANGE,
    PROGRAM_CHANGE,
    PITCH_BEND,
    SYSTEM
}

data class MidiEventLog(
    val id: Long,
    val timeLabel: String,
    val channel: Int,
    val type: MidiEventType,
    val summary: String,
    val rawHex: String
)
