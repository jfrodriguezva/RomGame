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

class LotteryGame(private val bingo:Boolean,private val onComplete:(String,Int,Int)->Unit={_,_,_->}):ApplicationAdapter(){
 private val viewport=FitViewport(960f,540f);private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
 private var board=emptyList<LotteryCard>();private var calls=emptyList<LotteryCard>();private var calledIndex=0;private var marked=emptySet<Int>();private var selected=false;private var dragging=false;private var drag=Vector2();private var paused=false;private var tutorial=true;private var message="Arrastra una ficha sobre la carta cantada"
 override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=GameTypography.create(20);reset();Gdx.input.inputProcessor=object:InputAdapter(){
  override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{val v=point(x,y);if(tutorial){tutorial=false;return true};if(paused){if(v.y>485f&&v.x in 158f..288f)paused=false;return true};if(v.y>485f){if(v.x<148f)reset()else if(v.x<298f)paused=true;return true};if(!bingo&&v.x in 50f..210f&&v.y in 80f..145f){notOnBoard();return true};if(v.x in 55f..175f&&v.y in 205f..325f){selected=true;dragging=true;drag.set(v.x,v.y);return true};if(selected)cellAt(v.x,v.y)?.let{mark(it)};return true}
  override fun touchDragged(x:Int,y:Int,p:Int):Boolean{if(!dragging||paused||tutorial)return false;val v=point(x,y);drag.set(v.x,v.y);return true}
  override fun touchUp(x:Int,y:Int,p:Int,b:Int):Boolean{if(!dragging)return false;dragging=false;val v=point(x,y);cellAt(v.x,v.y)?.let{mark(it)};return true}
 }}
 private fun point(x:Int,y:Int)=viewport.unproject(Vector3(x.toFloat(),y.toFloat(),0f));private fun id()=if(bingo)"bingo" else "loteria";private fun side()=if(bingo)3 else 4
 private fun reset(){if(bingo){board=bingoDeck.shuffled().mapIndexed{i,n->LotteryCard(n,(i+1).toString())};calls=board.shuffled()}else{board=lotteryBoard();calls=mexicanLotteryDeck.shuffled()};calledIndex=0;marked=emptySet();selected=false;dragging=false;paused=false;message="Arrastra una ficha sobre la carta cantada"}
 private fun called()=calls[calledIndex.coerceAtMost(calls.lastIndex)];private fun next(){if(calledIndex<calls.lastIndex)calledIndex++}
 private fun cellAt(x:Float,y:Float):Int?{val s=side();val left=300f;val bottom=80f;val size=if(bingo)120f else 90f;if(x !in left..left+s*size||y !in bottom..bottom+s*size)return null;val col=((x-left)/size).toInt().coerceAtMost(s-1);val row=((y-bottom)/size).toInt().coerceAtMost(s-1);return row*s+col}
 private fun mark(index:Int){if(!canMarkLottery(board,called(),index,marked)){message="Esa carta no fue cantada";selected=false;return};marked=marked+index;selected=false;message="Correcto: ${called().name}";val won=if(bingo)bingoLine(marked)else lotteryComplete(marked);if(won){message=if(bingo)"¡BINGO!" else "¡LOTERÍA!";onComplete(id(),1,1000)}else next()}
 private fun notOnBoard(){if(board.any{it.name==called().name})message="Sí la tienes: búscala en el cartón" else{message="Correcto, no está en tu cartón";next()}}
 override fun render(){Gdx.gl.glClearColor(.09f,.055f,.025f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;button(18f,"NUEVO");button(158f,if(paused)"SEGUIR" else "PAUSA");wideText(if(bingo)"BINGO CON IMÁGENES" else "LOTERÍA MEXICANA",700f,516f,Color.WHITE);text("CANTADA",115f,420f,Color.LIGHT_GRAY);box(45f,335f,140f,70f,Color(0xE0C23CFF.toInt()));wideText(called().name,115f,377f,Color(0x172033FF.toInt()),140f);marker(115f,265f);if(!bingo){box(50f,80f,160f,65f,Color(0x3D507AFF.toInt()));text("NO ESTÁ",130f,120f,Color.WHITE)};renderBoard();wideText(message,480f,45f,Color.WHITE);if(dragging)marker(drag.x,drag.y);if(tutorial)overlay("CÓMO JUGAR","Arrastra el marcador a la carta cantada.");if(paused)overlay("PAUSA","El canto está detenido.")}
 private fun renderBoard(){val s=side();val left=300f;val bottom=80f;val size=if(bingo)120f else 90f;board.forEachIndexed{i,c->val x=left+(i%s)*size;val y=bottom+(i/s)*size;box(x+3f,y+3f,size-6f,size-6f,if(i in marked)Color(0x4C9A5FFF.toInt())else Color(0xF4F0E8FF.toInt()));text(c.symbol,x+size/2,y+size*.63f,Color(0x172033FF.toInt()));smallText(c.name,x+size/2,y+size*.25f,Color(0x172033FF.toInt()),size-8f)}}
 private fun marker(x:Float,y:Float){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=Color(0xD9433AFF.toInt());shapes.circle(x,y,32f,24);shapes.end()};private fun button(x:Float,label:String){box(x,488f,130f,40f,Color(0x3D507AFF.toInt()));text(label,x+65f,516f,Color.WHITE)};private fun overlay(t:String,b:String){box(150f,120f,660f,335f,Color(0x111B30F2.toInt()));wideText(t,480f,360f,Color(0xE0C23CFF.toInt()));wideText(b,480f,270f,Color.WHITE);wideText("TOCA PARA CONTINUAR",480f,180f,Color.LIGHT_GRAY)}
 private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.roundedRect(x,y,w,h);shapes.end()};private fun text(v:String,x:Float,y:Float,c:Color){wideText(v,x,y,c,110f)};private fun wideText(v:String,x:Float,y:Float,c:Color,w:Float=440f){batch.begin();font.color=c;font.draw(batch,v,x-w/2f,y,w,Align.center,false);batch.end()};private fun smallText(v:String,x:Float,y:Float,c:Color,w:Float){val old=font.data.scaleX;font.data.setScale(.72f);batch.begin();font.color=c;font.draw(batch,v,x-w/2f,y,w,Align.center,false);batch.end();font.data.setScale(old)}
 override fun resize(w:Int,h:Int)=viewport.update(w,h,true);override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
