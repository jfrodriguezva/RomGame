package com.rominagame.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurriculumRulesTest {
    @Test fun `all 34 curriculum modes are retained`() = assertEquals(34, curriculumFamilies.sumOf { it.modes.size })
    @Test fun `quiz answers remain valid`() { curriculumFamilies.forEach { f -> f.modes.indices.forEach { m -> (1..20).forEach { l -> val q=curriculumChallenge(f.id,m,l); assertTrue(q.answer in q.options.indices) } } } }
    @Test fun `sort destinations are valid`() { curriculumFamilies.forEach { f -> f.modes.indices.filter { f.modes[it].mechanic==CurriculumMechanic.SORT }.forEach { m -> repeat(8) { assertTrue(sortItem(f.id,m,it).destination in 0..1) } } } }
    @Test fun `count difficulty is bounded`() { assertEquals(2, countTarget(1)); assertEquals(12, countTarget(20)) }
}
