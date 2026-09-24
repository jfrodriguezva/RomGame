package com.miambiente.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.*
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

internal fun vecinos(i: Int, cols: Int, total: Int) = buildList {
    if (i >= cols) add(i - cols); if (i + cols < total) add(i + cols)
    if (i % cols > 0) add(i - 1); if (i % cols < cols - 1) add(i + 1)
}
internal fun moverValido(pos: Int, delta: Int, libres: Set<Int>, cols: Int): Int {
    val n = pos + delta
    if (n !in libres || delta == 1 && pos % cols == cols - 1 || delta == -1 && pos % cols == 0) return pos
    return n
}

internal fun capturarTerritorio(
    seguras: Set<Int>,
    trazo: Set<Int>,
    guardianes: Set<Int>,
    cols: Int,
    total: Int,
): Set<Int> {
    val muro = seguras + trazo
    val pendientes = ((0 until total).toSet() - muro).toMutableSet()
    val capturadas = mutableSetOf<Int>()
    while (pendientes.isNotEmpty()) {
        val inicio = pendientes.first()
        val componente = mutableSetOf(inicio)
        val cola = ArrayDeque<Int>().apply { add(inicio) }
        pendientes.remove(inicio)
        while (cola.isNotEmpty()) {
            val actual = cola.removeFirst()
            vecinos(actual, cols, total).filter { it in pendientes }.forEach {
                pendientes.remove(it)
                componente.add(it)
                cola.add(it)
            }
        }
        if (componente.none { it in guardianes }) capturadas += componente
    }
    return muro + capturadas
}

/** Trazado de territorio: sale del borde seguro, dibuja una línea y la cierra para revelar área. */
@Composable fun MosaicoScreen(onVolver: () -> Unit) {
    val s=LocalServices.current; val scope=rememberCoroutineScope(); val juego=buscarJuego("mosaico")!!
    val cols=10; val total=100; val borde=remember{(0 until total).filter{it/cols in setOf(0,9)||it%cols in setOf(0,9)}.toSet()}
    var seguras by remember{mutableStateOf(borde)}; var trazo by remember{mutableStateOf(emptyList<Int>())}; var jugador by remember{mutableIntStateOf(0)}
    var guardianes by remember{mutableStateOf(listOf(44,55))}; var vidas by remember{mutableIntStateOf(3)}; var jugando by remember{mutableStateOf(true)}
    fun reiniciar(){seguras=borde;trazo=emptyList();jugador=0;guardianes=listOf(44,55);vidas=3;jugando=true}
    fun paso(delta:Int){if(!jugando)return;val n=moverValido(jugador,delta,(0 until total).toSet(),cols);if(n==jugador)return
        jugador=n
        if(n in seguras&&trazo.isNotEmpty()){seguras=capturarTerritorio(seguras,trazo.toSet(),guardianes.toSet(),cols,total);trazo=emptyList();s.sound.tocar(Efecto.CORRECT)}
        else if(n !in seguras){if(n in trazo)return;trazo=trazo+n}
        if(seguras.size>=70){jugando=false;s.sound.tocar(Efecto.WIN);scope.launch{s.progress.completarNivel(juego.id,1)}}
    }
    LaunchedEffect(jugando,jugador,trazo){while(jugando){delay(420);guardianes=guardianes.map{g->vecinos(g,cols,total).filter{it !in seguras}.minByOrNull{abs(it/cols-jugador/cols)+abs(it%cols-jugador%cols)}?:g};if(guardianes.any{it==jugador||it in trazo}){vidas--;trazo=emptyList();jugador=0;s.haptics.vibrar(Patron.ERROR);if(vidas<=0)jugando=false}}}
    GameShell(juego,"Territorio ${seguras.size}% · Vidas $vidas",onVolver=onVolver,acciones=if(!jugando)({Button(onClick=::reiniciar){Text("Reintentar")}})else null){
        Column(Modifier.fillMaxSize().padding(8.dp),horizontalAlignment=Alignment.CenterHorizontally){LazyVerticalGrid(GridCells.Fixed(cols),Modifier.size(360.dp)){items(total){i->Box(Modifier.size(36.dp).padding(1.dp).background(when{i==jugador->Color.Yellow;i in guardianes->Color.Red;i in trazo->Color.Cyan;i in seguras->Color(0xFF77BE8C);else->Color(0xFF17283A)}),contentAlignment=Alignment.Center){Text(when{i==jugador->"✦";i in guardianes->"◆";else->""},fontSize=12.sp)}}};Controles{paso(it)} }
    }
}

private data class EnemigoOeste(val id:Int,val x:Int,val fila:Int,val vida:Int=1,val jefe:Boolean=false)
private data class BalaOeste(val x:Int,val fila:Int)

/** Acción lateral: avance por el nivel, salto, proyectiles, enemigos y duelo final. */
@Composable fun VaquerosScreen(onVolver:()->Unit){
    val s=LocalServices.current;val scope=rememberCoroutineScope();val juego=buscarJuego("vaqueros")!!
    var x by remember{mutableIntStateOf(1)};var fila by remember{mutableIntStateOf(1)}
    var enemigos by remember{mutableStateOf(emptyList<EnemigoOeste>())};var balas by remember{mutableStateOf(emptyList<BalaOeste>())}
    var puntos by remember{mutableIntStateOf(0)};var vidas by remember{mutableIntStateOf(3)};var distancia by remember{mutableIntStateOf(0)}
    var siguienteId by remember{mutableIntStateOf(1)};var jefeVencido by remember{mutableStateOf(false)};var ronda by remember{mutableIntStateOf(0)};var jugando by remember{mutableStateOf(true)}
    fun reiniciar(){x=1;fila=1;enemigos=emptyList();balas=emptyList();puntos=0;vidas=3;distancia=0;siguienteId=1;jefeVencido=false;ronda++;jugando=true}
    fun disparar(){if(jugando&&balas.none{it.x==x+1&&it.fila==fila}){balas=balas+BalaOeste(x+1,fila);s.sound.tocar(Efecto.CLICK)}}
    fun avanzar(){if(jugando){if(x<4)x++ else distancia=(distancia+2).coerceAtMost(100)}}
    LaunchedEffect(ronda,jugando){while(jugando){delay(180)
        val balasMovidas=balas.map{it.copy(x=it.x+1)}.filter{it.x<=8}.toMutableList();val vivos=enemigos.toMutableList()
        balasMovidas.toList().forEach{b->val blanco=vivos.filter{it.fila==b.fila&&it.x==b.x}.minByOrNull{it.x};if(blanco!=null){balasMovidas.remove(b);vivos.remove(blanco);if(blanco.vida>1)vivos+=blanco.copy(vida=blanco.vida-1)else{puntos+=if(blanco.jefe)5 else 1;if(blanco.jefe)jefeVencido=true}}}
        enemigos=vivos.map{e->if(e.jefe)e else e.copy(x=e.x-1)}.filter{e->if(!e.jefe&&e.x<=x&&e.fila==fila){vidas--;false}else e.x>=0};balas=balasMovidas
        if(distancia<80&&enemigos.count{!it.jefe}<3&&(0..3).random()==0){enemigos=enemigos+EnemigoOeste(siguienteId++,8,(0..1).random())}
        if(distancia>=80&&!jefeVencido&&enemigos.none{it.jefe})enemigos=enemigos+EnemigoOeste(siguienteId++,7,1,vida=8,jefe=true)
        distancia=(distancia+1).coerceAtMost(100)
        if(vidas<=0)jugando=false
        if(jefeVencido){jugando=false;s.sound.tocar(Efecto.WIN);scope.launch{s.progress.completarNivel(juego.id,1)}}
    }}
    GameShell(juego,"Ruta $distancia% · Bandidos $puntos · Vidas $vidas",onVolver=onVolver,acciones=if(!jugando)({Button(onClick=::reiniciar){Text("Otra partida")}})else null){
        Column(Modifier.fillMaxSize().background(Color(0xFFF2D291)).padding(12.dp),verticalArrangement=Arrangement.SpaceBetween){
            Column{repeat(2){f->Row(Modifier.fillMaxWidth()){repeat(9){c->
                Box(Modifier.weight(1f).height(88.dp).background(if(f==0)Color(0xFFBFE1F4)else Color(0xFFD8A45F)),contentAlignment=Alignment.Center){
                    Text(when{c==x&&f==fila->"🤠";enemigos.any{it.x==c&&it.fila==f&&it.jefe}->"👹";enemigos.any{it.x==c&&it.fila==f}->"🥷";balas.any{it.x==c&&it.fila==f}->"•";else->""},fontSize=32.sp)
                }
            }}}}
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){Button({x=(x-1).coerceAtLeast(0)}){Text("◀")};Button({fila=1-fila}){Text(if(fila==0)"Bajar" else "Saltar")};Button(::disparar){Text("Disparar")};Button(::avanzar){Text("▶")}}
        }
    }
}

private val MAPA_PUNTOS=listOf("###########","#o.......o#","#.###.###.#","#.........#","#.#.###.#.#","#.........#","#.###.###.#","#o.......o#","###########")
/** Laberinto en tiempo real: puntos, energizantes, cuatro perseguidores, vidas y estado vulnerable. */
@Composable fun ComepuntosScreen(onVolver:()->Unit){
    val s=LocalServices.current;val scope=rememberCoroutineScope();val juego=buscarJuego("comepuntos")!!;val cols=11;val total=99
    val libres=remember{MAPA_PUNTOS.flatMapIndexed{f,r->r.indices.filter{r[it]!='#'}.map{f*cols+it}}.toSet()};val poderes=remember{MAPA_PUNTOS.flatMapIndexed{f,r->r.indices.filter{r[it]=='o'}.map{f*cols+it}}.toSet()}
    var jugador by remember{mutableIntStateOf(12)};var direccion by remember{mutableIntStateOf(1)};var fantasmas by remember{mutableStateOf(listOf(20,78,76,18))};var puntos by remember{mutableStateOf(libres-jugador-fantasmas.toSet())};var poder by remember{mutableIntStateOf(0)};var vidas by remember{mutableIntStateOf(3)};var ronda by remember{mutableIntStateOf(0)};var terminado by remember{mutableStateOf(false)}
    fun reiniciar(){jugador=12;direccion=1;fantasmas=listOf(20,78,76,18);puntos=libres-jugador-fantasmas.toSet();poder=0;vidas=3;terminado=false;ronda++}
    LaunchedEffect(ronda,terminado){while(!terminado){delay(220)
        val jugadorAnterior=jugador;val fantasmasAnteriores=fantasmas
        jugador=moverValido(jugador,direccion,libres,cols)
        if(jugador in puntos){puntos=puntos-jugador;if(jugador in poderes)poder=28}
        val choquePrevio=fantasmas.indexOf(jugador)
        if(choquePrevio>=0&&poder>0){fantasmas=fantasmas.toMutableList().also{it[choquePrevio]=49};s.sound.tocar(Efecto.CORRECT)}
        fantasmas=fantasmas.mapIndexed{idx,g->val ops=vecinos(g,cols,total).filter{it in libres};if(poder>0)ops.maxByOrNull{abs(it/cols-jugador/cols)+abs(it%cols-jugador%cols)}?:g else if(idx%2==0)ops.minByOrNull{abs(it/cols-jugador/cols)+abs(it%cols-jugador%cols)}?:g else ops.random()}
        val choque=fantasmas.indexOfFirst{it==jugador}>=0||fantasmas.indices.any{fantasmas[it]==jugadorAnterior&&fantasmasAnteriores[it]==jugador}
        if(choque){if(poder>0){fantasmas=fantasmas.map{if(it==jugador||it==jugadorAnterior)49 else it};s.sound.tocar(Efecto.CORRECT)}else{vidas--;jugador=12;fantasmas=listOf(20,78,76,18);s.haptics.vibrar(Patron.ERROR);if(vidas<=0)terminado=true}}
        if(poder>0)poder--
        if(puntos.isEmpty()){terminado=true;s.sound.tocar(Efecto.WIN);scope.launch{s.progress.completarNivel(juego.id,1)}}
    }}
    GameShell(juego,"Puntos ${puntos.size} · Vidas $vidas${if(poder>0)" · ¡Poder!" else ""}",onVolver=onVolver,acciones=if(terminado)({Button(onClick=::reiniciar){Text("Reintentar")}})else null){Column(Modifier.fillMaxSize().padding(8.dp),horizontalAlignment=Alignment.CenterHorizontally){LazyVerticalGrid(GridCells.Fixed(cols),Modifier.size(352.dp)){items(total){i->Box(Modifier.size(32.dp).background(if(i in libres)Color(0xFF10132D)else Color(0xFF3154B5)),contentAlignment=Alignment.Center){Text(when{i==jugador->"🟡";i in fantasmas->if(poder>0)"🔵" else "👻";i in puntos&&i in poderes->"●";i in puntos->"·";else->""},color=Color.White,fontSize=14.sp)}}};Controles{direccion=it}}}
}

private data class MaloNieve(val id:Int,val x:Int,val piso:Int,val nieve:Int=0,val dir:Int=1)
private data class BolaNieve(val x:Int,val piso:Int,val dir:Int)
/** Plataformas: moverse, saltar de piso, disparar nieve y empujar la bola completa. */
@Composable fun NieveScreen(onVolver:()->Unit){
    val s=LocalServices.current;val scope=rememberCoroutineScope();val juego=buscarJuego("nieve")!!
    fun enemigosIniciales()=List(6){MaloNieve(it,2+it%3*2,it/3,dir=if(it%2==0)1 else -1)}
    var x by remember{mutableIntStateOf(1)};var piso by remember{mutableIntStateOf(0)};var malos by remember{mutableStateOf(enemigosIniciales())};var bolas by remember{mutableStateOf(emptyList<BolaNieve>())};var puntos by remember{mutableIntStateOf(0)};var vidas by remember{mutableIntStateOf(3)};var ronda by remember{mutableIntStateOf(0)};var jugando by remember{mutableStateOf(true)}
    fun reiniciar(){x=1;piso=0;malos=enemigosIniciales();bolas=emptyList();puntos=0;vidas=3;ronda++;jugando=true}
    fun nieve(){val b=malos.filter{it.piso==piso}.minByOrNull{abs(it.x-x)}?:return;val nuevo=malos.toMutableList();val i=nuevo.indexOf(b);if(b.nieve>=3){nuevo.removeAt(i);bolas=bolas+BolaNieve(b.x,b.piso,if(b.x>=x)1 else -1)}else nuevo[i]=b.copy(nieve=b.nieve+1);malos=nuevo;s.sound.tocar(Efecto.CLICK)}
    LaunchedEffect(ronda,jugando){while(jugando){delay(420);val movidas=bolas.map{it.copy(x=it.x+it.dir)}.filter{it.x in 0..7};val golpeados=malos.filter{m->movidas.any{it.piso==m.piso&&it.x==m.x}};if(golpeados.isNotEmpty()){puntos+=golpeados.size;s.sound.tocar(Efecto.CORRECT)};malos=malos.filterNot{it in golpeados}.map{m->if(m.nieve>=3)m else{val nx=m.x+m.dir;if(nx !in 0..7)m.copy(dir=-m.dir)else m.copy(x=nx)}};bolas=movidas;if(malos.any{it.x==x&&it.piso==piso&&it.nieve<3}){vidas--;x=1;piso=0;s.haptics.vibrar(Patron.ERROR)};if(vidas<=0)jugando=false;if(malos.isEmpty()){jugando=false;s.sound.tocar(Efecto.WIN);scope.launch{s.progress.completarNivel(juego.id,1)}}}}
    GameShell(juego,"Enemigos ${malos.size} · Puntos $puntos · Vidas $vidas",onVolver=onVolver,acciones=if(!jugando)({Button(onClick=::reiniciar){Text("Otro nivel")}})else null){
        Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFDDF4FF),Color.White))).padding(12.dp),verticalArrangement=Arrangement.SpaceBetween){
            Column{repeat(2){f->Row(Modifier.fillMaxWidth().height(130.dp)){repeat(8){c->
                Box(Modifier.weight(1f).fillMaxHeight().background(if(f==0)Color(0xFFF4FBFF)else Color(0xFFE2F3FA)),contentAlignment=Alignment.Center){
                    val m=malos.find{it.x==c&&it.piso==f};Text(when{c==x&&piso==f->"⛄";bolas.any{it.x==c&&it.piso==f}->"⚪";m!=null->listOf("👾","❄️","⛄","⚪")[m.nieve];else->""},fontSize=34.sp)
                }
            }}}}
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){Button({x=(x-1).coerceAtLeast(0)}){Text("◀")};Button({piso=1-piso}){Text("Saltar")};Button(::nieve){Text("Nieve")};Button({x=(x+1).coerceAtMost(7)}){Text("▶")}}
        }
    }
}

private data class ObjetoEspacial(val id:Int,val celda:Int,val profundidad:Int,val meteoro:Boolean=false,val jefe:Boolean=false,val vida:Int=1)
/** Shooter sobre rieles: vuelo automático, movimiento libre, profundidad, disparos y jefe. */
@Composable fun EscuadronEstelarScreen(onVolver:()->Unit){
    val s=LocalServices.current;val scope=rememberCoroutineScope();val juego=buscarJuego("escuadron-estelar")!!
    var nave by remember{mutableIntStateOf(4)};var objetos by remember{mutableStateOf(emptyList<ObjetoEspacial>())};var escudo by remember{mutableIntStateOf(5)};var puntos by remember{mutableIntStateOf(0)};var distancia by remember{mutableIntStateOf(0)};var siguienteId by remember{mutableIntStateOf(0)};var ronda by remember{mutableIntStateOf(0)};var jugando by remember{mutableStateOf(false)}
    fun iniciar(){nave=4;objetos=emptyList();escudo=5;puntos=0;distancia=0;siguienteId=0;ronda++;jugando=true}
    fun mover(delta:Int){if(jugando)nave=moverValido(nave,delta,(0..8).toSet(),3)}
    fun disparar(){if(!jugando)return;val blanco=objetos.filter{!it.meteoro&&it.celda==nave}.minByOrNull{it.profundidad};if(blanco!=null){objetos=objetos.mapNotNull{if(it.id!=blanco.id)it else if(it.vida>1)it.copy(vida=it.vida-1)else null};puntos+=if(blanco.jefe)5 else 1;s.sound.tocar(Efecto.CORRECT)}else s.sound.tocar(Efecto.CLICK)}
    LaunchedEffect(ronda,jugando){while(jugando){delay(430);distancia++
        val avanzados=objetos.map{if(it.jefe)it.copy(profundidad=0)else it.copy(profundidad=it.profundidad-1)};val impactos=avanzados.count{it.profundidad<0&&it.celda==nave};if(impactos>0){escudo-=impactos;s.haptics.vibrar(Patron.ERROR)};objetos=avanzados.filter{it.profundidad>=0}
        if(distancia<35&&(0..2).random()>0)objetos=objetos+ObjetoEspacial(siguienteId++,(0..8).random(),2,meteoro=(0..3).random()==0)
        if(distancia==35)objetos=objetos+ObjetoEspacial(siguienteId++,4,3,jefe=true,vida=10)
        if(escudo<=0)jugando=false
        if(distancia>35&&objetos.none{it.jefe}){jugando=false;s.sound.tocar(Efecto.WIN);scope.launch{s.progress.completarNivel(juego.id,1)}}
    }}
    val jefe=objetos.firstOrNull{it.jefe}
    GameShell(juego,if(distancia<35)"Sector $distancia/35 · Naves $puntos · Escudo $escudo" else "Jefe ${jefe?.vida?:0}/10 · Escudo $escudo",onVolver=onVolver,acciones={Button(onClick=::iniciar){Text(if(jugando)"Reiniciar" else "Despegar")}}){Column(Modifier.fillMaxSize().background(Color(0xFF080D25)).padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally){LazyVerticalGrid(GridCells.Fixed(3),Modifier.size(300.dp)){items(9){i->val objeto=objetos.filter{it.celda==i}.minByOrNull{it.profundidad};Box(Modifier.size(100.dp).padding(3.dp).clip(RoundedCornerShape(12.dp)).background(if(i==nave)Color(0xFF30467A)else Color(0xFF151D42)).clickable{nave=i},contentAlignment=Alignment.Center){Text(when{objeto?.jefe==true->"👾";objeto?.meteoro==true->"☄️";objeto!=null->"🛸";i==nave->"🚀";else->"✨"},fontSize=when(objeto?.profundidad){0->44.sp;1->34.sp;else->26.sp})}}};Controles(::mover);Button(::disparar){Text("DISPARAR")}}}
}

private data class RivalCarrera(val id:Int,val carril:Int,val fila:Int)
/** Carrera completa: acelerador/freno, volante, curvas, tráfico, vueltas y daños. */
@Composable fun GranPremioScreen(onVolver:()->Unit){
    val s=LocalServices.current;val scope=rememberCoroutineScope();val juego=buscarJuego("gran-premio")!!
    var carril by remember{mutableIntStateOf(1)};var velocidad by remember{mutableIntStateOf(0)};var rivales by remember{mutableStateOf(emptyList<RivalCarrera>())};var siguienteId by remember{mutableIntStateOf(0)};var progreso by remember{mutableIntStateOf(0)};var vuelta by remember{mutableIntStateOf(1)};var dano by remember{mutableIntStateOf(0)};var curva by remember{mutableIntStateOf(0)};var tramo by remember{mutableIntStateOf(0)};var ronda by remember{mutableIntStateOf(0)};var jugando by remember{mutableStateOf(false)}
    fun iniciar(){carril=1;velocidad=0;rivales=emptyList();siguienteId=0;progreso=0;vuelta=1;dano=0;curva=0;tramo=0;ronda++;jugando=true}
    LaunchedEffect(ronda,jugando){while(jugando){delay((720-velocidad*4).coerceAtLeast(220).toLong());if(velocidad>0){tramo++;progreso+=velocidad/18+1
        val movidos=rivales.map{it.copy(fila=it.fila+1)};val choques=movidos.count{it.fila>=5&&it.carril==carril};if(choques>0){dano+=choques;velocidad=(velocidad-35).coerceAtLeast(0);s.haptics.vibrar(Patron.ERROR)};rivales=movidos.filter{it.fila<6}
        if(rivales.size<3&&(0..2).random()>0)rivales=rivales+RivalCarrera(siguienteId++,(0..2).random(),0)
        if(tramo%7==0)curva=(-1..1).random();if(curva!=0&&velocidad>85&&carril!=if(curva<0)0 else 2){dano++;velocidad-=20}
        if(progreso>=100){progreso-=100;vuelta++;if(vuelta>3){jugando=false;s.sound.tocar(Efecto.WIN);scope.launch{s.progress.completarNivel(juego.id,1)}}};if(dano>=3)jugando=false
    }}}
    GameShell(juego,"Vuelta ${vuelta.coerceAtMost(3)}/3 · $progreso% · $velocidad km/h · Daño $dano/3",onVolver=onVolver,acciones={Button(onClick=::iniciar){Text(if(jugando)"Reiniciar" else "Arrancar")}}){Column(Modifier.fillMaxSize().background(Color(0xFF3F4548)).padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(if(curva<0)"↩ Curva izquierda" else if(curva>0)"Curva derecha ↪" else "Recta",color=Color.White,fontWeight=FontWeight.Bold);repeat(6){f->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){repeat(3){c->Box(Modifier.size(82.dp,48.dp).background(if(c==1)Color(0xFF50575A)else Color(0xFF484E51)),contentAlignment=Alignment.Center){if(rivales.any{it.fila==f&&it.carril==c})Text("🚙",fontSize=28.sp)}}}};Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){repeat(3){c->Box(Modifier.size(82.dp,54.dp).clip(RoundedCornerShape(12.dp)).background(if(c==carril)Color(0xFF8DB9DB)else Color.LightGray).clickable{if(jugando)carril=c},contentAlignment=Alignment.Center){if(c==carril)Text("🏎️",fontSize=34.sp)}}};Row(Modifier.padding(top=8.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Button({velocidad=(velocidad-20).coerceAtLeast(0)}){Text("Freno")};Button({carril=(carril-1).coerceAtLeast(0)}){Text("◀")};Button({carril=(carril+1).coerceAtMost(2)}){Text("▶")};Button({velocidad=(velocidad+15).coerceAtMost(120)}){Text("Gas")}}}}
}

@Composable private fun Controles(onMove:(Int)->Unit){Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.padding(6.dp)){Button({onMove(-10)}){Text("▲")};Row(horizontalArrangement=Arrangement.spacedBy(14.dp)){Button({onMove(-1)}){Text("◀")};Button({onMove(10)}){Text("▼")};Button({onMove(1)}){Text("▶")}}}}
