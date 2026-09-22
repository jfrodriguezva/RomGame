package com.rominagame.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FinalBlockRulesTest {
    @Test fun `all fourteen final normal modes are retained`() = assertEquals(14, finalFamilies.sumOf { it.modes.size })
    @Test fun `target only accepts touches inside its radius`() { assertTrue(targetContains(100f,100f,120f,120f)); assertFalse(targetContains(100f,100f,300f,300f)) }
    @Test fun `difficulty remains bounded`() { assertEquals(4, mazeSize(1)); assertEquals(9, mazeSize(20)); assertEquals(9, targetHits(20)) }
    @Test fun `maze path connects start and finish`() { val p=mazePath(6);assertTrue(0 to 0 in p);assertTrue(5 to 5 in p) }
}
