package com.example.data

import com.example.model.CapabilityItem
import com.example.model.EffectSetting
import com.example.model.HeroMetric
import com.example.model.MidiCapabilityRow
import com.example.model.MusicSpaceFeature
import com.example.model.OtgSetupStep
import com.example.model.PianoTone
import com.example.model.PortReference
import com.example.model.ProductionWorkflow
import com.example.model.SoftwareIntegration
import com.example.model.SpecGroup
import com.example.model.SpecRow
import com.example.model.ToneCategory

object CdpS100Catalog {

    val heroMetrics = listOf(
        HeroMetric("88", "Weighted keys"),
        HeroMetric("64", "Note polyphony"),
        HeroMetric("10", "Built-in tones")
    )

    val capabilities = listOf(
        CapabilityItem(
            id = "standalone",
            title = "Standalone piano",
            description = "Play using built-in instrument tones, speakers and effects.",
            iconKey = "piano"
        ),
        CapabilityItem(
            id = "midi_controller",
            title = "MIDI controller",
            description = "Send keyboard performance information to compatible software.",
            iconKey = "usb"
        ),
        CapabilityItem(
            id = "audio_integration",
            title = "Audio integration",
            description = "Connect headphones, external audio and amplification equipment.",
            iconKey = "headphones"
        ),
        CapabilityItem(
            id = "mobile_integration",
            title = "Mobile integration",
            description = "Connect compatible Android and iOS devices using USB.",
            iconKey = "smartphone"
        ),
        CapabilityItem(
            id = "computer_integration",
            title = "Computer integration",
            description = "Work with MIDI-compatible DAWs and virtual instruments.",
            iconKey = "laptop"
        ),
        CapabilityItem(
            id = "composition",
            title = "Music composition",
            description = "Record, edit and arrange performances using external software.",
            iconKey = "music_note"
        )
    )

    val specGroups = listOf(
        SpecGroup(
            id = "keyboard",
            title = "Keyboard & performance",
            rows = listOf(
                SpecRow("Keys", "88 full-size keys"),
                SpecRow("Action", "Scaled Hammer Action Keyboard II"),
                SpecRow("Touch Response", "3 sensitivity levels, Off"),
                SpecRow("Maximum Polyphony", "64 notes"),
                SpecRow("Transpose / Tuning", "-12 to +12 semitones / A4 = 415.5–465.9 Hz")
            )
        ),
        SpecGroup(
            id = "physical",
            title = "Physical & power",
            rows = listOf(
                SpecRow("Dimensions (W × D × H)", "1,322 × 232 × 99 mm"),
                SpecRow("Weight", "10.5 kg (excluding batteries)"),
                SpecRow("AC Power", "12V DC (AD-A12150LW adaptor)"),
                SpecRow("Battery Power", "6 × AA alkaline batteries (~13 hours)"),
                SpecRow("Included Accessories", "SP-3 sustain pedal, music rest, AC adaptor")
            )
        ),
        SpecGroup(
            id = "audio",
            title = "Audio system",
            rows = listOf(
                SpecRow("Speakers", "[12 cm × 6 cm (oval)] × 2"),
                SpecRow("Amplifiers", "8W + 8W"),
                SpecRow("Phones / Output", "3.5 mm stereo mini jack"),
                SpecRow("Audio In", "3.5 mm stereo mini jack (10 kΩ / 200 mV)"),
                SpecRow("Pedal Terminal", "6.35 mm standard damper jack")
            )
        ),
        SpecGroup(
            id = "sound",
            title = "Internal sounds & effects",
            rows = listOf(
                SpecRow("Preset Tones", "10 built-in tones"),
                SpecRow("Layer Mode", "Yes (combine any two tones)"),
                SpecRow("Digital Effects", "Reverb (4 types), Chorus (4 types)"),
                SpecRow("Metronome", "0 (Off), 1–9 beats • 20–255 BPM"),
                SpecRow("Demo Songs", "2 built-in demo songs")
            )
        )
    )

    val presetTones = listOf(
        PianoTone(
            id = 1,
            numberLabel = "01",
            name = "GRAND PIANO STANDARD",
            category = ToneCategory.ACOUSTIC_PIANO,
            description = "Balanced concert grand tone suited for classical and general practice.",
            hardwareShortcut = "Hold FUNCTION + Press A0",
            midiProgramIndex = 0
        ),
        PianoTone(
            id = 2,
            numberLabel = "02",
            name = "GRAND PIANO MELLOW",
            category = ToneCategory.ACOUSTIC_PIANO,
            description = "Warm, softer grand piano voicing ideal for ballads and solo pieces.",
            hardwareShortcut = "Hold FUNCTION + Press A#0",
            midiProgramIndex = 1
        ),
        PianoTone(
            id = 3,
            numberLabel = "03",
            name = "GRAND PIANO BRIGHT",
            category = ToneCategory.ACOUSTIC_PIANO,
            description = "Crisp, articulate piano tone designed to cut through ensemble mixes.",
            hardwareShortcut = "Hold FUNCTION + Press B0",
            midiProgramIndex = 2
        ),
        PianoTone(
            id = 4,
            numberLabel = "04",
            name = "ELEC.PIANO 1",
            category = ToneCategory.ELECTRIC_PIANO,
            description = "Classic tine-style electric piano for pop, soul, and fusion.",
            hardwareShortcut = "Hold FUNCTION + Press C1",
            midiProgramIndex = 4
        ),
        PianoTone(
            id = 5,
            numberLabel = "05",
            name = "ELEC.PIANO 2",
            category = ToneCategory.ELECTRIC_PIANO,
            description = "Bell-like FM electric piano timbre with clean harmonic shimmer.",
            hardwareShortcut = "Hold FUNCTION + Press C#1",
            midiProgramIndex = 5
        ),
        PianoTone(
            id = 6,
            numberLabel = "06",
            name = "ELEC.PIANO 3",
            category = ToneCategory.ELECTRIC_PIANO,
            description = "Warm reed-style electric piano with expressive dynamic bite.",
            hardwareShortcut = "Hold FUNCTION + Press D1",
            midiProgramIndex = 7
        ),
        PianoTone(
            id = 7,
            numberLabel = "07",
            name = "HARPSICHORD",
            category = ToneCategory.ENSEMBLE_KEYBOARD,
            description = "Plucked baroque keyboard tone great for layering with strings.",
            hardwareShortcut = "Hold FUNCTION + Press D#1",
            midiProgramIndex = 6
        ),
        PianoTone(
            id = 8,
            numberLabel = "08",
            name = "STRINGS",
            category = ToneCategory.ENSEMBLE_KEYBOARD,
            description = "Lush stereo string ensemble suited for layering under piano.",
            hardwareShortcut = "Hold FUNCTION + Press E1",
            midiProgramIndex = 48
        ),
        PianoTone(
            id = 9,
            numberLabel = "09",
            name = "PIPE ORGAN",
            category = ToneCategory.ORGAN,
            description = "Full cathedral pipe organ voice for classical and liturgical repertoire.",
            hardwareShortcut = "Hold FUNCTION + Press F1",
            midiProgramIndex = 19
        ),
        PianoTone(
            id = 10,
            numberLabel = "10",
            name = "JAZZ ORGAN",
            category = ToneCategory.ORGAN,
            description = "Percussive tonewheel-inspired organ for jazz, funk, and rock.",
            hardwareShortcut = "Hold FUNCTION + Press F#1",
            midiProgramIndex = 18
        )
    )

    val reverbSettings = listOf(
        EffectSetting(0, "Off", "Dry direct tone without room ambience"),
        EffectSetting(1, "Type 1 · Room", "Intimate small-room reflection"),
        EffectSetting(2, "Type 2 · Hall 1", "Balanced recital hall decay"),
        EffectSetting(3, "Type 3 · Hall 2", "Rich concert hall spatial tail"),
        EffectSetting(4, "Type 4 · Stadium", "Wide ambient wash for pads and strings")
    )

    val chorusSettings = listOf(
        EffectSetting(0, "Off", "No stereo modulation applied"),
        EffectSetting(1, "Type 1 · Subtle", "Gentle stereo widening"),
        EffectSetting(2, "Type 2 · Light", "Classic electric piano shimmer"),
        EffectSetting(3, "Type 3 · Deep", "Rich detuned ensemble sweep"),
        EffectSetting(4, "Type 4 · Lush", "Wide rotary-style stereo depth")
    )

    val portReferences = listOf(
        PortReference("USB Type-B", "Computer / mobile MIDI", isAudioDomain = false),
        PortReference("3.5 mm Audio In", "External audio playback", isAudioDomain = true),
        PortReference("3.5 mm Phones/Output", "Headphones / audio equipment", isAudioDomain = true),
        PortReference("6.35 mm Damper", "Sustain pedal", isAudioDomain = false),
        PortReference("12V DC", "Power supply", isAudioDomain = false)
    )

    val midiCapabilities = listOf(
        MidiCapabilityRow("Note On / Off", "Yes", true),
        MidiCapabilityRow("Velocity", "Yes", true),
        MidiCapabilityRow("USB MIDI", "Yes (Class-compliant)", true),
        MidiCapabilityRow("MIDI reception", "Yes", true),
        MidiCapabilityRow("Multi-timbre reception", "16 channels", true),
        MidiCapabilityRow("USB audio interface", "No (analog output only)", false),
        MidiCapabilityRow("Built-in MIDI sequencer", "No (use external DAW)", false)
    )

    val otgSetupSteps = listOf(
        OtgSetupStep(
            1,
            "01",
            "Install the application",
            "Install CASIO MUSIC SPACE from Google Play, where available."
        ),
        OtgSetupStep(
            2,
            "02",
            "Prepare the USB connection",
            "Connect a suitable USB OTG adapter to the phone and a data-capable cable to the piano."
        ),
        OtgSetupStep(
            3,
            "03",
            "Connect the devices",
            "Connect the USB Type-B end to the CDP-S100 and the other end to the phone adapter."
        ),
        OtgSetupStep(
            4,
            "04",
            "Check application permissions",
            "Allow USB access if Android prompts you, then check whether the piano is recognized above."
        ),
        OtgSetupStep(
            5,
            "05",
            "Test MIDI communication",
            "Play a key and verify that the live USB MIDI monitor detects Note On, Note Off, and velocity."
        )
    )

    val musicSpaceFeatures = listOf(
        MusicSpaceFeature(
            "Piano Roll",
            "Visualizes pitch and note timing in real time so you can practice melodies and chords even without reading traditional sheet music."
        ),
        MusicSpaceFeature(
            "Score Viewer",
            "Displays digital music scores with zoom, annotation, and pedal-activated page turning on supported setups."
        ),
        MusicSpaceFeature(
            "Remote Controller",
            "Adjust tones, layer settings, reverb, chorus, touch response, and metronome controls directly from your phone or tablet."
        ),
        MusicSpaceFeature(
            "Music Player",
            "Play back audio tracks with tempo, key shift, and loop tools for guided practice along with the piano speakers."
        )
    )

    val softwareIntegrations = listOf(
        SoftwareIntegration(
            id = "fl_studio",
            vendorTag = "FL STUDIO",
            title = "Music production",
            description = "MIDI recording, sequencing, virtual instruments and mixing.",
            url = "https://www.image-line.com/",
            footerLabel = "Official website"
        ),
        SoftwareIntegration(
            id = "ableton",
            vendorTag = "ABLETON LIVE",
            title = "Performance & production",
            description = "Live performance, MIDI sequencing and sound design.",
            url = "https://www.ableton.com/",
            footerLabel = "Official website"
        ),
        SoftwareIntegration(
            id = "reaper",
            vendorTag = "REAPER",
            title = "Recording & editing",
            description = "MIDI editing, recording and audio production.",
            url = "https://www.reaper.fm/",
            footerLabel = "Official website"
        ),
        SoftwareIntegration(
            id = "garageband",
            vendorTag = "GARAGEBAND",
            title = "Music creation",
            description = "Virtual instruments and music composition on supported Apple devices.",
            url = "https://www.apple.com/mac/garageband/",
            footerLabel = "Official website"
        ),
        SoftwareIntegration(
            id = "musescore",
            vendorTag = "MUSESCORE",
            title = "Music notation",
            description = "Sheet music creation and MIDI-based composition workflows.",
            url = "https://musescore.org/",
            footerLabel = "Official website"
        ),
        SoftwareIntegration(
            id = "vst",
            vendorTag = "VIRTUAL INSTRUMENTS",
            title = "Sound expansion",
            description = "Software pianos, orchestral instruments, synthesizers and more.",
            url = null,
            footerLabel = "VST / AU / Standalone"
        )
    )

    val productionWorkflows = listOf(
        ProductionWorkflow(
            id = "workflow_midi",
            badge = "WORKFLOW A • VIRTUAL INSTRUMENTS",
            title = "USB MIDI to DAW",
            summary = "Connect the CDP-S100 directly to your computer with a USB Type-B cable. Record MIDI notes inside your DAW to trigger software pianos, synths, and orchestral libraries with full velocity sensitivity and post-recording editing.",
            signalChain = listOf("CDP-S100 Keys", "USB Type-B (MIDI)", "DAW MIDI Track", "VST / Software Piano"),
            isAudioPath = false
        ),
        ProductionWorkflow(
            id = "workflow_audio",
            badge = "WORKFLOW B • INTERNAL TONES",
            title = "Analog Audio to Interface",
            summary = "To record the piano's built-in sounds, route the 3.5 mm Phones/Output jack into a dedicated audio interface or line input using a stereo breakout cable, then capture the live audio track in your DAW.",
            signalChain = listOf("CDP-S100 Engine", "3.5 mm Phones/Out", "Stereo Breakout Cable", "Audio Interface → DAW"),
            isAudioPath = true
        )
    )
}
