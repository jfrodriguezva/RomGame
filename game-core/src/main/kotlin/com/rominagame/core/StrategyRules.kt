package com.rominagame.core

data class Checker(val row:Int,val col:Int,val player:Boolean,val king:Boolean=false,val id:Int=0)
data class CheckerMove(val piece:Checker,val row:Int,val col:Int,val captured:Checker?=null)
fun playableSquare(r:Int,c:Int)=(r+c)%2==1
fun initialCheckers():List<Checker>{var id=0;return(0..2).flatMap{r->(0..7).filter{playableSquare(r,it)}.map{c->Checker(r,c,false,id=id++)}}+(5..7).flatMap{r->(0..7).filter{playableSquare(r,it)}.map{c->Checker(r,c,true,id=id++)}}}
private fun checkerDirections(p:Checker)=if(p.king)listOf(-1 to-1,-1 to 1,1 to-1,1 to 1)else{val d=if(p.player)-1 else 1;listOf(d to-1,d to 1)}
fun checkerMoves(piece:Checker,board:List<Checker>):List<CheckerMove>{val occupied=board.associateBy{it.row to it.col};val simple=mutableListOf<CheckerMove>();val captures=mutableListOf<CheckerMove>();for((dr,dc)in checkerDirections(piece)){val r=piece.row+dr;val c=piece.col+dc;if(r !in 0..7||c !in 0..7)continue;val at=occupied[r to c];if(at==null)simple+=CheckerMove(piece,r,c)else if(at.player!=piece.player){val rr=r+dr;val cc=c+dc;if(rr in 0..7&&cc in 0..7&&occupied[rr to cc]==null)captures+=CheckerMove(piece,rr,cc,at)}};return captures.ifEmpty{simple}}
fun legalCheckerMoves(board:List<Checker>,player:Boolean):List<CheckerMove>{val moves=board.filter{it.player==player}.flatMap{checkerMoves(it,board)};return if(moves.any{it.captured!=null})moves.filter{it.captured!=null}else moves}
fun applyCheckerMove(board:List<Checker>,move:CheckerMove):List<Checker>{var p=move.piece.copy(row=move.row,col=move.col);if(!p.king&&p.row==(if(p.player)0 else 7))p=p.copy(king=true);return board.filter{it!=move.piece&&it!=move.captured}+p}

data class ChinesePiece(val row:Int,val col:Int,val player:Boolean,val id:Int=0)
data class ChineseMove(val piece:ChinesePiece,val row:Int,val col:Int)
val playerHome=listOf(0 to 0,0 to 1,0 to 2,1 to 0,1 to 1,1 to 2)
val cpuHome=listOf(6 to 5,6 to 6,6 to 7,7 to 5,7 to 6,7 to 7)
private val chineseDirections=listOf(-1 to-1,-1 to 0,-1 to 1,0 to-1,0 to 1,1 to-1,1 to 0,1 to 1)
fun initialChinese()=playerHome.mapIndexed{i,p->ChinesePiece(p.first,p.second,true,i)}+cpuHome.mapIndexed{i,p->ChinesePiece(p.first,p.second,false,i+6)}
private fun jumpTargets(origin:Pair<Int,Int>,occupied:Set<Pair<Int,Int>>,visited:MutableSet<Pair<Int,Int>>):Set<Pair<Int,Int>>{val out=mutableSetOf<Pair<Int,Int>>();for((dr,dc)in chineseDirections){val middle=origin.first+dr to origin.second+dc;val target=origin.first+2*dr to origin.second+2*dc;if(target.first !in 0..7||target.second !in 0..7||middle !in occupied||target in occupied||!visited.add(target))continue;out+=target;out+=jumpTargets(target,occupied,visited)};return out}
fun chineseTargets(piece:ChinesePiece,board:List<ChinesePiece>):List<Pair<Int,Int>>{val occupied=board.map{it.row to it.col}.toSet();val steps=chineseDirections.map{piece.row+it.first to piece.col+it.second}.filter{it.first in 0..7&&it.second in 0..7&&it !in occupied};return(steps+jumpTargets(piece.row to piece.col,occupied,mutableSetOf(piece.row to piece.col))).distinct()}
fun applyChineseMove(board:List<ChinesePiece>,move:ChineseMove)=board.filter{it!=move.piece}+move.piece.copy(row=move.row,col=move.col)
fun chineseWinner(board:List<ChinesePiece>,player:Boolean)=if(player)cpuHome.all{p->board.any{it.player&&it.row to it.col==p}}else playerHome.all{p->board.any{!it.player&&it.row to it.col==p}}

enum class ChessKind{PAWN,ROOK,KNIGHT,BISHOP,QUEEN,KING}
data class ChessPiece(val row:Int,val col:Int,val kind:ChessKind,val player:Boolean,val id:Int=0)
data class ChessMove(val piece:ChessPiece,val row:Int,val col:Int,val captured:ChessPiece?=null)
fun initialChess():List<ChessPiece>{val order=listOf(ChessKind.ROOK,ChessKind.KNIGHT,ChessKind.BISHOP,ChessKind.QUEEN,ChessKind.KING,ChessKind.BISHOP,ChessKind.KNIGHT,ChessKind.ROOK);var id=0;return(0..7).flatMap{c->listOf(ChessPiece(0,c,order[c],false,id++),ChessPiece(1,c,ChessKind.PAWN,false,id++),ChessPiece(6,c,ChessKind.PAWN,true,id++),ChessPiece(7,c,order[c],true,id++))}}
private fun onBoard(r:Int,c:Int)=r in 0..7&&c in 0..7
fun pseudoChessMoves(piece:ChessPiece,board:List<ChessPiece>):List<ChessMove>{val occupied=board.associateBy{it.row to it.col};val out=mutableListOf<ChessMove>();fun add(r:Int,c:Int):Boolean{if(!onBoard(r,c))return false;val at=occupied[r to c];if(at==null){out+=ChessMove(piece,r,c);return true};if(at.player!=piece.player)out+=ChessMove(piece,r,c,at);return false};when(piece.kind){
 ChessKind.PAWN->{val d=if(piece.player)-1 else 1;val start=if(piece.player)6 else 1;if(onBoard(piece.row+d,piece.col)&&occupied[piece.row+d to piece.col]==null){out+=ChessMove(piece,piece.row+d,piece.col);if(piece.row==start&&occupied[piece.row+2*d to piece.col]==null)out+=ChessMove(piece,piece.row+2*d,piece.col)};for(dc in listOf(-1,1)){val r=piece.row+d;val c=piece.col+dc;val at=occupied[r to c];if(onBoard(r,c)&&at!=null&&at.player!=piece.player)out+=ChessMove(piece,r,c,at)}}
 ChessKind.KNIGHT->listOf(-2 to-1,-2 to 1,-1 to-2,-1 to 2,1 to-2,1 to 2,2 to-1,2 to 1).forEach{add(piece.row+it.first,piece.col+it.second)}
 ChessKind.KING->for(dr in-1..1)for(dc in-1..1)if(dr!=0||dc!=0)add(piece.row+dr,piece.col+dc)
 else->{val dirs=when(piece.kind){ChessKind.ROOK->listOf(-1 to 0,1 to 0,0 to-1,0 to 1);ChessKind.BISHOP->listOf(-1 to-1,-1 to 1,1 to-1,1 to 1);else->listOf(-1 to 0,1 to 0,0 to-1,0 to 1,-1 to-1,-1 to 1,1 to-1,1 to 1)};for((dr,dc)in dirs){var r=piece.row+dr;var c=piece.col+dc;while(add(r,c)){r+=dr;c+=dc}}}}
 return out}
fun applyChessMove(board:List<ChessPiece>,move:ChessMove):List<ChessPiece>{var p=move.piece.copy(row=move.row,col=move.col);if(p.kind==ChessKind.PAWN&&p.row==(if(p.player)0 else 7))p=p.copy(kind=ChessKind.QUEEN);return board.filter{it!=move.piece&&it!=move.captured}+p}
fun kingInCheck(board:List<ChessPiece>,player:Boolean):Boolean{val king=board.find{it.player==player&&it.kind==ChessKind.KING}?:return false;return board.filter{it.player!=player}.any{p->pseudoChessMoves(p,board).any{it.row==king.row&&it.col==king.col}}}
fun legalChessMoves(board:List<ChessPiece>,player:Boolean)=board.filter{it.player==player}.flatMap{pseudoChessMoves(it,board)}.filter{!kingInCheck(applyChessMove(board,it),player)}
