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

class DeductionGame(private val domino:Boolean,private val onComplete:(String,Int,Int)->Unit={_,_,_->}):ApplicationAdapter(){
 private val viewport=FitViewport(960f,540f);private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
 private var paused=false;private var tutorial=true;private var local=false;private var message="";private var selected:Int?=null;private var dragging=false;private var drag=Vector2()
 private var hand=emptyList<DominoTile>();private var rival=emptyList<DominoTile>();private var pool=emptyList<DominoTile>();private var chain=DominoChain();private var playerTurn=true
 private var secret=guessCharacters.first();private var active=guessCharacters;private var eliminated=emptySet<Int>();private var attribute=GuessAttribute.COLOR;private var valueIndex=0;private var questions=0
 override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=GameTypography.create(18);reset();Gdx.input.inputProcessor=object:InputAdapter(){
  override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{val v=point(x,y);if(tutorial){tutorial=false;return true};if(paused){if(v.y>485f&&v.x in 158f..288f)paused=false;return true};if(v.y>485f){when{v.x<145f->reset();v.x<295f->paused=true;v.x<465f->{local=!local;reset()}};return true};return if(domino)dominoDown(v)else guessDown(v)}
  override fun touchDragged(x:Int,y:Int,p:Int):Boolean{if(!dragging||paused||tutorial)return false;val v=point(x,y);drag.set(v.x,v.y);return true}
  override fun touchUp(x:Int,y:Int,p:Int,b:Int):Boolean{if(!dragging)return false;dragging=false;val v=point(x,y);if(domino)dropDomino(v)else dropGuess(v);return true}
 }}
 private fun point(x:Int,y:Int)=viewport.unproject(Vector3(x.toFloat(),y.toFloat(),0f))
 private fun reset(){selected=null;dragging=false;paused=false;if(domino){val all=doubleSixSet().shuffled();hand=all.take(7);rival=all.drop(7).take(7);pool=all.drop(14);chain=DominoChain();playerTurn=true;message="Arrastra una ficha al centro"}else{secret=guessCharacters.random();active=guessCharacters;eliminated=emptySet();attribute=GuessAttribute.COLOR;valueIndex=0;questions=0;message="Haz preguntas y descarta candidatos"}}
 private fun dominoRects(second:Boolean=false):List<Pair<DominoTile,Float>>{val items=if(second)rival else hand;val gap=(780f/items.size.coerceAtLeast(1)).coerceAtMost(82f);return items.mapIndexed{i,t->t to (90f+i*gap)}}
 private fun dominoDown(v:Vector3):Boolean{
  if(!playerTurn&&!local)return true
  val second=!playerTurn&&local;val y=if(second)405f else 55f
  dominoRects(second).lastOrNull{v.x in it.second..it.second+62f&&v.y in y..y+82f}?.let{selected=it.first.id;dragging=true;drag.set(v.x,v.y);return true}
  if(v.x in 18f..180f&&v.y in 205f..265f){drawTile();return true}
  if(selected!=null){dropDomino(v);return true};return true
 }
 private fun dropDomino(v:Vector3){val second=!playerTurn&&local;val source=if(second)rival else hand;val tile=source.firstOrNull{it.id==selected}?:return;val end=when{chain.tiles.isEmpty()&&v.x in 250f..710f&&v.y in 190f..350f->DominoEnd.RIGHT;v.x in 190f..330f&&v.y in 190f..350f->DominoEnd.LEFT;v.x in 630f..770f&&v.y in 190f..350f->DominoEnd.RIGHT;else->{message="Suelta en IZQUIERDA o DERECHA";return}};if(!dominoCanPlay(tile,chain,end)){message="La ficha no coincide con ese extremo";return};chain=placeDomino(chain,tile,end);if(second)rival=rival.filter{it.id!=tile.id}else hand=hand.filter{it.id!=tile.id};selected=null;if((if(second)rival else hand).isEmpty()){message=if(second)"Ganó Jugador 2" else "¡Ganaste!";if(!second)onComplete("domino",1,1000);return};playerTurn=!playerTurn;message=if(playerTurn)"Turno del Jugador 1" else if(local)"Turno del Jugador 2" else "Turno de la computadora";if(!local&&!playerTurn)cpuDomino()}
 private fun drawTile(){if(chain.tiles.isNotEmpty()&&(if(playerTurn)hand else rival).any{playableDominoEnds(it,chain).isNotEmpty()}){message="Tienes una ficha jugable";return};if(pool.isNotEmpty()){val t=pool.first();pool=pool.drop(1);if(playerTurn)hand=hand+t else rival=rival+t;message="Ficha robada"}else{playerTurn=!playerTurn;message="Sin pozo: pasas turno";if(!local&&!playerTurn)cpuDomino()}}
 private fun cpuDomino(){var move=rival.firstOrNull{playableDominoEnds(it,chain).isNotEmpty()};while(move==null&&pool.isNotEmpty()){rival=rival+pool.first();pool=pool.drop(1);move=rival.last().takeIf{playableDominoEnds(it,chain).isNotEmpty()}};if(move!=null){val end=playableDominoEnds(move,chain).last();chain=placeDomino(chain,move,end);rival=rival.filter{it.id!=move.id}};if(rival.isEmpty()){message="Ganó la computadora";return};if(dominoBlocked(listOf(hand,rival),pool,chain)){finishBlocked();return};playerTurn=true;message="Tu turno"}
 private fun finishBlocked(){val mine=hand.sumOf{it.a+it.b};val other=rival.sumOf{it.a+it.b};message=when{mine<other->"Ganó Jugador 1 por puntos";mine>other->if(local)"Ganó Jugador 2 por puntos" else "Ganó la computadora por puntos";else->"Empate"}}
 private fun guessValues()=when(attribute){GuessAttribute.COLOR->listOf("café","gris","blanco","verde","rosa","negro","naranja","amarillo");GuessAttribute.SIZE->listOf("chico","grande");GuessAttribute.HABITAT->listOf("casa","campo","agua","bosque")}
 private fun guessDown(v:Vector3):Boolean{when{v.y in 405f..455f&&v.x in 40f..205f->{attribute=GuessAttribute.entries[(attribute.ordinal+1)%3];valueIndex=0};v.y in 405f..455f&&v.x in 220f..385f->{valueIndex=(valueIndex+1)%guessValues().size};v.y in 405f..455f&&v.x in 400f..575f->ask();else->cardAt(v.x,v.y)?.let{selected=it.id;dragging=true;drag.set(v.x,v.y)}};return true}
 private fun ask(){val value=guessValues()[valueIndex];val yes=answerQuestion(secret,attribute,value);eliminated=eliminated+eliminateByAnswer(active.filter{it.id !in eliminated},attribute,value,yes);questions++;message="¿${attributeLabel()} es $value? ${if(yes)"SÍ" else "NO"}"}
 private fun cardAt(x:Float,y:Float):GuessCharacter?{if(x !in 170f..790f||y !in 90f..370f)return null;val c=((x-170f)/105f).toInt().coerceIn(0,5);val r=((y-90f)/140f).toInt().coerceIn(0,1);return active.getOrNull(r*6+c)?.takeIf{it.id !in eliminated}}
 private fun dropGuess(v:Vector3){val card=active.firstOrNull{it.id==selected}?:return;when{v.x in 15f..145f&&v.y in 90f..340f->{eliminated=eliminated+card.id;message="${card.name} descartado"};v.x in 815f..945f&&v.y in 90f..340f->{if(card.id==secret.id){message="¡Correcto! Era ${card.name}";onComplete("adivinaquien",1,1000)}else{eliminated=eliminated+card.id;message="No era ${card.name}"}};else->message="Suelta en DESCARTAR o ADIVINAR"};selected=null}
 private fun attributeLabel()=when(attribute){GuessAttribute.COLOR->"color";GuessAttribute.SIZE->"tamaño";GuessAttribute.HABITAT->"hábitat"}
 override fun render(){Gdx.gl.glClearColor(.055f,.075f,.12f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;button(15f,"NUEVO",130f);button(155f,if(paused)"SEGUIR" else "PAUSA",130f);button(295f,if(local)"2 JUGADORES" else "VS CPU",165f);wideText(if(domino)"DOMINÓ DOBLE 6" else "ADIVINA QUIÉN",725f,516f,Color.WHITE,450f);if(domino)renderDomino()else renderGuess();wideText(message,480f,30f,Color.WHITE,850f);if(dragging)if(domino)drawDomino(hand.plus(rival).firstOrNull{it.id==selected}?:return,drag.x-31f,drag.y-41f)else active.firstOrNull{it.id==selected}?.let{drawCard(it,drag.x-45f,drag.y-55f,false)};if(tutorial)overlay("CÓMO JUGAR",if(domino)"Arrastra tus fichas al extremo que coincida. Roba sólo si no puedes jugar." else "Pregunta por atributos. Arrastra cartas para descartarlas o adivinarlas.");if(paused)overlay("PAUSA","La partida está detenida.")}
 private fun renderDomino(){box(190f,190f,140f,160f,Color(0x355C7DFF.toInt()));box(630f,190f,140f,160f,Color(0x355C7DFF.toInt()));text("IZQUIERDA",260f,335f);text("DERECHA",700f,335f);box(18f,205f,162f,60f,Color(0x8A5A2BFF.toInt()));text("ROBAR (${pool.size})",99f,243f);dominoRects(true).forEach{(t,x)->if(local)drawDomino(t,x,405f)else back(x,405f)};dominoRects().forEach{(t,x)->drawDomino(t,x,55f)};val visible=chain.tiles.takeLast(7);visible.forEachIndexed{i,t->drawDomino(t,230f+i*72f,235f)};wideText(if(playerTurn)"Jugador 1" else if(local)"Jugador 2" else "Computadora",480f,385f,Color(0xE0C23CFF.toInt()))}
 private fun drawDomino(t:DominoTile,x:Float,y:Float){box(x,y,62f,82f,Color(0xF7F1E3FF.toInt()));line(x+31f,y,x+31f,y+82f);wideText(t.a.toString(),x+15f,y+48f,Color(0x172033FF.toInt()),30f);wideText(t.b.toString(),x+47f,y+48f,Color(0x172033FF.toInt()),30f)}
 private fun back(x:Float,y:Float)=box(x,y,62f,82f,Color(0x3E5C78FF.toInt()))
 private fun renderGuess(){button(40f,attributeLabel().uppercase(),165f,405f);button(220f,guessValues()[valueIndex].uppercase(),165f,405f);button(400f,"PREGUNTAR",175f,405f);box(15f,90f,130f,250f,Color(0x7A3343FF.toInt()));wideText("DESCARTAR",80f,225f,Color.WHITE,120f);box(815f,90f,130f,250f,Color(0x367A52FF.toInt()));wideText("ADIVINAR",880f,225f,Color.WHITE,120f);active.forEachIndexed{i,c->drawCard(c,170f+(i%6)*105f,90f+(i/6)*140f,c.id in eliminated)};wideText("Preguntas: $questions",720f,438f,Color.LIGHT_GRAY,220f)}
 private fun drawCard(c:GuessCharacter,x:Float,y:Float,off:Boolean){box(x,y,90f,115f,if(off)Color(0x2D3240FF.toInt())else Color(0xE9D8A6FF.toInt()));wideText(if(off)"X" else c.symbol,x+45f,y+78f,if(off)Color.GRAY else Color(0x172033FF.toInt()),80f);wideText(c.name,x+45f,y+28f,if(off)Color.GRAY else Color(0x172033FF.toInt()),88f)}
 private fun button(x:Float,label:String,w:Float,y:Float=488f){box(x,y,w,40f,Color(0x3D507AFF.toInt()));wideText(label,x+w/2f,y+28f,Color.WHITE,w-8f)}
 private fun overlay(t:String,b:String){box(145f,115f,670f,340f,Color(0x111B30F2.toInt()));wideText(t,480f,360f,Color(0xE0C23CFF.toInt()));wideText(b,480f,270f,Color.WHITE,610f);wideText("TOCA PARA CONTINUAR",480f,175f,Color.LIGHT_GRAY)}
 private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.roundedRect(x,y,w,h);shapes.end()};private fun line(x1:Float,y1:Float,x2:Float,y2:Float){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=Color.GRAY;shapes.rectLine(x1,y1,x2,y2,2f);shapes.end()};private fun text(v:String,x:Float,y:Float)=wideText(v,x,y,Color.WHITE,130f);private fun wideText(v:String,x:Float,y:Float,c:Color,w:Float=440f){batch.begin();font.color=c;font.draw(batch,v,x-w/2f,y,w,Align.center,true);batch.end()}
 override fun resize(w:Int,h:Int)=viewport.update(w,h,true);override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
