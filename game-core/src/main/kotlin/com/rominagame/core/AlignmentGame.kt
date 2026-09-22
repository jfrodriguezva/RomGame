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

class AlignmentGame(private val gameId:String,private val onComplete:(String,Int,Int)->Unit={_,_,_->}):ApplicationAdapter(){
    private val viewport=FitViewport(960f,540f);private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
    private var dragging=false;private var selected=false;private var drag=Vector2();private var choiceIndex=0;private var message="Arrastra una ficha o toca origen y destino";private var finished=false
    private var tic=MutableList<Char?>(9){null};private var connect=MutableList<Char?>(42){null};private var wins=0;private var losses=0;private var draws=0
    override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=BitmapFont().apply{data.setScale(1.6f)};Gdx.input.inputProcessor=object:InputAdapter(){
        override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{val v=point(x,y);if(v.y>485f&&v.x<148f){reset();return true};if(sourceContains(v.x,v.y)){dragging=true;selected=true;drag.set(v.x,v.y);if(gameId=="rps")choiceIndex=((v.x-150f)/125f).toInt().coerceIn(0,2);return true};if(selected)place(v.x,v.y);return true}
        override fun touchDragged(x:Int,y:Int,p:Int):Boolean{if(!dragging)return false;val v=point(x,y);drag.set(v.x,v.y);return true}
        override fun touchUp(x:Int,y:Int,p:Int,b:Int):Boolean{if(!dragging)return false;dragging=false;val v=point(x,y);place(v.x,v.y);return true}
    }}
    private fun point(x:Int,y:Int)=viewport.unproject(Vector3(x.toFloat(),y.toFloat(),0f))
    private fun sourceContains(x:Float,y:Float)=when(gameId){"rps"->y in 80f..190f&&x in 150f..520f;else->x in 60f..190f&&y in 185f..335f}
    private fun reset(){tic=MutableList(9){null};connect=MutableList(42){null};selected=false;dragging=false;finished=false;message="Arrastra una ficha o toca origen y destino"}
    private fun place(x:Float,y:Float){if(finished){if(y<100f)reset();return};when(gameId){
        "gato"->{val left=315f;val bottom=105f;val cell=110f;if(x !in left..left+330f||y !in bottom..bottom+330f)return;val c=((x-left)/cell).toInt().coerceAtMost(2);val r=((y-bottom)/cell).toInt().coerceAtMost(2);val i=r*3+c;if(tic[i]!=null){message="Esa casilla ya está ocupada";return};tic[i]='X';selected=false;resolveTic()}
        "conecta4"->{val left=285f;val width=420f;if(x !in left..left+width||y !in 85f..445f)return;val column=((x-left)/(width/7f)).toInt().coerceAtMost(6);val next=dropConnectToken(connect,column,'X')?:run{message="Esa columna está llena";return};connect=next.toMutableList();selected=false;resolveConnect()}
        "rps"->{if(y !in 245f..430f)return;val choice=DuelChoice.entries[choiceIndex];val cpu=DuelChoice.entries.random();when(duelOutcome(choice,cpu)){DuelOutcome.WIN->{wins++;message="Ganaste: ${label(choice)} vence a ${label(cpu)}"};DuelOutcome.LOSE->{losses++;message="CPU: ${label(cpu)} vence a ${label(choice)}"};DuelOutcome.DRAW->{draws++;message="Empate: ambos eligieron ${label(choice)}"}};selected=false;if(wins>=3){finished=true;onComplete(gameId,1,wins*300-losses*50)}}
    }}
    private fun resolveTic(){val winner=ticTacToeWinner(tic);if(winner=='X'){finishWin();return};if(tic.none{it==null}){finishDraw();return};bestTicTacToeMove(tic)?.let{tic[it]='O'};when{ticTacToeWinner(tic)=='O'->finishLoss();tic.none{it==null}->finishDraw();else->message="Tu turno: coloca X"}}
    private fun resolveConnect(){if(connectWinner(connect)=='X'){finishWin();return};if(connect.none{it==null}){finishDraw();return};bestConnectMove(connect)?.let{connect=dropConnectToken(connect,it,'O')!!.toMutableList()};when{connectWinner(connect)=='O'->finishLoss();connect.none{it==null}->finishDraw();else->message="Tu turno: coloca la ficha amarilla"}}
    private fun finishWin(){wins++;finished=true;message="¡Ganaste! Toca REINICIAR";onComplete(gameId,1,1000)}
    private fun finishLoss(){losses++;finished=true;message="Ganó la computadora. Toca REINICIAR"}
    private fun finishDraw(){draws++;finished=true;message="Empate. Toca REINICIAR"}
    private fun label(c:DuelChoice)=when(c){DuelChoice.ROCK->"piedra";DuelChoice.PAPER->"papel";DuelChoice.SCISSORS->"tijera"}
    override fun render(){Gdx.gl.glClearColor(.035f,.055f,.09f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;button(18f,"REINICIAR");text(title(),480f,510f,Color.WHITE);when(gameId){"gato"->renderTic();"conecta4"->renderConnect();else->renderRps()};text(message,480f,55f,Color.LIGHT_GRAY);if(dragging)token(drag.x,drag.y,if(gameId=="gato")Color(0xE0C23CFF.toInt())else Color(0xE0C23CFF.toInt()))}
    private fun title()=when(gameId){"gato"->"GATO · TRES EN LÍNEA";"conecta4"->"CUATRO EN LÍNEA · 7 × 6";else->"PIEDRA · PAPEL · TIJERA   $wins-$losses-$draws"}
    private fun renderTic(){text("TU FICHA",125f,370f,Color.LIGHT_GRAY);token(125f,260f,Color(0xE0C23CFF.toInt()),"X");val l=315f;val b=105f;repeat(9){i->val x=l+(i%3)*110f;val y=b+(i/3)*110f;box(x+3f,y+3f,104f,104f,Color(0x182847FF.toInt()));tic[i]?.let{token(x+55f,y+55f,if(it=='X')Color(0xE0C23CFF.toInt())else Color(0xD9433AFF.toInt()),it.toString())}};if(finished)button(390f,"REINICIAR")}
    private fun renderConnect(){text("TU FICHA",125f,370f,Color.LIGHT_GRAY);token(125f,260f,Color(0xE0C23CFF.toInt()));val l=285f;val b=85f;val cw=60f;val ch=60f;box(l,b,420f,360f,Color(0x234D9AFF.toInt()));repeat(42){i->val c=i%7;val r=i/7;val value=connect[i];token(l+(c+.5f)*cw,b+(r+.5f)*ch,when(value){'X'->Color(0xE0C23CFF.toInt());'O'->Color(0xD9433AFF.toInt());else->Color(0x07101FFF.toInt())},"",22f)};if(finished)button(390f,"REINICIAR")}
    private fun renderRps(){val labels=listOf("PIEDRA","PAPEL","TIJERA");repeat(3){i->val x=150f+i*125f;box(x,80f,110f,110f,if(selected&&drag.x in x..x+110f)Color(0x4C7A3AFF.toInt())else Color(0x243E78FF.toInt()));text(labels[i],x+55f,142f,Color.WHITE)};box(560f,245f,260f,185f,Color(0x182847FF.toInt()));text("SUELTA AQUÍ",690f,345f,Color.WHITE);if(finished)button(390f,"REINICIAR")}
    private fun button(x:Float,label:String){box(x,488f,130f,40f,Color(0x3D507AFF.toInt()));text(label,x+65f,516f,Color.WHITE)}
    private fun token(x:Float,y:Float,color:Color,label:String="",radius:Float=45f){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=color;shapes.circle(x,y,radius,32);shapes.end();if(label.isNotEmpty())text(label,x,y+11f,Color(0x101522FF.toInt()))}
    private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.rect(x,y,w,h);shapes.end()}
    private fun text(v:String,x:Float,y:Float,c:Color){batch.begin();font.color=c;font.draw(batch,v,x-220f,y,440f,Align.center,false);batch.end()}
    override fun resize(w:Int,h:Int)=viewport.update(w,h,true);override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
