package com.rominagame.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QualityRulesTest {
    @Test fun `score maps to zero through three stars`() {
        assertEquals(0, starsForScore(0)); assertEquals(1, starsForScore(200)); assertEquals(2, starsForScore(700)); assertEquals(3, starsForScore(900))
    }
    @Test fun `logical tutorials are specific`() {
        assertTrue(logicTutorial("percepcion").contains("clasifícalo")); assertTrue(logicTutorial("orden-secuencias").contains("Arrastra"))
    }
    @Test fun `language tutorials cover every mode`() {
        assertTrue((0..7).all { languageTutorial("palabras-sonidos", it).isNotBlank() })
        assertTrue((0..3).all { languageTutorial("construye-palabras", it).isNotBlank() })
    }
    @Test fun `curriculum tutorials cover all 34 modes`() {
        val tutorials = curriculumFamilies.flatMap { family -> family.modes.indices.map { curriculumTutorial(family.id, it) } }
        assertEquals(34, tutorials.size); assertTrue(tutorials.all { it.isNotBlank() })
    }
}
