package com.rominagame.core
import org.junit.Assert.*
import org.junit.Test
class LotteryRulesTest{
 @Test fun `mexican deck has thirty six unique child safe cards`(){assertEquals(36,mexicanLotteryDeck.size);assertEquals(36,mexicanLotteryDeck.map{it.name}.distinct().size)}
 @Test fun `traditional board has sixteen unique cards`(){val b=lotteryBoard(mexicanLotteryDeck);assertEquals(16,b.size);assertEquals(16,b.distinct().size)}
 @Test fun `marker only accepts currently called matching card`(){val b=lotteryBoard(mexicanLotteryDeck);assertTrue(canMarkLottery(b,b[3],3,emptySet()));assertFalse(canMarkLottery(b,b[3],2,emptySet()));assertFalse(canMarkLottery(b,b[3],3,setOf(3)))}
 @Test fun `loteria requires full sixteen card board`(){assertFalse(lotteryComplete((0..14).toSet()));assertTrue(lotteryComplete((0..15).toSet()))}
 @Test fun `bingo recognizes rows columns and diagonals`(){assertTrue(bingoLine(setOf(0,1,2)));assertTrue(bingoLine(setOf(0,3,6)));assertTrue(bingoLine(setOf(0,4,8)));assertFalse(bingoLine(setOf(0,1,4)))}
}
