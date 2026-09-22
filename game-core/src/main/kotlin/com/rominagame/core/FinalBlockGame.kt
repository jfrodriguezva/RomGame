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

class FinalBlockGame(familyId:String,private val onComplete:(String,Int,Int)->Unit={_,_,_->}):ApplicationAdapter(){
    private enum class Screen{MODES,PLAY,RESULT}
    private val family=finalFamily(familyId);private val viewport=FitViewport(960f,540f)
    private lateinit var shapes:ShapeRenderer;private lateinit var batch:SpriteBatch;private lateinit var font:BitmapFont
    private var screen=Screen.MODES;private var mode=0;private var level=1;private var score=0;private var progress=0;private var feedback="Elige una modalidad"
    private var target=Vector2(480f,280f);private var drawing=false;private val strokes=mutableListOf<Vector2>();private var colored=mutableSetOf<Int>();private var dragging=false
    private var patternGoal=List(4){it};private var pattern=List(4){(it+1)%4};private var mazePos=0 to 0
    private var paused=false;private var tutorial=false
    override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=BitmapFont().apply{data.setScale(1.6f)};Gdx.input.inputProcessor=object:InputAdapter(){
        override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{val v=point(x,y);tap(v.x,v.y);return true}
        override fun touchDragged(x:Int,y:Int,p:Int):Boolean{if(paused||tutorial)return false;val v=point(x,y);if(mechanic()==FinalMechanic.DRAW&&screen==Screen.PLAY&&drawing){strokes+=Vector2(v.x,v.y);if(strokes.size>=45)finish();return true};return false}
        override fun touchUp(x:Int,y:Int,p:Int,b:Int):Boolean{if(paused||tutorial){drawing=false;dragging=false;return false};drawing=false;if(!dragging)return false;val v=point(x,y);dragging=false;if(mechanic()==FinalMechanic.BASKET){if(v.x in 650f..830f&&v.y in 250f..370f)finish()else feedback="Lleva la pelota dentro de la canasta"}else if(v.x in 250f..710f&&v.y in 160f..390f){progress++;if(progress>=4)finish()else feedback="Pieza colocada"};return true}
    }}
    private fun point(x:Int,y:Int)=viewport.unproject(Vector3(x.toFloat(),y.toFloat(),0f));private fun mechanic()=family.modes[mode].mechanic
    private fun start(selected:Int=mode,showTutorial:Boolean=false){mode=selected;screen=Screen.PLAY;score=0;progress=0;strokes.clear();colored.clear();drawing=false;dragging=false;paused=false;tutorial=showTutorial;target=Vector2(300f+Random.nextFloat()*360f,190f+Random.nextFloat()*190f);patternGoal=List(4){(level+it)%4};pattern=List(4){(patternGoal[it]+1)%4};mazePos=0 to 0;feedback=instruction()}
    private fun instruction()=when(mechanic()){FinalMechanic.DRAW->"Dibuja sobre el lienzo";FinalMechanic.COLOR->"Colorea todas las zonas";FinalMechanic.COLLAGE->"Arrastra piezas al lienzo";FinalMechanic.MUSIC->"Toca la secuencia de notas";FinalMechanic.PATTERN->"Copia el patrón";FinalMechanic.TARGET->"Toca únicamente el objetivo";FinalMechanic.BASKET->"Arrastra la pelota a la canasta";FinalMechanic.MAZE->"Avanza por el camino hasta la meta"}
    private fun finish(){score=1000-level*5;screen=Screen.RESULT;onComplete(family.id,level,score)}
    private fun tap(x:Float,y:Float){when(screen){Screen.MODES->family.modes.indices.forEach{i->val bx=90f+(i%3)*270f;val by=350f-(i/3)*115f;if(x in bx..bx+235f&&y in by..by+85f)start(i,true)};Screen.RESULT->if(y<175f){if(x<480f)screen=Screen.MODES else{level=(level+1).coerceAtMost(20);start()}};Screen.PLAY->{if(tutorial){tutorial=false;return};if(paused){if(y>485f&&x in 468f..575f)paused=false;return};if(y>485f){when{x<115f->screen=Screen.MODES;x<230f->{level=(level-1).coerceAtLeast(1);start()};x<345f->{level=(level+1).coerceAtMost(20);start()};x<460f->start();x<575f->{drawing=false;dragging=false;paused=true}};return};when(mechanic()){
        FinalMechanic.DRAW->{if(x in 120f..840f&&y in 100f..410f){drawing=true;strokes+=Vector2(x,y)}}
        FinalMechanic.COLOR->{val i=colorIndex(x,y);if(i>=0){colored+=i;if(colored.size==6)finish()}}
        FinalMechanic.COLLAGE->if(y in 90f..165f)dragging=true
        FinalMechanic.MUSIC->{if(y in 120f..360f){progress++;if(progress>=musicNotes(level))finish()else feedback="$progress/${musicNotes(level)} notas"}}
        FinalMechanic.PATTERN->{val i=patternIndex(x,y);if(i>=0){pattern=pattern.toMutableList().also{it[i]=(it[i]+1)%4};if(pattern==patternGoal)finish()}}
        FinalMechanic.TARGET->{if(targetContains(target.x,target.y,x,y)){progress++;if(progress>=targetHits(level))finish()else{target.set(180f+Random.nextFloat()*600f,150f+Random.nextFloat()*260f);feedback="$progress/${targetHits(level)}"}}else feedback="Toca el objetivo, no el fondo"}
        FinalMechanic.BASKET->if(x in 150f..300f&&y in 130f..260f)dragging=true
        FinalMechanic.MAZE->moveMaze(x,y)
    }}}}
    private fun colorIndex(x:Float,y:Float):Int{if(x !in 210f..750f||y !in 145f..385f)return-1;return((y-145f)/120f).toInt()*3+((x-210f)/180f).toInt()}
    private fun patternIndex(x:Float,y:Float):Int{if(x !in 560f..760f||y !in 185f..385f)return-1;return((385f-y)/100f).toInt().coerceAtMost(1)*2+((x-560f)/100f).toInt().coerceAtMost(1)}
    private fun moveMaze(x:Float,y:Float){val size=mazeSize(level);val cell=360f/size;val left=300f;val bottom=105f;if(x !in left..left+360f||y !in bottom..bottom+360f)return;val next=((x-left)/cell).toInt().coerceAtMost(size-1) to ((y-bottom)/cell).toInt().coerceAtMost(size-1);val distance=kotlin.math.abs(next.first-mazePos.first)+kotlin.math.abs(next.second-mazePos.second);if(distance==1&&next in mazePath(size)){mazePos=next;if(next==(size-1 to size-1))finish()}else feedback="Ese paso está bloqueado"}
    override fun render(){Gdx.gl.glClearColor(.035f,.055f,.09f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;when(screen){Screen.MODES->renderModes();Screen.PLAY->renderPlay();Screen.RESULT->renderResult()}}
    private fun renderModes(){text(family.title.uppercase(),480f,500f,Color.WHITE);text("Modalidades · 20 niveles",480f,462f,Color.LIGHT_GRAY);family.modes.forEachIndexed{i,m->val x=90f+(i%3)*270f;val y=350f-(i/3)*115f;box(x,y,235f,85f,Color(0x3154B5FF.toInt()));text(m.title,x+117f,y+53f,Color.WHITE)}}
    private fun renderPlay(){compactButton(8f,"MODOS");compactButton(123f,"NIVEL -");compactButton(238f,"NIVEL +");compactButton(353f,"REINICIAR");compactButton(468f,if(paused)"SEGUIR" else "PAUSA");text("${family.modes[mode].title} · Nivel $level",770f,516f,Color.WHITE);when(mechanic()){FinalMechanic.DRAW->renderDraw();FinalMechanic.COLOR->renderColor();FinalMechanic.COLLAGE->renderCollage();FinalMechanic.MUSIC->renderMusic();FinalMechanic.PATTERN->renderPattern();FinalMechanic.TARGET->renderTarget();FinalMechanic.BASKET->renderBasket();FinalMechanic.MAZE->renderMaze()};text(feedback,480f,70f,Color.LIGHT_GRAY);if(tutorial)overlay("CÓMO JUGAR",finalTutorial(family.id,mode),"TOCA PARA EMPEZAR");if(paused)overlay("PAUSA","La actividad está detenida.","TOCA SEGUIR")}
    private fun renderDraw(){box(120f,100f,720f,320f,Color(0xF4F0E8FF.toInt()));if(strokes.size>1){shapes.begin(ShapeRenderer.ShapeType.Line);shapes.color=Color(0x3154B5FF.toInt());strokes.zipWithNext().forEach{(a,b)->shapes.line(a,b)};shapes.end()}}
    private fun renderColor(){repeat(6){i->val x=210f+(i%3)*180f;val y=145f+(i/3)*120f;box(x+5f,y+5f,170f,110f,if(i in colored)palette(i)else Color(0x30384AFF.toInt()))}}
    private fun renderCollage(){box(250f,160f,460f,230f,Color(0xF4F0E8FF.toInt()));repeat(4-progress){i->box(220f+i*140f,90f,110f,65f,if(dragging&&i==0)Color(0xE0C23CFF.toInt())else palette(i))}}
    private fun renderMusic(){repeat(8){i->val x=120f+i*90f;box(x,120f,80f,240f,palette(i));text((i+1).toString(),x+40f,250f,Color.WHITE)}}
    private fun renderPattern(){text("PATRÓN",320f,410f,Color.LIGHT_GRAY);text("TU COPIA",660f,410f,Color.LIGHT_GRAY);grid(220f,185f,patternGoal);grid(560f,185f,pattern)}
    private fun grid(left:Float,bottom:Float,values:List<Int>){values.forEachIndexed{i,v->box(left+(i%2)*100f+4f,bottom+(1-i/2)*100f+4f,92f,92f,palette(v))}}
    private fun renderTarget(){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=if(mode==2)Color(0xE0669CFF.toInt())else Color(0xE0C23CFF.toInt());shapes.circle(target.x,target.y,55f,32);shapes.end();text("$progress/${targetHits(level)}",480f,110f,Color.WHITE)}
    private fun renderBasket(){box(650f,250f,180f,120f,Color(0xE08A3AFF.toInt()));shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=if(dragging)Color(0xE0C23CFF.toInt())else Color(0xD9433AFF.toInt());shapes.circle(220f,190f,48f,32);shapes.end()}
    private fun renderMaze(){val size=mazeSize(level);val cell=360f/size;val path=mazePath(size);repeat(size){row->repeat(size){col->val open=col to row in path;box(300f+col*cell+2f,105f+row*cell+2f,cell-4f,cell-4f,if(open)Color(0x243E78FF.toInt())else Color(0x111622FF.toInt()))}};shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=Color(0xE0C23CFF.toInt());shapes.circle(300f+(mazePos.first+.5f)*cell,105f+(mazePos.second+.5f)*cell,cell*.25f,20);shapes.end()}
    private fun palette(i:Int)=listOf(Color(0xD9433AFF.toInt()),Color(0x3E7AA3FF.toInt()),Color(0xE0C23CFF.toInt()),Color(0x4C7A3AFF.toInt()),Color(0x7A4FA3FF.toInt()),Color(0xE08A3AFF.toInt()),Color(0x4C9A5FFF.toInt()),Color(0xE0669CFF.toInt()))[i%8]
    private fun renderResult(){text("NIVEL SUPERADO",480f,405f,Color.WHITE);text("Puntuación $score · Estrellas ${starsForScore(score)}/3",480f,330f,Color(0xE0C23CFF.toInt()));box(230f,80f,220f,75f,Color(0x3D507AFF.toInt()));box(510f,80f,220f,75f,Color(0x4C9A5FFF.toInt()));text("MODOS",340f,128f,Color.WHITE);text("SIGUIENTE",620f,128f,Color.WHITE)}
    private fun compactButton(x:Float,label:String){box(x,488f,107f,40f,Color(0x3D507AFF.toInt()));text(label,x+53f,516f,Color.WHITE)}
    private fun overlay(title:String,body:String,footer:String){box(150f,120f,660f,335f,Color(0x111B30F2.toInt()));text(title,480f,365f,Color(0xE0C23CFF.toInt()));text(body,480f,275f,Color.WHITE);text(footer,480f,185f,Color.LIGHT_GRAY)}
    private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.rect(x,y,w,h);shapes.end()};private fun text(v:String,x:Float,y:Float,c:Color){batch.begin();font.color=c;font.draw(batch,v,x-220f,y,440f,Align.center,false);batch.end()}
    override fun resize(w:Int,h:Int)=viewport.update(w,h,true);override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
