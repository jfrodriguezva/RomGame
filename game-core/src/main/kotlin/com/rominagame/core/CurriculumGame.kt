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
import com.badlogic.gdx.utils.viewport.FitViewport

class CurriculumGame(familyId: String, private val onComplete: (String, Int, Int) -> Unit = { _, _, _ -> }) : ApplicationAdapter() {
    private enum class Screen { MODES, PLAY, RESULT }
    private val family = curriculumFamily(familyId); private val viewport = FitViewport(960f, 540f)
    private lateinit var shapes: ShapeRenderer; private lateinit var batch: SpriteBatch; private lateinit var font: BitmapFont
    private var screen=Screen.MODES; private var mode=0; private var level=1; private var score=0; private var step=0
    private var challenge=curriculumChallenge(family.id,0,1); private var draggingSort=false; private var draggingOrder=-1
    private var expected=emptyList<String>(); private var ordered=emptyList<String>(); private var counted=emptySet<Int>(); private var feedback="Elige una modalidad"
    private var paused=false;private var tutorial=false

    override fun create(){shapes=ShapeRenderer();batch=SpriteBatch();font=BitmapFont().apply{data.setScale(1.55f)};Gdx.input.inputProcessor=object:InputAdapter(){
        override fun touchDown(x:Int,y:Int,p:Int,b:Int):Boolean{val v=point(x,y);tap(v.x,v.y);return true}
        override fun touchUp(x:Int,y:Int,p:Int,b:Int):Boolean{if(paused||tutorial)return false;val v=point(x,y);when{draggingSort->{draggingSort=false;dropSort(v.x,v.y)};draggingOrder>=0->{dropOrder(v.x);draggingOrder=-1};else->return false};return true}
    }}
    private fun point(x:Int,y:Int)=viewport.unproject(Vector3(x.toFloat(),y.toFloat(),0f));private fun mechanic()=family.modes[mode].mechanic
    private fun start(selected:Int=mode,showTutorial:Boolean=false){mode=selected;screen=Screen.PLAY;score=0;step=0;counted=emptySet();paused=false;tutorial=showTutorial;challenge=curriculumChallenge(family.id,mode,level);feedback=when(mechanic()){CurriculumMechanic.QUIZ->"Selecciona la respuesta";CurriculumMechanic.SORT->"Arrastra cada elemento";CurriculumMechanic.ORDER->"Ordena de izquierda a derecha";CurriculumMechanic.COUNT->"Toca cada elemento para contar"};if(mechanic()==CurriculumMechanic.ORDER){expected=orderItems(family.id,mode,level);ordered=expected.reversed()}}
    private fun finish(){score=1000-level*5;screen=Screen.RESULT;onComplete(family.id,level,score)}
    private fun tap(x:Float,y:Float){when(screen){
        Screen.MODES->family.modes.indices.forEach{i->val bx=60f+(i%3)*300f;val by=365f-(i/3)*92f;if(x in bx..bx+270f&&y in by..by+68f)start(i,true)}
        Screen.RESULT->if(y<175f){if(x<480f)screen=Screen.MODES else{level=(level+1).coerceAtMost(20);start()}}
        Screen.PLAY->{if(tutorial){tutorial=false;return};if(paused){if(y>485f&&x in 468f..575f)paused=false;return};if(y>485f){when{x<115f->screen=Screen.MODES;x<230f->{level=(level-1).coerceAtLeast(1);start()};x<345f->{level=(level+1).coerceAtMost(20);start()};x<460f->start();x<575f->paused=true};return};when(mechanic()){
            CurriculumMechanic.QUIZ->{val i=optionAt(x,y);if(i==challenge.answer)finish()else if(i>=0)feedback="Intenta otra vez"}
            CurriculumMechanic.SORT->if(x in 340f..620f&&y in 250f..390f)draggingSort=true
            CurriculumMechanic.ORDER->if(y in 190f..330f)draggingOrder=orderIndex(x)
            CurriculumMechanic.COUNT->{val i=countIndex(x,y);if(i>=0){counted=counted+i;if(counted.size>=countTarget(level))finish()else feedback="${counted.size} contados"}}
        }}
    }}
    private fun optionAt(x:Float,y:Float):Int{if(x !in 170f..790f||y !in 115f..375f)return-1;return((375f-y)/130f).toInt()*2+((x-170f)/320f).toInt()}
    private fun dropSort(x:Float,y:Float){if(y !in 105f..225f)return;val item=sortItem(family.id,mode,step);val destination=if(x<480f)0 else 1;if(destination==item.destination){step++;if(step>=4)finish()else feedback="¡Correcto! Faltan ${4-step}"}else feedback="Prueba en el otro grupo"}
    private fun orderIndex(x:Float):Int{val w=780f/ordered.size;return((x-90f)/w).toInt().takeIf{it in ordered.indices}?:-1}
    private fun dropOrder(x:Float){val target=orderIndex(x);if(target<0||draggingOrder !in ordered.indices)return;val list=ordered.toMutableList();val item=list.removeAt(draggingOrder);list.add(target,item);ordered=list;if(ordered==expected)finish()else feedback="Sigue ordenando"}
    private fun countIndex(x:Float,y:Float):Int{val count=countTarget(level);val cols=6;val w=105f;val h=100f;val left=165f;val bottom=145f;if(x !in left..left+cols*w||y !in bottom..bottom+2*h)return-1;val i=((y-bottom)/h).toInt()*cols+((x-left)/w).toInt();return i.takeIf{it in 0 until count}?:-1}
    override fun render(){Gdx.gl.glClearColor(.035f,.055f,.09f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined;when(screen){Screen.MODES->renderModes();Screen.PLAY->renderPlay();Screen.RESULT->renderResult()}}
    private fun renderModes(){text(family.title.uppercase(),480f,505f,Color.WHITE);text("Modalidades · 20 niveles",480f,468f,Color.LIGHT_GRAY);family.modes.forEachIndexed{i,m->val x=60f+(i%3)*300f;val y=365f-(i/3)*92f;box(x,y,270f,68f,Color(0x3154B5FF.toInt()));text(m.title,x+135f,y+44f,Color.WHITE)}}
    private fun renderPlay(){compactButton(8f,"MODOS");compactButton(123f,"NIVEL -");compactButton(238f,"NIVEL +");compactButton(353f,"REINICIAR");compactButton(468f,if(paused)"SEGUIR" else "PAUSA");text("${family.modes[mode].title} · Nivel $level",770f,516f,Color.WHITE);text(challenge.prompt,480f,435f,Color(0xE0C23CFF.toInt()));when(mechanic()){CurriculumMechanic.QUIZ->renderQuiz();CurriculumMechanic.SORT->renderSort();CurriculumMechanic.ORDER->renderOrder();CurriculumMechanic.COUNT->renderCount()};text(feedback,480f,70f,Color.LIGHT_GRAY);if(tutorial)overlay("CÓMO JUGAR",curriculumTutorial(family.id,mode),"TOCA PARA EMPEZAR");if(paused)overlay("PAUSA","La actividad está detenida.","TOCA SEGUIR")}
    private fun renderQuiz(){challenge.options.forEachIndexed{i,label->val x=170f+(i%2)*320f;val y=275f-(i/2)*130f;box(x,y,280f,105f,Color(0x243E78FF.toInt()));text(label,x+140f,y+63f,Color.WHITE)}}
    private fun renderSort(){val labels=sortLabels(family.id,mode);box(90f,105f,350f,120f,Color(0x6B4FA3FF.toInt()));box(520f,105f,350f,120f,Color(0x3154B5FF.toInt()));text(labels.first,265f,175f,Color.WHITE);text(labels.second,695f,175f,Color.WHITE);box(340f,270f,280f,110f,if(draggingSort)Color(0xE0C23CFF.toInt())else Color(0x4C9A5FFF.toInt()));text(sortItem(family.id,mode,step).label,480f,335f,Color.WHITE)}
    private fun renderOrder(){val w=780f/ordered.size;ordered.forEachIndexed{i,label->val x=90f+i*w;box(x+5f,205f,w-10f,105f,if(i==draggingOrder)Color(0xE0C23CFF.toInt())else Color(0x6B4FA3FF.toInt()));text(label,x+w/2,268f,Color.WHITE)}}
    private fun renderCount(){val count=countTarget(level);repeat(count){i->val x=165f+(i%6)*105f;val y=145f+(i/6)*100f;shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=if(i in counted)Color(0x4C9A5FFF.toInt())else Color(0xE0C23CFF.toInt());shapes.circle(x+50f,y+48f,30f,24);shapes.end()};text("${counted.size} / $count",480f,115f,Color.WHITE)}
    private fun renderResult(){text("NIVEL SUPERADO",480f,405f,Color.WHITE);text("Puntuación $score · Estrellas ${starsForScore(score)}/3",480f,330f,Color(0xE0C23CFF.toInt()));box(230f,80f,220f,75f,Color(0x3D507AFF.toInt()));box(510f,80f,220f,75f,Color(0x4C9A5FFF.toInt()));text("MODOS",340f,128f,Color.WHITE);text("SIGUIENTE",620f,128f,Color.WHITE)}
    private fun button(x:Float,label:String){box(x,488f,120f,40f,Color(0x3D507AFF.toInt()));text(label,x+60f,516f,Color.WHITE)};private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.rect(x,y,w,h);shapes.end()};private fun text(v:String,x:Float,y:Float,c:Color){batch.begin();font.color=c;font.draw(batch,v,x-220f,y,440f,Align.center,false);batch.end()}
    private fun compactButton(x:Float,label:String){box(x,488f,107f,40f,Color(0x3D507AFF.toInt()));text(label,x+53f,516f,Color.WHITE)}
    private fun overlay(title:String,body:String,action:String){box(150f,145f,660f,250f,Color(0x111A2CFA.toInt()));text(title,480f,345f,Color(0xE0C23CFF.toInt()));text(body,480f,275f,Color.WHITE);text(action,480f,205f,Color.LIGHT_GRAY)}
    override fun resize(w:Int,h:Int)=viewport.update(w,h,true);override fun dispose(){shapes.dispose();batch.dispose();font.dispose()}
}
