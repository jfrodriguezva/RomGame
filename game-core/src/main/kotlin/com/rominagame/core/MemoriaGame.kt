package com.rominagame.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlin.random.Random

class MemoriaGame(private val onComplete:(String,Int,Int)->Unit={_,_,_->}):ApplicationAdapter(){
    private enum class Screen{MENU,PLAY,RESULT}
    private val viewport=FitViewport(960f,540f);private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
    private var screen=Screen.MENU;private var mode=MemoryMode.PAIRS;private var level=1;private var generation=0
    private var deck=emptyList<Char>();private var open=emptySet<Int>();private var solved=emptySet<Int>();private var first:Int?=null;private var locked=false
    private var moves=0;private var playerScore=0;private var cpuScore=0;private var message="Elige un modo";private var preview=false
    private var missingIndex=0;private var missingAnswer='A';private var choices=emptyList<Char>();private var search:SearchDeck?=null;private var found=emptySet<Int>()
    private var paused=false;private var tutorial=false;private var resultScore=0

    override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=BitmapFont().apply{data.setScale(1.6f)};Gdx.input.inputProcessor=object:InputAdapter(){override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{tap(x,y);return true}}}
    private fun start(selected:MemoryMode=mode,showTutorial:Boolean=false){mode=selected;screen=Screen.PLAY;generation++;locked=false;paused=false;tutorial=showTutorial;open=emptySet();solved=emptySet();first=null;moves=0;playerScore=0;cpuScore=0;found=emptySet();val d=memoryDifficulty(mode,level)
        when(mode){
            MemoryMode.PAIRS,MemoryMode.TURNS->{deck=pairDeck(d.cells/2);message=if(mode==MemoryMode.TURNS)"Tu turno: encuentra una pareja" else "Encuentra todas las parejas"}
            MemoryMode.MISSING->{deck=('A'..'Z').take(d.cells).shuffled();missingIndex=Random.nextInt(deck.size);missingAnswer=deck[missingIndex];choices=(listOf(missingAnswer)+('A'..'Z').filter{it !in deck}.shuffled().take(3)).shuffled();preview=true;message="Memoriza las fichas";scheduleWhenActive(d.previewSeconds,generation){preview=false;message="Que letra falta?"}}
            MemoryMode.SEARCH->{search=searchDeck(d.cells,d.targets);message="Encuentra todas las ${search!!.target}"}
        }
    }
    private fun complete(score:Int){resultScore=score;screen=Screen.RESULT;message="Nivel superado";onComplete(mode.id,level,score)}
    private fun scheduleWhenActive(delay:Float,g:Int,action:()->Unit){Timer.schedule(object:Timer.Task(){override fun run(){if(g!=generation)return;if(paused)scheduleWhenActive(.1f,g,action)else action()}},delay)}
    private fun tap(screenX:Int,screenY:Int){val p=viewport.unproject(Vector3(screenX.toFloat(),screenY.toFloat(),0f));when(screen){Screen.MENU->tapMenu(p.x,p.y);Screen.RESULT->if(p.y<170f){if(p.x<480f)screen=Screen.MENU else{level=(level+1).coerceAtMost(20);start()}};Screen.PLAY->tapPlay(p.x,p.y)}}
    private fun tapMenu(x:Float,y:Float){MemoryMode.entries.forEachIndexed{i,m->val left=180f+(i%2)*310f;val bottom=270f-(i/2)*160f;if(x in left..left+280f&&y in bottom..bottom+125f)start(m,true)}}
    private fun tapPlay(x:Float,y:Float){if(tutorial){tutorial=false;return};if(paused){if(y>485f&&x in 468f..575f)paused=false;return};if(y>485f){when{x<115f->screen=Screen.MENU;x<230f->{level=(level-1).coerceAtLeast(1);start()};x<345f->{level=(level+1).coerceAtMost(20);start()};x<460f->start();x<575f->paused=true};return};if(locked)return
        if(mode==MemoryMode.MISSING&&!preview&&y<90f){val option=((x-250f)/120f).toInt();if(option in choices.indices){moves++;if(choices[option]==missingAnswer)complete(100-moves*3)else message="Intenta otra vez"};return}
        val index=indexAt(x,y)?:return
        when(mode){MemoryMode.PAIRS,MemoryMode.TURNS->tapPair(index);MemoryMode.SEARCH->tapSearch(index);MemoryMode.MISSING->Unit}
    }
    private fun indexAt(x:Float,y:Float):Int?{val d=memoryDifficulty(mode,level);val cellW=(700f/d.cols).coerceAtMost(150f);val cellH=(360f/d.rows).coerceAtMost(125f);val width=cellW*d.cols;val height=cellH*d.rows;val left=(960f-width)/2;val bottom=105f+(360f-height)/2;if(x !in left..left+width||y !in bottom..bottom+height)return null;val col=((x-left)/cellW).toInt().coerceAtMost(d.cols-1);val row=((y-bottom)/cellH).toInt().coerceAtMost(d.rows-1);return row*d.cols+col}
    private fun tapPair(index:Int){if(index in open||index in solved)return;open=open+index;val previous=first;if(previous==null){first=index;return};moves++;locked=true;if(deck[previous]==deck[index]){solved=solved+previous+index;open=emptySet();first=null;locked=false;if(mode==MemoryMode.TURNS)playerScore++;if(solved.size==deck.size)complete(if(mode==MemoryMode.TURNS)playerScore*20-cpuScore*5 else 100-moves*2)}else{scheduleWhenActive(.65f,generation){open=emptySet();first=null;if(mode==MemoryMode.TURNS)cpuTurn()else locked=false}}}
    private fun cpuTurn(){message="Turno de la computadora";val available=deck.indices.filter{it !in solved}.shuffled();if(available.size<2){locked=false;return};val a=available[0];val match=available.drop(1).firstOrNull{deck[it]==deck[a]};val b=if(Random.nextFloat()<(.25f+level*.025f)&&match!=null)match else available[1];open=setOf(a,b);scheduleWhenActive(.8f,generation){if(deck[a]==deck[b]){solved=solved+a+b;cpuScore++;if(solved.size==deck.size){complete(playerScore*20-cpuScore*5);return@scheduleWhenActive}};open=emptySet();message="Tu turno";locked=false}}
    private fun tapSearch(index:Int){val s=search?:return;if(index in s.targetIndexes){found=found+index;message="${found.size}/${s.targetIndexes.size} encontrados";if(found.containsAll(s.targetIndexes))complete(100-moves*2)}else{moves++;message="Busca la letra ${s.target}"}}
    override fun render(){Gdx.gl.glClearColor(.04f,.06f,.1f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;when(screen){Screen.MENU->renderMenu();Screen.PLAY->renderPlay();Screen.RESULT->renderResult()}}
    private fun renderMenu(){title("MEMORIA Y OBSERVACION",490f);text("Cuatro modos, veinte niveles cada uno",480f,450f,Color.LIGHT_GRAY,Align.center);MemoryMode.entries.forEachIndexed{i,m->val x=180f+(i%2)*310f;val y=270f-(i/2)*160f;box(x,y,280f,125f,Color(0x3154B5FF.toInt()));text(m.title,x+140f,y+72f,Color.WHITE,Align.center)}}
    private fun renderPlay(){compactButton(8f,"MODOS");compactButton(123f,"NIVEL -");compactButton(238f,"NIVEL +");compactButton(353f,"REINICIAR");compactButton(468f,if(paused)"SEGUIR" else "PAUSA");text("${mode.title} · Nivel $level",770f,516f,Color.WHITE,Align.center);text(message,480f,472f,Color(0xE0C23CFF.toInt()),Align.center)
        when(mode){MemoryMode.PAIRS,MemoryMode.TURNS->renderPairs();MemoryMode.MISSING->renderMissing();MemoryMode.SEARCH->renderSearch()};if(tutorial)overlay("CÓMO JUGAR",logicTutorial("memoria-observacion",mode),"TOCA PARA EMPEZAR");if(paused)overlay("PAUSA","La partida y sus temporizadores están detenidos.","TOCA SEGUIR")}
    private fun renderPairs(){renderGrid(deck.size){i->when{i in solved->Color(0x4C9A5FFF.toInt());i in open->Color(0xC89A45FF.toInt());else->Color(0x243E78FF.toInt())} to if(i in open||i in solved)deck[i].toString() else ""};text(if(mode==MemoryMode.TURNS)"Tu: $playerScore   CPU: $cpuScore" else "Movimientos: $moves",480f,82f,Color.WHITE,Align.center)}
    private fun renderMissing(){renderGrid(deck.size){i->Color(0x3154B5FF.toInt()) to if(preview||i!=missingIndex)deck[i].toString() else "?"};if(!preview)choices.forEachIndexed{i,c->val x=250f+i*120f;box(x,25f,100f,55f,Color(0x6B4FA3FF.toInt()));text(c.toString(),x+50f,62f,Color.WHITE,Align.center)}}
    private fun renderSearch(){val s=search?:return;renderGrid(s.symbols.size){i->(if(i in found)Color(0x4C9A5FFF.toInt())else Color(0x243E78FF.toInt())) to s.symbols[i].toString()};text("Objetivo: ${s.target}    Errores: $moves",480f,82f,Color.WHITE,Align.center)}
    private fun renderGrid(count:Int,content:(Int)->Pair<Color,String>){val d=memoryDifficulty(mode,level);val cw=(700f/d.cols).coerceAtMost(150f);val ch=(360f/d.rows).coerceAtMost(125f);val left=(960f-cw*d.cols)/2;val bottom=105f+(360f-ch*d.rows)/2;(0 until count).forEach{i->val x=left+(i%d.cols)*cw;val y=bottom+(i/d.cols)*ch;val(c,label)=content(i);box(x+5f,y+5f,cw-10f,ch-10f,c);if(label.isNotEmpty())text(label,x+cw/2,y+ch/2+12f,Color.WHITE,Align.center)}}
    private fun renderResult(){title(message,400f);text("Puntuación $resultScore · Estrellas ${starsForScore(resultScore)}/3",480f,330f,Color(0xE0C23CFF.toInt()),Align.center);box(230f,80f,220f,75f,Color(0x3D507AFF.toInt()));box(510f,80f,220f,75f,Color(0x4C9A5FFF.toInt()));text("MODOS",340f,128f,Color.WHITE,Align.center);text("SIGUIENTE",620f,128f,Color.WHITE,Align.center)}
    private fun compactButton(x:Float,label:String){box(x,488f,107f,40f,Color(0x3D507AFF.toInt()));text(label,x+53f,516f,Color.WHITE,Align.center)}
    private fun overlay(title:String,body:String,footer:String){box(150f,120f,660f,335f,Color(0x111B30F2.toInt()));text(title,480f,365f,Color(0xE0C23CFF.toInt()),Align.center);text(body,480f,275f,Color.WHITE,Align.center);text(footer,480f,185f,Color.LIGHT_GRAY,Align.center)}
    private fun box(x:Float,y:Float,w:Float,h:Float,color:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=color;shapes.rect(x,y,w,h);shapes.end()}
    private fun title(value:String,y:Float){text(value,480f,y,Color.WHITE,Align.center)}
    private fun text(value:String,x:Float,y:Float,color:Color,align:Int=Align.left){batch.begin();font.color=color;font.draw(batch,value,x-220f,y,440f,align,false);batch.end()}
    override fun resize(width:Int,height:Int)=viewport.update(width,height,true)
    override fun dispose(){generation++;shapes.dispose();batch.dispose();font.dispose()}
}
