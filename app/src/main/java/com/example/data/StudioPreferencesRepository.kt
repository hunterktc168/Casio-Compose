package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.studioDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "cdp_s100_studio_prefs"
)

data class PersistedStudioSettings(
    val completedOtgStepIds: Set<Int> = emptySet(),
    val metronomeBpm: Int = 120,
    val metronomeBeatsPerBar: Int = 4,
    val primaryToneId: Int = 1,
    val secondaryToneId: Int = 8,
    val layerEnabled: Boolean = false,
    val reverbLevel: Int = 2,
    val chorusLevel: Int = 0
)

class StudioPreferencesRepository(private val context: Context) {

    private object Keys {
        val COMPLETED_OTG_STEPS = stringSetPreferencesKey("completed_otg_steps")
        val METRONOME_BPM = intPreferencesKey("metronome_bpm")
        val METRONOME_BEATS = intPreferencesKey("metronome_beats")
        val PRIMARY_TONE_ID = intPreferencesKey("primary_tone_id")
        val SECONDARY_TONE_ID = intPreferencesKey("secondary_tone_id")
        val LAYER_ENABLED = booleanPreferencesKey("layer_enabled")
        val REVERB_LEVEL = intPreferencesKey("reverb_level")
        val CHORUS_LEVEL = intPreferencesKey("chorus_level")
    }

    val settingsFlow: Flow<PersistedStudioSettings> = context.studioDataStore.data.map { prefs ->
        val completedSteps = prefs[Keys.COMPLETED_OTG_STEPS]
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()

        PersistedStudioSettings(
            completedOtgStepIds = completedSteps,
            metronomeBpm = (prefs[Keys.METRONOME_BPM] ?: 120).coerceIn(20, 255),
            metronomeBeatsPerBar = (prefs[Keys.METRONOME_BEATS] ?: 4).coerceIn(0, 9),
            primaryToneId = (prefs[Keys.PRIMARY_TONE_ID] ?: 1).coerceIn(1, 10),
            secondaryToneId = (prefs[Keys.SECONDARY_TONE_ID] ?: 8).coerceIn(1, 10),
            layerEnabled = prefs[Keys.LAYER_ENABLED] ?: false,
            reverbLevel = (prefs[Keys.REVERB_LEVEL] ?: 2).coerceIn(0, 4),
            chorusLevel = (prefs[Keys.CHORUS_LEVEL] ?: 0).coerceIn(0, 4)
        )
    }

    suspend fun toggleOtgStep(stepNumber: Int) {
        context.studioDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_OTG_STEPS]?.toMutableSet() ?: mutableSetOf()
            val key = stepNumber.toString()
            if (current.contains(key)) {
                current.remove(key)
            } else {
                current.add(key)
            }
            prefs[Keys.COMPLETED_OTG_STEPS] = current
        }
    }

    suspend fun resetOtgChecklist() {
        context.studioDataStore.edit { prefs ->
            prefs[Keys.COMPLETED_OTG_STEPS] = emptySet()
        }
    }

    suspend fun saveMetronomeBpm(bpm: Int) {
        context.studioDataStore.edit { prefs ->
            prefs[Keys.METRONOME_BPM] = bpm.coerceIn(20, 255)
        }
    }

    suspend fun saveMetronomeBeats(beatsPerBar: Int) {
        context.studioDataStore.edit { prefs ->
            prefs[Keys.METRONOME_BEATS] = beatsPerBar.coerceIn(0, 9)
        }
    }

    suspend fun savePrimaryTone(primaryToneId: Int) {
        context.studioDataStore.edit { prefs ->
            prefs[Keys.PRIMARY_TONE_ID] = primaryToneId.coerceIn(1, 10)
        }
    }

    suspend fun saveSecondaryTone(secondaryToneId: Int, enableLayer: Boolean? = null) {
        context.studioDataStore.edit { prefs ->
            prefs[Keys.SECONDARY_TONE_ID] = secondaryToneId.coerceIn(1, 10)
            if (enableLayer != null) {
                prefs[Keys.LAYER_ENABLED] = enableLayer
            }
        }
    }

    suspend fun saveLayerEnabled(enabled: Boolean) {
        context.studioDataStore.edit { prefs ->
            prefs[Keys.LAYER_ENABLED] = enabled
        }
    }

    suspend fun saveReverbLevel(reverbLevel: Int) {
        context.studioDataStore.edit { prefs ->
            prefs[Keys.REVERB_LEVEL] = reverbLevel.coerceIn(0, 4)
        }
    }

    suspend fun saveChorusLevel(chorusLevel: Int) {
        context.studioDataStore.edit { prefs ->
            prefs[Keys.CHORUS_LEVEL] = chorusLevel.coerceIn(0, 4)
        }
    }
}
