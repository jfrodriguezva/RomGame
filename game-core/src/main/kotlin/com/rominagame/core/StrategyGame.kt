package com.rominagame.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport

class StrategyGame(private val gameId:String,private val onComplete:(String,Int,Int)->Unit={_,_,_->}):ApplicationAdapter(){
 private val viewport=FitViewport(960f,540f);private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
 private var checkers=initialCheckers();private var chinese=initialChinese();private var chess=initialChess();private var selectedId:Int?=null;private var dragging=false;private var drag=Vector2();private var turn=true;private var vsCpu=true;private var paused=false;private var tutorial=true;private var message="Arrastra una pieza a un destino resaltado";private var forcedChecker:Int?=null
 override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=BitmapFont().apply{data.setScale(1.3f)};Gdx.input.inputProcessor=object:InputAdapter(){
  override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{val v=point(x,y);if(tutorial){tutorial=false;return true};if(paused){if(v.y>485f&&v.x in 298f..428f)paused=false;return true};if(v.y>485f){when{v.x<148f->reset();v.x<288f->{vsCpu=!vsCpu;reset()};v.x<438f->paused=true};return true};val cell=cellAt(v.x,v.y)?:return true;if(selectedId!=null){move(cell.first,cell.second);return true};if(select(cell.first,cell.second)){dragging=true;drag.set(v.x,v.y)};return true}
  override fun touchDragged(x:Int,y:Int,p:Int):Boolean{if(!dragging||paused||tutorial)return false;val v=point(x,y);drag.set(v.x,v.y);return true}
  override fun touchUp(x:Int,y:Int,p:Int,b:Int):Boolean{if(!dragging)return false;dragging=false;val v=point(x,y);cellAt(v.x,v.y)?.let{move(it.first,it.second)};return true}
 }}
 private fun point(x:Int,y:Int)=viewport.unproject(Vector3(x.toFloat(),y.toFloat(),0f));private fun cellAt(x:Float,y:Float):Pair<Int,Int>?{if(x !in 280f..680f||y !in 70f..470f)return null;return(7-((y-70f)/50f).toInt().coerceAtMost(7)) to ((x-280f)/50f).toInt().coerceAtMost(7)}
 private fun reset(){checkers=initialCheckers();chinese=initialChinese();chess=initialChess();selectedId=null;dragging=false;turn=true;paused=false;forcedChecker=null;message="Arrastra una pieza a un destino resaltado"}
 private fun select(r:Int,c:Int):Boolean{val id=when(gameId){"damas"->checkers.find{it.row==r&&it.col==c&&it.player==turn&&(forcedChecker==null||it.id==forcedChecker)}?.id;"damas-chinas"->chinese.find{it.row==r&&it.col==c&&it.player==turn}?.id;else->chess.find{it.row==r&&it.col==c&&it.player==turn}?.id};if(id==null){message="Elige una pieza de tu turno";return false};selectedId=id;return true}
 private fun targets():List<Pair<Int,Int>>{val id=selectedId?:return emptyList();return when(gameId){"damas"->{val p=checkers.first{it.id==id};legalCheckerMoves(checkers,turn).filter{it.piece.id==id}.map{it.row to it.col}};"damas-chinas"->{val p=chinese.first{it.id==id};chineseTargets(p,chinese)};else->legalChessMoves(chess,turn).filter{it.piece.id==id}.map{it.row to it.col}}}
 private fun move(r:Int,c:Int){val id=selectedId?:return;when(gameId){"damas"->{val legal=legalCheckerMoves(checkers,turn).firstOrNull{it.piece.id==id&&it.row==r&&it.col==c}?:return invalid();checkers=applyCheckerMove(checkers,legal);if(legal.captured!=null){val moved=checkers.first{it.id==id};val more=checkerMoves(moved,checkers).filter{it.captured!=null};if(more.isNotEmpty()){forcedChecker=id;selectedId=id;message="Captura otra vez con la misma pieza";return}};forcedChecker=null};"damas-chinas"->{val p=chinese.first{it.id==id};if(r to c !in chineseTargets(p,chinese))return invalid();chinese=applyChineseMove(chinese,ChineseMove(p,r,c))};else->{val legal=legalChessMoves(chess,turn).firstOrNull{it.piece.id==id&&it.row==r&&it.col==c}?:return invalid();chess=applyChessMove(chess,legal)}};selectedId=null;endTurn()}
 private fun invalid(){message="Ese destino no es válido";selectedId=null}
 private fun endTurn(){if(winner(turn)){message="¡Ganó el Jugador ${if(turn)1 else 2}!";onComplete(gameId,1,1000);return};turn=!turn;message="Turno del Jugador ${if(turn)1 else 2}";if(vsCpu&&!turn)cpuMove()}
 private fun winner(side:Boolean)=when(gameId){"damas"->legalCheckerMoves(checkers,!side).isEmpty();"damas-chinas"->chineseWinner(chinese,side);else->legalChessMoves(chess,!side).isEmpty()&&kingInCheck(chess,!side)}
 private fun cpuMove(){when(gameId){"damas"->{do{val moves=legalCheckerMoves(checkers,false);if(moves.isEmpty())break;val m=moves.maxByOrNull{if(it.captured!=null)10 else it.row}?:break;checkers=applyCheckerMove(checkers,m);val moved=checkers.first{it.id==m.piece.id};val more=if(m.captured!=null)checkerMoves(moved,checkers).filter{it.captured!=null}else emptyList()}while(more.isNotEmpty())};"damas-chinas"->{val moves=chinese.filter{!it.player}.flatMap{p->chineseTargets(p,chinese).map{ChineseMove(p,it.first,it.second)}};moves.maxByOrNull{(it.piece.row+it.piece.col)-(it.row+it.col)}?.let{chinese=applyChineseMove(chinese,it)}};else->{val moves=legalChessMoves(chess,false);moves.maxByOrNull{value(it.captured?.kind)}?.let{chess=applyChessMove(chess,it)}}};if(winner(false)){message="Ganó la computadora";return};turn=true;message="Tu turno"}
 private fun value(k:ChessKind?)=when(k){ChessKind.QUEEN->9;ChessKind.ROOK->5;ChessKind.BISHOP,ChessKind.KNIGHT->3;ChessKind.PAWN->1;else->0}
 override fun render(){Gdx.gl.glClearColor(.04f,.055f,.08f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;button(18f,"REINICIAR");button(158f,if(vsCpu)"VS CPU" else "2 JUG.");button(298f,if(paused)"SEGUIR" else "PAUSA");text(title(),700f,516f,Color.WHITE);renderBoard();text(message,480f,42f,Color.LIGHT_GRAY);if(dragging)piece(drag.x,drag.y,turn,"",22f);if(tutorial)overlay("CÓMO JUGAR","Arrastra una pieza a uno de los destinos resaltados.");if(paused)overlay("PAUSA","La partida está detenida.")}
 private fun title()=when(gameId){"damas"->"DAMAS";"damas-chinas"->"DAMAS CHINAS";else->"AJEDREZ"}
 private fun renderBoard(){repeat(8){r->repeat(8){c->val x=280f+c*50f;val y=70f+(7-r)*50f;box(x,y,50f,50f,if((r+c)%2==0)Color(0xE7D7B7FF.toInt())else Color(0x5A3B28FF.toInt()))}};targets().forEach{(r,c)->box(288f+c*50f,78f+(7-r)*50f,34f,34f,Color(0x4C9A5FAA.toInt()))};when(gameId){"damas"->checkers.forEach{piece(305f+it.col*50f,95f+(7-it.row)*50f,it.player,if(it.king)"K" else "")};"damas-chinas"->chinese.forEach{piece(305f+it.col*50f,95f+(7-it.row)*50f,it.player,"")};else->chess.forEach{piece(305f+it.col*50f,95f+(7-it.row)*50f,it.player,chessLabel(it.kind))}}}
 private fun chessLabel(k:ChessKind)=when(k){ChessKind.PAWN->"P";ChessKind.ROOK->"T";ChessKind.KNIGHT->"C";ChessKind.BISHOP->"A";ChessKind.QUEEN->"D";ChessKind.KING->"R"}
 private fun piece(x:Float,y:Float,player:Boolean,label:String,radius:Float=20f){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=if(player)Color(0xE0C23CFF.toInt())else Color(0xD9433AFF.toInt());shapes.circle(x,y,radius,24);shapes.end();if(label.isNotEmpty())text(label,x,y+7f,Color(0x111622FF.toInt()),40f)}
 private fun button(x:Float,label:String){box(x,488f,130f,40f,Color(0x3D507AFF.toInt()));text(label,x+65f,516f,Color.WHITE,120f)};private fun overlay(t:String,b:String){box(150f,120f,660f,335f,Color(0x111B30F2.toInt()));text(t,480f,360f,Color(0xE0C23CFF.toInt()));text(b,480f,270f,Color.WHITE);text("TOCA PARA CONTINUAR",480f,180f,Color.LIGHT_GRAY)}
 private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.rect(x,y,w,h);shapes.end()};private fun text(v:String,x:Float,y:Float,c:Color,w:Float=440f){batch.begin();font.color=c;font.draw(batch,v,x-w/2f,y,w,Align.center,false);batch.end()}
 override fun resize(w:Int,h:Int)=viewport.update(w,h,true);override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
