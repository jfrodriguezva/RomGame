package com.rominagame.core

import org.junit.Assert.*
import org.junit.Test

class DeductionRulesTest{
 @Test fun `double six has 28 unique tiles`(){val set=doubleSixSet();assertEquals(28,set.size);assertEquals(28,set.distinctBy{it.a to it.b}.size)}
 @Test fun `tile is oriented on the right`(){val c=placeDomino(DominoChain(listOf(DominoTile(0,2,4)),2,4),DominoTile(1,6,4),DominoEnd.RIGHT);assertEquals(6,c.right);assertEquals(DominoTile(1,4,6),c.tiles.last())}
 @Test fun `tile is oriented on the left`(){val c=placeDomino(DominoChain(listOf(DominoTile(0,2,4)),2,4),DominoTile(1,2,5),DominoEnd.LEFT);assertEquals(5,c.left);assertEquals(DominoTile(1,5,2),c.tiles.first())}
 @Test(expected=IllegalArgumentException::class) fun `invalid domino placement is rejected`(){placeDomino(DominoChain(listOf(DominoTile(0,2,4)),2,4),DominoTile(1,5,6),DominoEnd.RIGHT)}
 @Test fun `blocked game requires empty pool and no legal tile`(){val c=DominoChain(listOf(DominoTile(0,2,4)),2,4);assertTrue(dominoBlocked(listOf(listOf(DominoTile(1,5,6)),listOf(DominoTile(2,0,1))),emptyList(),c))}
 @Test fun `question answer uses the secret attribute`(){val dog=guessCharacters.first();assertTrue(answerQuestion(dog,GuessAttribute.SIZE,"grande"));assertFalse(answerQuestion(dog,GuessAttribute.HABITAT,"agua"))}
 @Test fun `yes answer eliminates characters without attribute`(){val removed=eliminateByAnswer(guessCharacters,GuessAttribute.COLOR,"gris",true);assertFalse(1 in removed);assertFalse(5 in removed);assertTrue(0 in removed)}
 @Test fun `no answer eliminates characters with attribute`(){val removed=eliminateByAnswer(guessCharacters,GuessAttribute.HABITAT,"agua",false);assertTrue(3 in removed);assertFalse(0 in removed)}
}
