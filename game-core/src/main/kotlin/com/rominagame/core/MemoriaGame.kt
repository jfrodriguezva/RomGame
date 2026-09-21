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
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.FitViewport

class MemoriaGame(private val onComplete:(Int)->Unit={}):ApplicationAdapter(){
    private val viewport=FitViewport(960f,540f)
    private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
    private var cards=emptyList<Card>();private var first:Int?=null;private var locked=false;private var moves=0
    data class Card(val symbol:Char,val open:Boolean=false,val solved:Boolean=false)

    override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=BitmapFont().apply{data.setScale(2.4f)};reset();Gdx.input.inputProcessor=object:InputAdapter(){override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{tap(x,y);return true}}}
    private fun reset(){cards=(('A'..'F').flatMap{listOf(it,it)}).shuffled().map{Card(it)};first=null;locked=false;moves=0}
    private fun tap(screenX:Int,screenY:Int){if(locked)return;val p=viewport.unproject(Vector3(screenX.toFloat(),screenY.toFloat(),0f));val col=((p.x-170f)/160f).toInt();val row=((p.y-55f)/145f).toInt();if(col !in 0..3||row !in 0..2)return;val index=row*4+col;val card=cards[index];if(card.open||card.solved)return;cards=cards.toMutableList().also{it[index]=card.copy(open=true)};val previous=first;if(previous==null){first=index;return};moves++;locked=true;if(cards[previous].symbol==cards[index].symbol){cards=cards.toMutableList().also{it[previous]=it[previous].copy(solved=true);it[index]=it[index].copy(solved=true)};first=null;locked=false;if(cards.all{it.solved})Timer.schedule(object:Timer.Task(){override fun run(){onComplete(moves)}},.5f)}else Timer.schedule(object:Timer.Task(){override fun run(){cards=cards.toMutableList().also{it[previous]=it[previous].copy(open=false);it[index]=it[index].copy(open=false)};first=null;locked=false}},.75f)}
    override fun render(){Gdx.gl.glClearColor(.055f,.075f,.12f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;shapes.begin(ShapeRenderer.ShapeType.Filled);cards.forEachIndexed{i,c->val x=180f+(i%4)*160f;val y=65f+(i/4)*145f;shapes.color=when{c.solved->Color(0x6FBE73FF.toInt());c.open->Color(0xE0B45CFF.toInt());else->Color(0x3154B5FF.toInt())};shapes.rect(x,y,135f,115f)};shapes.end();batch.begin();font.color=Color.WHITE;font.draw(batch,"MEMORIA  Movimientos: $moves",260f,520f);cards.forEachIndexed{i,c->if(c.open||c.solved)font.draw(batch,c.symbol.toString(),225f+(i%4)*160f,138f+(i/4)*145f)};batch.end()}
    override fun resize(width:Int,height:Int)=viewport.update(width,height,true)
    override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
