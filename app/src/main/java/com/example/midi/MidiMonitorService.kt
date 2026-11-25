package com.example.midi

import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.media.midi.MidiOutputPort
import android.media.midi.MidiReceiver
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.example.model.MidiDeviceSummary
import com.example.model.MidiEventLog
import com.example.model.MidiEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong

class MidiMonitorService(context: Context) {

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val midiManager: MidiManager? =
        appContext.getSystemService(Context.MIDI_SERVICE) as? MidiManager

    val isMidiSupported: Boolean =
        appContext.packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI) && midiManager != null

    private val _devices = MutableStateFlow<List<MidiDeviceSummary>>(emptyList())
    val devices: StateFlow<List<MidiDeviceSummary>> = _devices.asStateFlow()

    private val _selectedDeviceId = MutableStateFlow<Int?>(null)
    val selectedDeviceId: StateFlow<Int?> = _selectedDeviceId.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _activeNotes = MutableStateFlow<Set<Int>>(emptySet())
    val activeNotes: StateFlow<Set<Int>> = _activeNotes.asStateFlow()

    private val _events = MutableStateFlow<List<MidiEventLog>>(emptyList())
    val events: StateFlow<List<MidiEventLog>> = _events.asStateFlow()

    private val _statusMessage = MutableStateFlow(
        if (isMidiSupported) "Waiting for USB MIDI keyboard" else "Android MIDI service unavailable on this device"
    )
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    @Volatile
    private var openDevice: MidiDevice? = null

    @Volatile
    private var openOutputPort: MidiOutputPort? = null

    @Volatile
    private var openInputPort: MidiInputPort? = null

    private val eventIdCounter = AtomicLong(1L)
    private val timeFormatterLocal = ThreadLocal.withInitial {
        SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    }
    private val deviceInfoMap = mutableMapOf<Int, MidiDeviceInfo>()

    private val receiver = object : MidiReceiver() {
        override fun onSend(msg: ByteArray, offset: Int, count: Int, timestamp: Long) {
            parseMidiPacket(msg, offset, count)
        }
    }

    private val deviceCallback = object : MidiManager.DeviceCallback() {
        override fun onDeviceAdded(device: MidiDeviceInfo) {
            refreshDevices(autoConnect = true)
        }

        override fun onDeviceRemoved(device: MidiDeviceInfo) {
            if (_selectedDeviceId.value == device.id) {
                closeCurrentDevice()
            }
            refreshDevices(autoConnect = false)
        }
    }

    init {
        if (isMidiSupported) {
            registerCallback()
            refreshDevices(autoConnect = true)
        }
    }

    private fun registerCallback() {
        val manager = midiManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            manager.registerDeviceCallback(
                MidiManager.TRANSPORT_MIDI_BYTE_STREAM,
                { runnable -> mainHandler.post(runnable) },
                deviceCallback
            )
        } else {
            @Suppress("DEPRECATION")
            manager.registerDeviceCallback(deviceCallback, mainHandler)
        }
    }

    fun refreshDevices(autoConnect: Boolean = true) {
        val manager = midiManager ?: return
        val rawInfos: Collection<MidiDeviceInfo> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                manager.getDevicesForTransport(MidiManager.TRANSPORT_MIDI_BYTE_STREAM)
            } else {
                @Suppress("DEPRECATION")
                manager.devices.toList()
            }

        deviceInfoMap.clear()
        val summaries = rawInfos.map { info ->
            deviceInfoMap[info.id] = info
            val props = info.properties
            val name = props.getString(MidiDeviceInfo.PROPERTY_NAME)
                ?: props.getString(MidiDeviceInfo.PROPERTY_PRODUCT)
                ?: "USB MIDI Device #${info.id}"
            val manufacturer = props.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER) ?: "Generic MIDI"
            MidiDeviceSummary(
                id = info.id,
                name = name,
                manufacturer = manufacturer,
                outputPortCount = info.outputPortCount,
                inputPortCount = info.inputPortCount
            )
        }

        _devices.value = summaries

        if (summaries.isEmpty()) {
            _statusMessage.value = "No USB MIDI keyboard detected. Connect via USB OTG."
        } else if (autoConnect && _selectedDeviceId.value == null) {
            val preferred = summaries.firstOrNull {
                it.name.contains("CASIO", ignoreCase = true) ||
                    it.manufacturer.contains("CASIO", ignoreCase = true)
            } ?: summaries.first()
            connectToDevice(preferred.id)
        }
    }

    fun connectToDevice(deviceId: Int) {
        val manager = midiManager ?: return
        val info = deviceInfoMap[deviceId] ?: return
        closeCurrentDevice()
        _selectedDeviceId.value = deviceId
        _statusMessage.value = "Opening MIDI port…"

        manager.openDevice(
            info,
            { opened ->
                if (opened == null) {
                    if (_selectedDeviceId.value == info.id) {
                        _isConnected.value = false
                        _selectedDeviceId.value = null
                        _statusMessage.value = "Could not open selected MIDI device"
                    }
                    return@openDevice
                }
                // Guard against stale async callback if user switched or disconnected
                if (_selectedDeviceId.value != info.id) {
                    runCatching { opened.close() }
                    return@openDevice
                }

                openDevice = opened
                if (info.outputPortCount > 0) {
                    openOutputPort = opened.openOutputPort(0)?.also { port ->
                        port.connect(receiver)
                    }
                }
                if (info.inputPortCount > 0) {
                    openInputPort = opened.openInputPort(0)
                }
                _isConnected.value = true
                val name = info.properties.getString(MidiDeviceInfo.PROPERTY_NAME) ?: "MIDI Device"
                _statusMessage.value = "Connected to $name"
            },
            mainHandler
        )
    }

    fun disconnect() {
        closeCurrentDevice()
        _statusMessage.value = if (_devices.value.isEmpty()) {
            "No USB MIDI keyboard detected"
        } else {
            "Disconnected — select a device to monitor"
        }
    }

    private fun closeCurrentDevice() {
        runCatching { openOutputPort?.disconnect(receiver) }
        runCatching { openOutputPort?.close() }
        runCatching { openInputPort?.close() }
        runCatching { openDevice?.close() }
        openOutputPort = null
        openInputPort = null
        openDevice = null
        _isConnected.value = false
        _selectedDeviceId.value = null
        _activeNotes.value = emptySet()
    }

    fun sendProgramChange(programIndex: Int, reverbLevel: Int? = null, chorusLevel: Int? = null): Boolean {
        val inputPort = openInputPort ?: return false
        return runCatching {
            val programMsg = byteArrayOf(0xC0.toByte(), programIndex.coerceIn(0, 127).toByte())
            inputPort.send(programMsg, 0, programMsg.size)
            appendEvent(
                channel = 1,
                type = MidiEventType.PROGRAM_CHANGE,
                summary = "TX Program Change → #${programIndex + 1}",
                rawHex = formatHex(programMsg)
            )
            if (reverbLevel != null) {
                val revVal = (reverbLevel.coerceIn(0, 4) * 31).coerceIn(0, 127)
                val revMsg = byteArrayOf(0xB0.toByte(), 91.toByte(), revVal.toByte())
                inputPort.send(revMsg, 0, revMsg.size)
            }
            if (chorusLevel != null) {
                val choVal = (chorusLevel.coerceIn(0, 4) * 31).coerceIn(0, 127)
                val choMsg = byteArrayOf(0xB0.toByte(), 93.toByte(), choVal.toByte())
                inputPort.send(choMsg, 0, choMsg.size)
            }
            true
        }.getOrDefault(false)
    }

    fun sendTestNoteC4(): Boolean {
        val inputPort = openInputPort ?: return false
        return runCatching {
            val noteOn = byteArrayOf(0x90.toByte(), 60.toByte(), 96.toByte())
            inputPort.send(noteOn, 0, noteOn.size)
            appendEvent(
                channel = 1,
                type = MidiEventType.NOTE_ON,
                summary = "TX Test Note C4 (Vel 96)",
                rawHex = formatHex(noteOn)
            )
            mainHandler.postDelayed({
                if (openInputPort === inputPort) {
                    runCatching {
                        val noteOff = byteArrayOf(0x80.toByte(), 60.toByte(), 0.toByte())
                        inputPort.send(noteOff, 0, noteOff.size)
                        appendEvent(
                            channel = 1,
                            type = MidiEventType.NOTE_OFF,
                            summary = "TX Note Off C4 (#60)",
                            rawHex = formatHex(noteOff)
                        )
                    }
                }
            }, 350L)
            true
        }.getOrDefault(false)
    }

    fun sendAllNotesOff(): Boolean {
        val inputPort = openInputPort ?: return false
        return runCatching {
            val msg = byteArrayOf(0xB0.toByte(), 123.toByte(), 0.toByte())
            inputPort.send(msg, 0, msg.size)
            _activeNotes.value = emptySet()
            appendEvent(
                channel = 1,
                type = MidiEventType.CONTROL_CHANGE,
                summary = "TX CC 123 All Notes Off",
                rawHex = formatHex(msg)
            )
            true
        }.getOrDefault(false)
    }

    fun clearLog() {
        _events.value = emptyList()
    }

    private fun parseMidiPacket(msg: ByteArray, offset: Int, count: Int) {
        val parsed = decodeMidiPacket(msg, offset, count)
        if (parsed.isEmpty()) return

        _activeNotes.update { current ->
            var next = current
            for (item in parsed) {
                when {
                    item.noteOn != null -> next = next + item.noteOn
                    item.noteOff != null -> next = next - item.noteOff
                }
            }
            next
        }

        val timestamp = timeFormatterLocal.get()?.format(Date()) ?: ""
        val newEntries = parsed.reversed().map { item ->
            MidiEventLog(
                id = eventIdCounter.getAndIncrement(),
                timeLabel = timestamp,
                channel = item.channel,
                type = item.type,
                summary = item.summary,
                rawHex = item.rawHex
            )
        }
        _events.update { existing -> (newEntries + existing).take(50) }
    }

    private fun appendEvent(channel: Int, type: MidiEventType, summary: String, rawHex: String) {
        val timestamp = timeFormatterLocal.get()?.format(Date()) ?: ""
        val entry = MidiEventLog(
            id = eventIdCounter.getAndIncrement(),
            timeLabel = timestamp,
            channel = channel,
            type = type,
            summary = summary,
            rawHex = rawHex
        )
        _events.update { existing -> (listOf(entry) + existing).take(50) }
    }

    fun release() {
        mainHandler.removeCallbacksAndMessages(null)
        closeCurrentDevice()
        if (isMidiSupported) {
            runCatching { midiManager?.unregisterDeviceCallback(deviceCallback) }
        }
    }

    internal data class DecodedMidiMessage(
        val channel: Int,
        val type: MidiEventType,
        val summary: String,
        val rawHex: String,
        val noteOn: Int? = null,
        val noteOff: Int? = null
    )

    companion object {
        private val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        fun midiNoteName(noteNumber: Int): String {
            val safe = noteNumber.coerceIn(0, 127)
            val name = NOTE_NAMES[safe % 12]
            val octave = (safe / 12) - 1
            return "$name$octave"
        }

        private fun formatHex(bytes: ByteArray): String =
            bytes.joinToString(" ") { "%02X".format(it) }

        internal fun decodeMidiPacket(msg: ByteArray, offset: Int, count: Int): List<DecodedMidiMessage> {
            if (count <= 0 || offset < 0 || offset + count > msg.size) return emptyList()
            val results = mutableListOf<DecodedMidiMessage>()
            var i = offset
            val end = offset + count
            var runningStatus = 0

            while (i < end) {
                val byteVal = msg[i].toInt() and 0xFF
                if (byteVal >= 0xF8) {
                    // System Real-Time message (single byte, does not clear running status)
                    i++
                    continue
                }
                if (byteVal >= 0xF0) {
                    // System Common / SysEx clears running status
                    runningStatus = 0
                    if (byteVal == 0xF0) {
                        // Skip SysEx until 0xF7
                        i++
                        while (i < end && (msg[i].toInt() and 0xFF) != 0xF7) {
                            i++
                        }
                        if (i < end) i++
                    } else {
                        i++
                    }
                    continue
                }

                val status: Int
                if (byteVal >= 0x80) {
                    status = byteVal
                    runningStatus = status
                    i++
                } else {
                    if (runningStatus < 0x80) {
                        i++
                        continue
                    }
                    status = runningStatus
                }

                val messageType = status and 0xF0
                val channel = (status and 0x0F) + 1

                when (messageType) {
                    0x90 -> {
                        if (i + 1 >= end) break
                        val note = msg[i].toInt() and 0x7F
                        val velocity = msg[i + 1].toInt() and 0x7F
                        val hex = formatHex(byteArrayOf(status.toByte(), note.toByte(), velocity.toByte()))
                        if (velocity > 0) {
                            results += DecodedMidiMessage(
                                channel = channel,
                                type = MidiEventType.NOTE_ON,
                                summary = "Note On  ${midiNoteName(note)} (#$note)  Vel $velocity",
                                rawHex = hex,
                                noteOn = note
                            )
                        } else {
                            results += DecodedMidiMessage(
                                channel = channel,
                                type = MidiEventType.NOTE_OFF,
                                summary = "Note Off ${midiNoteName(note)} (#$note)",
                                rawHex = hex,
                                noteOff = note
                            )
                        }
                        i += 2
                    }

                    0x80 -> {
                        if (i + 1 >= end) break
                        val note = msg[i].toInt() and 0x7F
                        val velocity = msg[i + 1].toInt() and 0x7F
                        val hex = formatHex(byteArrayOf(status.toByte(), note.toByte(), velocity.toByte()))
                        results += DecodedMidiMessage(
                            channel = channel,
                            type = MidiEventType.NOTE_OFF,
                            summary = "Note Off ${midiNoteName(note)} (#$note)",
                            rawHex = hex,
                            noteOff = note
                        )
                        i += 2
                    }

                    0xB0 -> {
                        if (i + 1 >= end) break
                        val cc = msg[i].toInt() and 0x7F
                        val value = msg[i + 1].toInt() and 0x7F
                        val hex = formatHex(byteArrayOf(status.toByte(), cc.toByte(), value.toByte()))
                        val label = when (cc) {
                            64 -> "Damper Pedal"
                            1 -> "Modulation"
                            7 -> "Channel Volume"
                            91 -> "Reverb Send"
                            93 -> "Chorus Send"
                            123 -> "All Notes Off"
                            else -> "CC #$cc"
                        }
                        results += DecodedMidiMessage(
                            channel = channel,
                            type = MidiEventType.CONTROL_CHANGE,
                            summary = "$label = $value",
                            rawHex = hex
                        )
                        i += 2
                    }

                    0xC0 -> {
                        if (i >= end) break
                        val program = msg[i].toInt() and 0x7F
                        val hex = formatHex(byteArrayOf(status.toByte(), program.toByte()))
                        results += DecodedMidiMessage(
                            channel = channel,
                            type = MidiEventType.PROGRAM_CHANGE,
                            summary = "Program Change → #$program",
                            rawHex = hex
                        )
                        i += 1
                    }

                    0xD0 -> {
                        // Channel Pressure (1 data byte)
                        if (i >= end) break
                        i += 1
                    }

                    0xA0 -> {
                        // Polyphonic Key Pressure (2 data bytes)
                        if (i + 1 >= end) break
                        i += 2
                    }

                    0xE0 -> {
                        if (i + 1 >= end) break
                        val lsb = msg[i].toInt() and 0x7F
                        val msb = msg[i + 1].toInt() and 0x7F
                        val bend = (msb shl 7) or lsb
                        val hex = formatHex(byteArrayOf(status.toByte(), lsb.toByte(), msb.toByte()))
                        results += DecodedMidiMessage(
                            channel = channel,
                            type = MidiEventType.PITCH_BEND,
                            summary = "Pitch Bend $bend",
                            rawHex = hex
                        )
                        i += 2
                    }

                    else -> i++
                }
            }
            return results
        }
    }
}
