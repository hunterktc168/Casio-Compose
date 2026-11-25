# CDP-S100 Guide

A native Android companion application for the **Casio CDP-S100** digital piano. Built in Kotlin and Jetpack Compose with a dark studio aesthetic, the app pairs an offline-ready hardware reference guide with a precision `20–255 BPM` audio metronome, an interactive Tone Layer & Effects explorer, a persistent Android USB OTG setup checklist, and a live USB-MIDI monitor.

---

## Functions & Capabilities

### 1. Adaptive Studio Interface
- **Four-Tab Navigation**: Organized into **Guide**, **Sounds**, **MIDI**, and **Studio** sections via a Material 3 bottom `NavigationBar` on phones and an ergonomic side `NavigationRail` on screens `>= 700.dp` wide.
- **Live Telemetry Header**: Top bar displays interactive status pills for the metronome (`BPM` / active state) and USB-MIDI connection status (`USB MIDI` / `MIDI ON`), allowing one-tap jumps to the corresponding tools.
- **Dark Studio Design System**: Built on deep ink (`#101319`), elevated slate panels (`#181E28`), teal (`#54D6C4`) for MIDI and primary controls, warm amber (`#FBBF24`) for analog audio workflows, and muted slate (`#9AA8BA`) secondary text.

### 2. Guide Tab (Overview & Specifications)
- **Product Profile & Hero Metrics**: Highlights the CDP-S100 core hardware profile (`88` weighted keys, `64`-note polyphony, `10` built-in tones) with quick actions to jump into Sounds or USB MIDI setup.
- **Capabilities Breakdown**: Six capability cards covering Standalone piano, MIDI controller, Audio integration, Mobile integration, Computer integration, and Music composition.
- **Live Searchable Technical Specifications**: Instant filtering across all four technical tables (**Keyboard & performance**, **Physical & power**, **Audio system**, and **Internal sounds & effects**).

### 3. Sounds Tab (Metronome, Tone Explorer & Preset Engine)
- **Studio Metronome (`20–255 BPM`)**:
  - Synthesizes a sample-accurate percussive click stream via `AudioTrack` (`44.1 kHz`, 16-bit PCM).
  - Supports fine `+/- 1 BPM` stepping, slider scrubbing, **Tap Tempo** averaging, and `0 (Off)` or `1–9` beats per measure with an accented downbeat and animated visual beat dots.
- **Tone Layer & Effects Explorer**:
  - Select a **Primary Tone** (`01`–`10`), toggle **Layer Mode** to combine a **Secondary Tone**, and adjust **Reverb** (`Off`, `Room`, `Hall 1`, `Hall 2`, `Stadium`) and **Chorus** (`Off`, `Subtle`, `Light`, `Deep`, `Lush`).
  - Displays the corresponding Casio hardware `FUNCTION + Key` shortcut and can transmit MIDI Program Change + Reverb (`CC 91`) / Chorus (`CC 93`) directly to a connected USB-MIDI keyboard.
- **Filterable Sound Engine Catalog**: Browse all 10 built-in tones (`01 GRAND PIANO STANDARD` through `10 JAZZ ORGAN`) by category (**All**, **Acoustic Piano**, **Electric Piano**, **Organ**, **Keys & Strings**).

### 4. MIDI Tab (Live USB Monitor, OTG Checklist & Connectivity)
- **Live USB MIDI Monitor (`MidiManager`)**:
  - Automatically discovers USB/byte-stream MIDI devices on hot-plug or manual scan.
  - Visualizes pressed keys in real time on an **88-key Canvas keyboard strip** (`A0`–`C8`).
  - Decodes incoming `Note On`, `Note Off` (including velocity `0` and Running Status chords), `Control Change` (e.g., `CC 64` Damper Pedal), `Program Change`, and `Pitch Bend` with channel and raw hex readouts.
  - Includes outbound diagnostic controls (**Test C4** and **All Off** / `CC 123`) when a connected device exposes a MIDI input port.
- **Android USB OTG Setup**:
  - Visual `Android ↔ OTG + USB ↔ CDP-S100` connection chain diagram.
  - Persistent 5-step interactive setup checklist with progress bar and reset option.
  - Breakdown of **CASIO MUSIC SPACE** companion features (*Piano Roll*, *Score Viewer*, *Remote Controller*, *Music Player*).
- **Hardware Interfaces & MIDI Matrix**: Clear distinction between **USB Type-B MIDI** (instructions) and **Analog Audio** (sound signals), physical jack reference (`USB Type-B`, `3.5 mm Audio In`, `3.5 mm Phones/Output`, `6.35 mm Damper`, `12V DC`), and a 7-row MIDI Capabilities table.

### 5. Studio Tab (Production Workflows & Software Ecosystem)
- **Interactive Workflow Switcher**: Compares **Workflow A: USB MIDI to DAW** (triggering virtual instruments) against **Workflow B: Analog Audio to Interface** (capturing internal tones via `3.5 mm Phones/Output`), complete with step-by-step signal-flow diagrams.
- **Software Ecosystem Cards**: Reference cards and direct external links for **FL Studio**, **Ableton Live**, **Reaper**, **GarageBand**, **MuseScore**, and **Virtual Instruments** (`VST / AU / Standalone`).

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material Design 3 (`enableEdgeToEdge`) |
| **Architecture** | MVVM (`AndroidViewModel`, unidirectional `StateFlow` + `collectAsStateWithLifecycle`) |
| **Concurrency** | Kotlin Coroutines (`Dispatchers.Default`, `SupervisorJob`, `Flow`) |
| **USB MIDI** | Android MIDI SDK (`android.media.midi.MidiManager`, `MidiDeviceInfo`, `MidiReceiver`) |
| **Audio Synthesis** | `android.media.AudioTrack` (`MODE_STREAM`, `44.1 kHz` mono PCM) |
| **Local Persistence** | Jetpack `DataStore` Preferences (`StudioPreferencesRepository`) |
| **Typography** | Local bundled `.ttf` fonts: *Space Grotesk*, *Plus Jakarta Sans*, *JetBrains Mono* |
| **Build & Testing** | Gradle Kotlin DSL, Version Catalog (`libs.versions.toml`), JUnit 4, Android Lint |
| **Manifest Features** | Optional `android.software.midi` & `android.hardware.usb.host` (`required="false"`) |

---

## User Guide

### Navigating the App
- Use the bottom navigation bar (**Guide**, **Sounds**, **MIDI**, **Studio**) on phones—or the left navigation rail on tablets—to switch workspaces.
- Pressing the system **Back** button from **Sounds**, **MIDI**, or **Studio** returns to the **Guide** tab.
- Tap the **BPM pill** or **USB MIDI pill** in the top bar at any time to jump directly to the metronome or live MIDI monitor.

### Searching Specifications (**Guide** Tab)
1. Open the **Guide** tab and scroll to **02 / Technical Data**.
2. Enter keywords such as `polyphony`, `10.5 kg`, `damper`, or `batteries` into the search field to filter rows across all four specification tables.
3. Tap the clear (`×`) icon to restore the full specification list.

### Practicing with the Metronome & Tone Explorer (**Sounds** Tab)
1. **Metronome**: Drag the BPM slider, tap `-` / `+` for `1 BPM` steps, or tap **Tap Tempo** rhythmically to set any tempo between `20` and `255 BPM`. Choose `Off` or `1–9` beats per measure, then tap **Start**.
2. **Tone Explorer**: Tap any tone card (`01`–`10`) to set it as your **Primary Tone**, or tap **+ Layer** on a card to enable **Layer Mode** and pair it as the **Secondary Tone**.
3. Select **Reverb** (`Off` to `Type 4 · Stadium`) and **Chorus** (`Off` to `Type 4 · Lush`) chips to inspect configurations, or tap **Send Tone & FX over USB MIDI** when your piano is connected.

### Connecting Your Casio CDP-S100 to Android (**MIDI** Tab)
1. Attach a data-capable **USB Type-B** cable from the rear of the CDP-S100 into a **USB OTG adapter** connected to your Android phone or tablet.
2. Follow and check off the 5 steps in the **Android USB OTG checklist** (your progress saves automatically).
3. Tap the **Refresh** icon in the **Live USB MIDI Monitor** card if the keyboard is not selected automatically, then tap the detected device chip.
4. Play keys or press the sustain pedal on the CDP-S100 to watch the **88-key visualizer** light up and inspect incoming Note On/Off, Velocity, and Damper (`CC 64`) events.
5. Use **Test C4** to trigger a brief middle-C note on the piano, or **All Off** (`CC 123`) to silence any stuck notes.

### Setting Up DAW Recording (**Studio** Tab)
1. Switch between **USB MIDI to DAW** (for recording MIDI notes to drive VST/AU software instruments) and **Analog Audio** (for recording the CDP-S100's built-in piano tones through a `3.5 mm` stereo breakout cable into an audio interface).
2. Tap **Official website** on any compatible DAW or notation card (FL Studio, Ableton Live, Reaper, GarageBand, MuseScore) to open its official site in your browser.
