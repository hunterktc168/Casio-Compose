package com.example

import com.example.data.CdpS100Catalog
import com.example.midi.MidiMonitorService
import com.example.model.MidiEventType
import com.example.ui.components.MarkdownBlock
import com.example.ui.components.parseMarkdownBlocks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun catalog_containsAllTenTonesWithUniqueProgramIds() {
        assertEquals(10, CdpS100Catalog.presetTones.size)
        assertEquals("GRAND PIANO STANDARD", CdpS100Catalog.presetTones.first().name)
        assertEquals("JAZZ ORGAN", CdpS100Catalog.presetTones.last().name)
        val uniquePrograms = CdpS100Catalog.presetTones.map { it.midiProgramIndex }.toSet()
        assertEquals(10, uniquePrograms.size)
        assertEquals(4, CdpS100Catalog.specGroups.size)
        assertEquals(5, CdpS100Catalog.otgSetupSteps.size)
        assertEquals(6, CdpS100Catalog.softwareIntegrations.size)
    }

    @Test
    fun midiNoteName_mapsStandardPianoRange() {
        assertEquals("A0", MidiMonitorService.midiNoteName(21))
        assertEquals("C4", MidiMonitorService.midiNoteName(60))
        assertEquals("C8", MidiMonitorService.midiNoteName(108))
        assertTrue(CdpS100Catalog.reverbSettings.size == 5)
        assertTrue(CdpS100Catalog.chorusSettings.size == 5)
    }

    @Test
    fun decodeMidiPacket_handlesNoteOnRunningStatusAndDamper() {
        val packet = byteArrayOf(
            0x90.toByte(), 60, 100,
            64, 88,
            60, 0,
            0xB0.toByte(), 64, 127
        )
        val decoded = MidiMonitorService.decodeMidiPacket(packet, 0, packet.size)
        assertEquals(4, decoded.size)
        assertEquals(MidiEventType.NOTE_ON, decoded[0].type)
        assertEquals(60, decoded[0].noteOn)
        assertEquals(MidiEventType.NOTE_ON, decoded[1].type)
        assertEquals(64, decoded[1].noteOn)
        assertEquals(MidiEventType.NOTE_OFF, decoded[2].type)
        assertEquals(60, decoded[2].noteOff)
        assertEquals(MidiEventType.CONTROL_CHANGE, decoded[3].type)
        assertTrue(decoded[3].summary.contains("Damper Pedal = 127"))
    }

    @Test
    fun parseMarkdownBlocks_parsesHeadingsTablesAndLists() {
        val sample = """
            # Title
            
            Intro **bold** and `code`.
            
            | Layer | Technology |
            | :--- | :--- |
            | UI | Jetpack Compose |
            
            1. First step
            - Bullet item
        """.trimIndent()

        val blocks = parseMarkdownBlocks(sample)
        assertTrue(blocks[0] is MarkdownBlock.Heading)
        assertTrue(blocks[1] is MarkdownBlock.Paragraph)
        val table = blocks[2] as MarkdownBlock.TableBlock
        assertEquals(listOf("Layer", "Technology"), table.headers)
        assertEquals(listOf("UI", "Jetpack Compose"), table.rows.first())
        assertTrue(blocks[3] is MarkdownBlock.OrderedItem)
        assertTrue(blocks[4] is MarkdownBlock.BulletItem)
    }
}
