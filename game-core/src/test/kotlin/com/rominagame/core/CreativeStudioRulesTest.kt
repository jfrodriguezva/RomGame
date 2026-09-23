package com.rominagame.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreativeStudioRulesTest {
    @Test fun escalaContieneSieteNotasNaturales() {
        assertEquals(listOf("Do", "Re", "Mi", "Fa", "Sol", "La", "Si"), studioNotes.map { it.label })
        assertTrue(studioNotes.zipWithNext().all { (a, b) -> a.frequency < b.frequency })
    }

    @Test fun estudioContieneLosSieteInstrumentosSolicitados() {
        assertEquals(
            listOf("Xilófono", "Piano", "Guitarra", "Flauta", "Trompeta", "Acordeón", "Arpa"),
            StudioInstrument.entries.map { it.label },
        )
    }

    @Test fun pizarraConservaHerramientasPaletaYMandala() {
        assertEquals(8, DrawingTool.entries.size)
        assertEquals(16, drawingColors.size)
        assertEquals(listOf(4, 10, 22, 44), drawingWidths.toList())
        assertEquals(listOf(1, 2, 4, 6, 8), drawingSymmetries.toList())
        assertEquals(8, symmetricPoints(10f, 20f, 100, 100, 8).size)
        assertEquals(listOf(70f to 40f, 30f to 60f), symmetricPoints(70f, 40f, 100, 100, 2))
    }
}
