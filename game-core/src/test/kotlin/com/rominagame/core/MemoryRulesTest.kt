package com.rominagame.core

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class MemoryRulesTest{
    @Test fun pairDeck_createsExactPairs(){val deck=pairDeck(8,Random(4));assertEquals(16,deck.size);assertTrue(deck.groupingBy{it}.eachCount().values.all{it==2})}
    @Test fun difficulty_growsWithoutExceedingBoard(){val first=memoryDifficulty(MemoryMode.PAIRS,1);val last=memoryDifficulty(MemoryMode.PAIRS,20);assertTrue(last.cells>first.cells);assertEquals(16,last.cells)}
    @Test fun searchDeck_hasRequestedTargets(){val deck=searchDeck(20,4,Random(9));assertEquals(20,deck.symbols.size);assertEquals(4,deck.targetIndexes.size);assertTrue(deck.targetIndexes.all{deck.symbols[it]==deck.target})}
    @Test fun allModesHaveStableIds(){assertEquals(4,MemoryMode.entries.map{it.id}.toSet().size)}
}
