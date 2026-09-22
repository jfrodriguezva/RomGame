package com.rominagame.core
import org.junit.Assert.*
import org.junit.Test
class SolitaireRulesTest{
 private fun c(s:Int,r:Int,up:Boolean=true)=FaceCard(PlayingCard(s,r),up)
 @Test fun `klondike deal has seven columns and twenty four stock cards`(){val s=newKlondike(fullDeck());assertEquals((1..7).toList(),s.columns.map{it.size});assertEquals(24,s.stock.size);assertTrue(s.columns.all{it.last().faceUp})}
 @Test fun `klondike sequence alternates color descending`(){assertTrue(validKlondikeSequence(listOf(c(0,8),c(1,7),c(3,6)),0));assertFalse(validKlondikeSequence(listOf(c(0,8),c(3,7)),0))}
 @Test fun `only kings enter an empty klondike column`(){assertTrue(canPlaceKlondike(PlayingCard(0,13),emptyList()));assertFalse(canPlaceKlondike(PlayingCard(0,12),emptyList()))}
 @Test fun `klondike move reveals source top`(){val s=KlondikeState(listOf(listOf(c(0,9,false),c(1,8)),listOf(c(3,9))),emptyList(),emptyList(),List(4){0});val moved=moveKlondikeColumns(s,0,1,1)!!;assertTrue(moved.columns[0].last().faceUp);assertEquals(8,moved.columns[1].last().card.rank)}
 @Test fun `foundation grows only by matching next rank`(){assertTrue(canPlaceFoundation(PlayingCard(2,1),0));assertFalse(canPlaceFoundation(PlayingCard(2,3),0))}
 @Test fun `spider deal uses one hundred four cards`(){val s=newSpider();assertEquals(10,s.columns.size);assertEquals(50,s.stock.size);assertEquals(54,s.columns.sumOf{it.size})}
 @Test fun `spider accepts descending sequence without color alternation`(){assertTrue(validSpiderSequence(listOf(c(0,8),c(0,7),c(0,6)),0));assertFalse(validSpiderSequence(listOf(c(0,8),c(0,6)),0))}
 @Test fun `spider recognizes king through ace tail`(){assertTrue(spiderComplete((13 downTo 1).map{c(0,it)}))}
 @Test fun `spider deals ten only when no column is empty`(){val s=newSpider();assertEquals(40,dealSpider(s)!!.stock.size);assertNull(dealSpider(s.copy(columns=s.columns.toMutableList().also{it[0]=emptyList()})))}
}
