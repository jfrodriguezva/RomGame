package com.rominagame.core

enum class DuelChoice { ROCK, PAPER, SCISSORS }
enum class DuelOutcome { WIN, DRAW, LOSE }

private val ticTacToeLines = listOf(
    intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
    intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
    intArrayOf(0, 4, 8), intArrayOf(2, 4, 6),
)

fun ticTacToeWinner(board: List<Char?>): Char? = ticTacToeLines.firstNotNullOfOrNull { line ->
    board[line[0]]?.takeIf { it == board[line[1]] && it == board[line[2]] }
}

fun bestTicTacToeMove(board: List<Char?>, cpu: Char = 'O', player: Char = 'X'): Int? {
    val empty = board.indices.filter { board[it] == null }
    if (empty.isEmpty()) return null
    fun score(state: List<Char?>, maximizing: Boolean, depth: Int): Int {
        ticTacToeWinner(state)?.let { return if (it == cpu) 10 - depth else depth - 10 }
        val spaces = state.indices.filter { state[it] == null }
        if (spaces.isEmpty()) return 0
        return if (maximizing) spaces.maxOf { i -> score(state.toMutableList().also { it[i] = cpu }, false, depth + 1) }
        else spaces.minOf { i -> score(state.toMutableList().also { it[i] = player }, true, depth + 1) }
    }
    return empty.maxBy { i -> score(board.toMutableList().also { it[i] = cpu }, false, 1) }
}

fun connectWinner(board: List<Char?>, columns: Int = 7, rows: Int = 6): Char? {
    fun at(c: Int, r: Int) = if (c in 0 until columns && r in 0 until rows) board[r * columns + c] else null
    for (r in 0 until rows) for (c in 0 until columns) {
        val token = at(c, r) ?: continue
        if (listOf(1 to 0, 0 to 1, 1 to 1, 1 to -1).any { (dc, dr) ->
                (1..3).all { step -> at(c + dc * step, r + dr * step) == token }
            }) return token
    }
    return null
}

fun dropConnectToken(board: List<Char?>, column: Int, token: Char, columns: Int = 7, rows: Int = 6): List<Char?>? {
    if (column !in 0 until columns || board.size != columns * rows) return null
    val row = (0 until rows).firstOrNull { board[it * columns + column] == null } ?: return null
    return board.toMutableList().also { it[row * columns + column] = token }
}

fun bestConnectMove(board: List<Char?>, cpu: Char = 'O', player: Char = 'X', columns: Int = 7, rows: Int = 6): Int? {
    val valid = (0 until columns).filter { dropConnectToken(board, it, cpu, columns, rows) != null }
    valid.firstOrNull { connectWinner(dropConnectToken(board, it, cpu, columns, rows)!!, columns, rows) == cpu }?.let { return it }
    valid.firstOrNull { connectWinner(dropConnectToken(board, it, player, columns, rows)!!, columns, rows) == player }?.let { return it }
    return valid.minByOrNull { kotlin.math.abs(it - columns / 2) }
}

fun duelOutcome(player: DuelChoice, opponent: DuelChoice): DuelOutcome = when {
    player == opponent -> DuelOutcome.DRAW
    player == DuelChoice.ROCK && opponent == DuelChoice.SCISSORS -> DuelOutcome.WIN
    player == DuelChoice.PAPER && opponent == DuelChoice.ROCK -> DuelOutcome.WIN
    player == DuelChoice.SCISSORS && opponent == DuelChoice.PAPER -> DuelOutcome.WIN
    else -> DuelOutcome.LOSE
}
