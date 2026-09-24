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

class SolitaireGame(private val spider:Boolean,private val onComplete:(String,Int,Int)->Unit={_,_,_->}):ApplicationAdapter(){
 private val viewport=FitViewport(960f,540f);private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
 private var klondike=newKlondike();private var spiderState=newSpider();private var sourceCol:Int?=null;private var sourceIndex=0;private var wasteSource=false;private var dragging=false;private var drag=Vector2();private var tutorial=true;private var paused=false;private var message="Arrastra cartas o toca origen y destino"
 override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=GameTypography.create(20);Gdx.input.inputProcessor=object:InputAdapter(){
  override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{val v=point(x,y);if(tutorial){tutorial=false;return true};if(paused){if(v.y>485f&&v.x in 158f..288f)paused=false;return true};if(v.y>485f){if(v.x<148f)reset()else if(v.x<298f)paused=true;return true};if(stockContains(v.x,v.y)){if(spider){spiderState=dealSpider(spiderState)?:run{message="No puedes repartir con columnas vacías";spiderState}}else klondike=drawKlondike(klondike);clearSelection();return true};if(!spider&&foundationAt(v.x,v.y)>=0&&hasSelection()){moveToFoundation();return true};val hit=columnHit(v.x,v.y);if(hit!=null){if(hasSelection()){dropOnColumn(hit.first);return true};if(select(hit.first,hit.second)){dragging=true;drag.set(v.x,v.y)};return true};if(!spider&&wasteContains(v.x,v.y)){wasteSource=true;sourceCol=null;dragging=true;drag.set(v.x,v.y);return true};return true}
  override fun touchDragged(x:Int,y:Int,p:Int):Boolean{if(!dragging||paused||tutorial)return false;val v=point(x,y);drag.set(v.x,v.y);return true}
  override fun touchUp(x:Int,y:Int,p:Int,b:Int):Boolean{if(!dragging)return false;dragging=false;val v=point(x,y);val foundation=if(!spider)foundationAt(v.x,v.y)else-1;if(foundation>=0)moveToFoundation()else columnHit(v.x,v.y)?.let{dropOnColumn(it.first)};return true}
 }}
 private fun point(x:Int,y:Int)=viewport.unproject(Vector3(x.toFloat(),y.toFloat(),0f));private fun id()=if(spider)"arana-cartas" else "solitario"
 private fun reset(){klondike=newKlondike();spiderState=newSpider();clearSelection();paused=false;message="Arrastra cartas o toca origen y destino"}
 private fun clearSelection(){sourceCol=null;wasteSource=false;dragging=false}
 private fun hasSelection()=sourceCol!=null||wasteSource
 private fun stockContains(x:Float,y:Float)=x in 25f..115f&&y in 405f..475f;private fun wasteContains(x:Float,y:Float)=x in 130f..220f&&y in 405f..475f
 private fun foundationAt(x:Float,y:Float)=if(y in 405f..475f&&x in 470f..850f)(((x-470f)/95f).toInt().coerceAtMost(3))else-1
 private fun columnHit(x:Float,y:Float):Pair<Int,Int>?{val count=if(spider)10 else 7;val left=if(spider)15f else 35f;val gap=if(spider)93f else 130f;val width=if(spider)82f else 110f;if(x<left||x>left+gap*(count-1)+width||y !in 75f..395f)return null;val col=((x-left)/gap).toInt().coerceAtMost(count-1);if(x>left+col*gap+width)return null;val cards=if(spider)spiderState.columns[col]else klondike.columns[col];if(cards.isEmpty())return col to 0;val index=((390f-y)/27f).toInt().coerceIn(0,cards.lastIndex);return col to index}
 private fun select(col:Int,index:Int):Boolean{val ok=if(spider)validSpiderSequence(spiderState.columns[col],index)else validKlondikeSequence(klondike.columns[col],index);if(ok){sourceCol=col;sourceIndex=index;wasteSource=false}else message="Esa secuencia no se puede mover";return ok}
 private fun dropOnColumn(to:Int){val changed=if(spider){val from=sourceCol?:return;moveSpider(spiderState,from,sourceIndex,to)?.also{spiderState=it}!=null}else if(wasteSource){moveWasteToColumn(klondike,to)?.also{klondike=it}!=null}else{val from=sourceCol?:return;moveKlondikeColumns(klondike,from,sourceIndex,to)?.also{klondike=it}!=null};message=if(changed)"Movimiento correcto" else "Movimiento no permitido";clearSelection();checkWin()}
 private fun moveToFoundation(){if(spider)return;val from=if(wasteSource)-1 else sourceCol?:return;val next=moveKlondikeToFoundation(klondike,from);if(next!=null){klondike=next;message="Carta enviada a la fundación"}else message="La fundación requiere la siguiente carta del palo";clearSelection();checkWin()}
 private fun checkWin(){val won=if(spider)spiderState.completed==8 else klondike.foundations.all{it==13};if(won){message="¡Solitario completado!";onComplete(id(),1,1000)}}
 override fun render(){Gdx.gl.glClearColor(.025f,.16f,.09f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;button(18f,"REPARTIR");button(158f,if(paused)"SEGUIR" else "PAUSA");text(if(spider)"SOLITARIO ARAÑA · ${spiderState.completed}/8" else "SOLITARIO KLONDIKE",700f,516f,Color.WHITE);renderTop();renderColumns();text(message,480f,45f,Color.WHITE);if(dragging)dragCard();if(tutorial)overlay("CÓMO JUGAR","Arrastra cartas y secuencias a un destino válido.");if(paused)overlay("PAUSA","La partida está detenida.")}
 private fun renderTop(){cardBack(25f,405f,if(spider)spiderState.stock.size else klondike.stock.size);if(spider){text("REPARTOS ${spiderState.stock.size/10}",180f,445f,Color.WHITE)}else{klondike.waste.lastOrNull()?.let{card(130f,405f,it,true)};repeat(4){s->val x=470f+s*95f;box(x,405f,82f,70f,Color(0x174F35FF.toInt()));text("F${s+1}:${klondike.foundations[s]}",x+41f,445f,Color.WHITE)}}}
 private fun renderColumns(){val columns=if(spider)spiderState.columns else klondike.columns;val left=if(spider)15f else 35f;val gap=if(spider)93f else 130f;columns.forEachIndexed{ci,col->if(col.isEmpty())box(left+ci*gap,320f,if(spider)82f else 110f,70f,Color(0x174F35FF.toInt()));col.forEachIndexed{i,c->card(left+ci*gap,320f-i*27f,c.card,c.faceUp,if(spider)82f else 110f)}}}
 private fun dragCard(){val c=if(wasteSource)klondike.waste.lastOrNull()else sourceCol?.let{if(spider)spiderState.columns[it][sourceIndex].card else klondike.columns[it][sourceIndex].card};c?.let{card(drag.x-42f,drag.y-35f,it,true,84f)}}
 private fun card(x:Float,y:Float,c:PlayingCard,up:Boolean,w:Float=110f){box(x,y,w,70f,if(up)Color(0xF4F0E8FF.toInt())else Color(0x243E78FF.toInt()));if(up){val suit=listOf("S","H","D","C")[c.suit];val rank=when(c.rank){1->"A";11->"J";12->"Q";13->"K";else->c.rank.toString()};text("$rank$suit",x+w/2,y+43f,if(isRed(c))Color.RED else Color(0x172033FF.toInt()))}}
 private fun cardBack(x:Float,y:Float,count:Int){box(x,y,90f,70f,Color(0x243E78FF.toInt()));text(count.toString(),x+45f,y+43f,Color.WHITE)}
 private fun button(x:Float,label:String){box(x,488f,130f,40f,Color(0x3D507AFF.toInt()));text(label,x+65f,516f,Color.WHITE)};private fun overlay(t:String,b:String){box(150f,120f,660f,335f,Color(0x111B30F2.toInt()));text(t,480f,360f,Color(0xE0C23CFF.toInt()));text(b,480f,270f,Color.WHITE);text("TOCA PARA CONTINUAR",480f,180f,Color.LIGHT_GRAY)}
 private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.roundedRect(x,y,w,h);shapes.end()};private fun text(v:String,x:Float,y:Float,c:Color){batch.begin();font.color=c;font.draw(batch,v,x-220f,y,440f,Align.center,false);batch.end()}
 override fun resize(w:Int,h:Int)=viewport.update(w,h,true);override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
