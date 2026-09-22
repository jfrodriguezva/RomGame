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
import kotlin.random.Random

class PathBoardGame(private val gameId:String,private val onComplete:(String,Int,Int)->Unit={_,_,_->}):ApplicationAdapter(){
 private val viewport=FitViewport(960f,540f);private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
 private var position=0;private var roll:Int?=null;private var pending:PathMove?=null;private var dragging=false;private var selected=false;private var drag=Vector2();private var message="Toca el dado para tirar";private var paused=false;private var tutorial=true;private var challenges=0
 override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=BitmapFont().apply{data.setScale(1.45f)};Gdx.input.inputProcessor=object:InputAdapter(){
  override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{val v=point(x,y);if(tutorial){tutorial=false;return true};if(paused){if(v.y>485f&&v.x in 158f..288f)paused=false;return true};if(v.y>485f){if(v.x<148f)reset()else if(v.x<298f)paused=true;return true};if(gameId=="dado"){if(v.x in 90f..250f&&v.y in 170f..330f){dragging=true;drag.set(v.x,v.y)};return true};if(roll==null&&v.x in 65f..205f&&v.y in 115f..255f){roll=Random.nextInt(1,7);pending=pathMove(position,roll!!,last(),jumps());message=if(pending==null)"Necesitas el número exacto para llegar" else "Arrastra la ficha a ${pending!!.rolledTarget}";if(pending==null)roll=null;return true};if(pending!=null&&pawnContains(v.x,v.y)){dragging=true;selected=true;drag.set(v.x,v.y);return true};if(selected)drop(v.x,v.y);return true}
  override fun touchDragged(x:Int,y:Int,p:Int):Boolean{if(!dragging||paused||tutorial)return false;val v=point(x,y);drag.set(v.x,v.y);return true}
  override fun touchUp(x:Int,y:Int,p:Int,b:Int):Boolean{if(!dragging)return false;dragging=false;val v=point(x,y);if(gameId=="dado")dropDie(v.x,v.y)else drop(v.x,v.y);return true}
 }}
 private fun point(x:Int,y:Int)=viewport.unproject(Vector3(x.toFloat(),y.toFloat(),0f));private fun last()=if(gameId=="oca")50 else 40;private fun jumps()=if(gameId=="oca")gooseJumps else snakesLaddersJumps
 private fun reset(){position=0;roll=null;pending=null;selected=false;dragging=false;paused=false;message=if(gameId=="dado")"Arrastra el dado a la zona de lanzamiento" else "Toca el dado para tirar"}
 private fun pawnCenter()=if(position==0)125f to 335f else pathCellCenter(position,last())
 private fun pawnContains(x:Float,y:Float):Boolean{val p=pawnCenter();val dx=x-p.first;val dy=y-p.second;return dx*dx+dy*dy<45f*45f}
 private fun drop(x:Float,y:Float){val move=pending?:return;val target=pathCellCenter(move.rolledTarget,last());val dx=x-target.first;val dy=y-target.second;if(dx*dx+dy*dy>42f*42f){message="Suelta en la casilla resaltada";return};position=move.finalTarget;roll=null;pending=null;selected=false;message=move.message?:"Avanzaste a $position";if(position==last()){message="¡Llegaste a la meta!";onComplete(gameId,1,1000)}}
 private fun dropDie(x:Float,y:Float){if(x !in 500f..820f||y !in 170f..390f){message="Suelta el dado dentro de la zona";return};val value=Random.nextInt(1,7);message="Dado $value · ${movementChallenges.random()}";challenges++;if(challenges==6)onComplete(gameId,1,1000)}
 override fun render(){Gdx.gl.glClearColor(.035f,.055f,.09f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;button(18f,"REINICIAR");button(158f,if(paused)"SEGUIR" else "PAUSA");text(title(),700f,516f,Color.WHITE);if(gameId=="dado")renderChallenge()else renderBoard();text(message,480f,55f,Color.LIGHT_GRAY);if(dragging){if(gameId=="dado")die(drag.x,drag.y)else pawn(drag.x,drag.y)};if(tutorial)overlay("CÓMO JUGAR",if(gameId=="dado")"Arrastra el dado a la zona y realiza el reto." else "Tira el dado y arrastra tu ficha a la casilla marcada.");if(paused)overlay("PAUSA","La partida está detenida.")}
 private fun title()=when(gameId){"oca"->"JUEGO DE LA OCA";"serpientes"->"SERPIENTES Y ESCALERAS";else->"DADO DE RETOS · $challenges/6"}
 private fun renderBoard(){die(135f,185f);val total=last();for(i in 1..total){val p=pathCellCenter(i,total);val special=i in jumps();val target=pending?.rolledTarget==i;box(p.first-26f,p.second-29f,52f,58f,when{target->Color(0xE0C23CFF.toInt());special->Color(0x4C7A3AFF.toInt());else->Color(0x243E78FF.toInt())});text(i.toString(),p.first,p.second+7f,Color.WHITE)};val p=pawnCenter();pawn(p.first,p.second);roll?.let{text("DADO: $it",135f,105f,Color.WHITE)}}
 private fun renderChallenge(){die(170f,250f);box(500f,170f,320f,220f,Color(0x182847FF.toInt()));text("ZONA DE LANZAMIENTO",660f,290f,Color.WHITE)}
 private fun pawn(x:Float,y:Float){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=Color(0xE0C23CFF.toInt());shapes.circle(x,y,24f,24);shapes.end()}
 private fun die(x:Float,y:Float){box(x-52f,y-52f,104f,104f,Color(0xF4F0E8FF.toInt()));text("DADO",x,y+7f,Color(0x182847FF.toInt()))}
 private fun button(x:Float,label:String){box(x,488f,130f,40f,Color(0x3D507AFF.toInt()));text(label,x+65f,516f,Color.WHITE)}
 private fun overlay(title:String,body:String){box(150f,120f,660f,335f,Color(0x111B30F2.toInt()));text(title,480f,360f,Color(0xE0C23CFF.toInt()));text(body,480f,270f,Color.WHITE);text("TOCA PARA CONTINUAR",480f,180f,Color.LIGHT_GRAY)}
 private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.rect(x,y,w,h);shapes.end()};private fun text(v:String,x:Float,y:Float,c:Color){batch.begin();font.color=c;font.draw(batch,v,x-220f,y,440f,Align.center,false);batch.end()}
 override fun resize(w:Int,h:Int)=viewport.update(w,h,true);override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
