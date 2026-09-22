package com.rominagame.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogicBlockRulesTest {
    @Test fun `three logical families preserve every consolidated mode`() {
        assertEquals(listOf(9, 7, 6), logicFamilies.map { it.modes.size })
    }

    @Test fun `round answers are always valid`() {
        logicFamilies.forEach { family ->
            family.modes.indices.forEach { mode ->
                (1..20).forEach { level ->
                    val round = logicRound(family.id, mode, level, level)
                    assertTrue(round.answer in round.options.indices)
                }
            }
        }
    }

    @Test fun `difficulty increases number of rounds`() {
        assertEquals(3, roundsForLevel(1))
        assertEquals(7, roundsForLevel(20))
    }

    @Test fun `sequence modes retain their canonical order`() {
        assertEquals(listOf("Lun", "Mar", "Mié"), sequenceFor(2, 1))
        assertEquals("Mariposa", sequenceFor(4, 20).last())
        assertEquals(7, sequenceFor(2, 20).size)
    }

    @Test fun `sensory classifications are balanced`() {
        (1..5).forEach { mode ->
            val round = sensorySort(mode)
            assertEquals(3, round.items.count { it.destination == 0 })
            assertEquals(3, round.items.count { it.destination == 1 })
        }
    }

    @Test fun `visual differences grow without exceeding the board`() {
        assertEquals(9, differenceCellCount(1))
        assertEquals(15, differenceCellCount(20))
    }
}
