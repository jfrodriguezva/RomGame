package com.rominagame.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguageRulesTest {
    @Test fun `all twelve consolidated language modes are retained`() {
        assertEquals(12, languageFamilies.sumOf { it.modes.size })
    }
    @Test fun `answers remain valid across levels`() {
        languageFamilies.forEach { family -> family.modes.indices.forEach { mode -> (1..20).forEach { level ->
            val challenge = languageChallenge(family.id, mode, level)
            assertTrue(challenge.answer in challenge.options.indices)
        } } }
    }
    @Test fun `alphabet and word difficulty are bounded`() {
        assertEquals(5, alphabetLength(1)); assertEquals(14, alphabetLength(20)); assertEquals("SOL", buildWord(1))
    }
}
