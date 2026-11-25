package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class MetronomeEngine {

    private val sampleRate = 44_100
    private val clickDurationSamples = (sampleRate * 0.025).toInt()
    private val accentWave: ShortArray = buildClickWave(freqHz = 1280.0, amplitude = 0.85)
    private val regularWave: ShortArray = buildClickWave(freqHz = 840.0, amplitude = 0.60)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var playJob: Job? = null
    private val sessionCounter = AtomicLong(0L)

    @Volatile
    private var currentBpm: Int = 120

    @Volatile
    private var currentBeatsPerBar: Int = 4

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentBeatIndex = MutableStateFlow(0)
    val currentBeatIndex: StateFlow<Int> = _currentBeatIndex.asStateFlow()

    private val _beatTickCounter = MutableStateFlow(0L)
    val beatTickCounter: StateFlow<Long> = _beatTickCounter.asStateFlow()

    private fun buildClickWave(freqHz: Double, amplitude: Double): ShortArray {
        return ShortArray(clickDurationSamples) { i ->
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 140.0)
            val sample = sin(2.0 * PI * freqHz * t) * envelope * amplitude
            (sample * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }
    }

    fun updateConfig(bpm: Int, beatsPerBar: Int) {
        currentBpm = bpm.coerceIn(20, 255)
        currentBeatsPerBar = beatsPerBar.coerceIn(0, 9)
    }

    fun start(bpm: Int, beatsPerBar: Int) {
        updateConfig(bpm, beatsPerBar)
        if (_isPlaying.value) return
        val sessionId = sessionCounter.incrementAndGet()
        val previousJob = playJob
        _isPlaying.value = true

        playJob = scope.launch {
            previousJob?.cancelAndJoin()
            if (!isActive || sessionCounter.get() != sessionId || !_isPlaying.value) return@launch

            val minBufBytes = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(2048)

            var audioTrack = runCatching {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufBytes * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            }.getOrNull()

            val startedOk = runCatching {
                audioTrack?.play()
                audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING
            }.getOrDefault(false)

            if (!startedOk) {
                runCatching { audioTrack?.release() }
                audioTrack = null
            }

            try {
                val chunkSamples = 512
                val chunkDurationMs = ((chunkSamples * 1000L) / sampleRate).coerceAtLeast(1L)
                val buffer = ShortArray(chunkSamples)
                var samplesUntilNextBeat = 0L
                var activeClick: ShortArray? = null
                var clickPos = 0
                var beatCounter = 0

                while (isActive && _isPlaying.value && sessionCounter.get() == sessionId) {
                    for (i in 0 until chunkSamples) {
                        if (samplesUntilNextBeat <= 0L) {
                            val beats = currentBeatsPerBar
                            val displayBeat = if (beats <= 0) {
                                1
                            } else {
                                (beatCounter % beats) + 1
                            }
                            _currentBeatIndex.value = displayBeat
                            _beatTickCounter.value = _beatTickCounter.value + 1L

                            val isAccent = beats > 1 && displayBeat == 1
                            activeClick = if (isAccent) accentWave else regularWave
                            clickPos = 0
                            beatCounter++

                            val safeBpm = currentBpm.coerceIn(20, 255)
                            samplesUntilNextBeat = ((sampleRate * 60.0) / safeBpm).toLong().coerceAtLeast(1L)
                        }

                        val wave = activeClick
                        buffer[i] = if (wave != null && clickPos < wave.size) {
                            wave[clickPos++]
                        } else {
                            activeClick = null
                            0
                        }
                        samplesUntilNextBeat--
                    }

                    val track = audioTrack
                    if (track != null) {
                        val written = track.write(buffer, 0, chunkSamples)
                        if (written <= 0) {
                            runCatching { track.release() }
                            audioTrack = null
                            delay(chunkDurationMs)
                        }
                    } else {
                        delay(chunkDurationMs)
                    }
                }
            } finally {
                runCatching {
                    audioTrack?.stop()
                    audioTrack?.release()
                }
                if (sessionCounter.get() == sessionId) {
                    _currentBeatIndex.value = 0
                }
            }
        }
    }

    fun stop() {
        sessionCounter.incrementAndGet()
        _isPlaying.value = false
        playJob?.cancel()
        playJob = null
        _currentBeatIndex.value = 0
    }

    fun release() {
        stop()
        scope.cancel()
    }
}
