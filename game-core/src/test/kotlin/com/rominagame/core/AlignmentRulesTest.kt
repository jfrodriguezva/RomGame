package com.rominagame.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlignmentRulesTest {
    @Test fun `tic tac toe detects every line direction`() {
        assertEquals('X', ticTacToeWinner(listOf('X','X','X',null,null,null,null,null,null)))
        assertEquals('O', ticTacToeWinner(listOf('O',null,null,'O',null,null,'O',null,null)))
        assertEquals('X', ticTacToeWinner(listOf('X',null,null,null,'X',null,null,null,'X')))
    }

    @Test fun `tic tac toe cpu completes a win`() {
        assertEquals(2, bestTicTacToeMove(listOf('O','O',null,'X','X',null,null,null,null)))
    }

    @Test fun `tic tac toe cpu blocks player`() {
        assertEquals(2, bestTicTacToeMove(listOf('X','X',null,null,'O',null,null,null,null)))
    }

    @Test fun `connect token falls to lowest available row`() {
        val first = dropConnectToken(List(42) { null }, 3, 'X')!!
        val second = dropConnectToken(first, 3, 'O')!!
        assertEquals('X', second[3]); assertEquals('O', second[10])
    }

    @Test fun `connect detects horizontal vertical and diagonal wins`() {
        fun board(vararg cells: Pair<Int, Char>) = MutableList<Char?>(42) { null }.also { b -> cells.forEach { b[it.first] = it.second } }
        assertEquals('X', connectWinner(board(0 to 'X',1 to 'X',2 to 'X',3 to 'X')))
        assertEquals('O', connectWinner(board(0 to 'O',7 to 'O',14 to 'O',21 to 'O')))
        assertEquals('X', connectWinner(board(0 to 'X',8 to 'X',16 to 'X',24 to 'X')))
    }

    @Test fun `connect cpu wins before blocking`() {
        val b = MutableList<Char?>(42) { null }.also { it[0]='O';it[1]='O';it[2]='O';it[7]='X';it[8]='X';it[9]='X' }
        assertEquals(3, bestConnectMove(b))
    }

    @Test fun `full connect column rejects another token`() {
        val b = List<Char?>(42) { if (it % 7 == 0) 'X' else null }
        assertNull(dropConnectToken(b, 0, 'O'))
    }

    @Test fun `duel has complete cyclic outcomes`() {
        assertEquals(DuelOutcome.WIN, duelOutcome(DuelChoice.ROCK, DuelChoice.SCISSORS))
        assertEquals(DuelOutcome.LOSE, duelOutcome(DuelChoice.ROCK, DuelChoice.PAPER))
        assertEquals(DuelOutcome.DRAW, duelOutcome(DuelChoice.ROCK, DuelChoice.ROCK))
        assertTrue(DuelChoice.entries.all { a -> DuelChoice.entries.all { b -> duelOutcome(a,b) in DuelOutcome.entries } })
    }
}
