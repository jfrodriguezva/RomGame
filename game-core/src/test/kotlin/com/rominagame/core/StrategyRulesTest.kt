package com.rominagame.core
import org.junit.Assert.*
import org.junit.Test
class StrategyRulesTest{
 @Test fun `checkers starts with twelve pieces per side`(){val b=initialCheckers();assertEquals(12,b.count{it.player});assertEquals(12,b.count{!it.player})}
 @Test fun `checkers enforces board wide capture`(){val capturer=Checker(5,0,true);val enemy=Checker(4,1,false);val free=Checker(5,4,true);val moves=legalCheckerMoves(listOf(capturer,enemy,free),true);assertTrue(moves.isNotEmpty());assertTrue(moves.all{it.captured!=null})}
 @Test fun `checker crowns on opposite row`(){val p=Checker(1,0,true);val moved=applyCheckerMove(listOf(p),CheckerMove(p,0,1)).single();assertTrue(moved.king)}
 @Test fun `chinese checkers supports chained jumps without capture`(){val p=ChinesePiece(0,0,true);val board=listOf(p,ChinesePiece(1,1,true),ChinesePiece(3,3,false));val targets=chineseTargets(p,board);assertTrue(2 to 2 in targets);assertTrue(4 to 4 in targets);assertEquals(3,board.size)}
 @Test fun `chess initial position has thirty two pieces and twenty legal moves`(){val b=initialChess();assertEquals(32,b.size);assertEquals(20,legalChessMoves(b,true).size)}
 @Test fun `knight jumps over occupied pieces`(){val knight=initialChess().first{it.player&&it.kind==ChessKind.KNIGHT&&it.col==1};assertEquals(2,pseudoChessMoves(knight,initialChess()).size)}
 @Test fun `chess rejects moves that expose own king`(){val king=ChessPiece(7,4,ChessKind.KING,true);val shield=ChessPiece(6,4,ChessKind.ROOK,true);val enemy=ChessPiece(0,4,ChessKind.ROOK,false);val board=listOf(king,shield,enemy);assertTrue(legalChessMoves(board,true).none{it.piece==shield&&it.col!=4})}
 @Test fun `pawn promotes to queen`(){val p=ChessPiece(1,0,ChessKind.PAWN,true);assertEquals(ChessKind.QUEEN,applyChessMove(listOf(p),ChessMove(p,0,0)).single().kind)}
}
