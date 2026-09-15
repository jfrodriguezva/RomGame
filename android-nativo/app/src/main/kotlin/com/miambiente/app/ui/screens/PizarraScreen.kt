package com.miambiente.app.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PathEffect
import android.graphics.DashPathEffect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.Guia
import com.miambiente.app.data.GUIAS
import com.miambiente.app.data.IMAGENES_COLOREAR
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.data.borrarDeGaleria
import com.miambiente.app.data.DibujoGuardado
import com.miambiente.app.data.exportarParaCompartir
import com.miambiente.app.data.guardarEnGaleria
import com.miambiente.app.data.leerGaleria
import com.miambiente.app.data.uriCompartible
import com.miambiente.app.model.GameDef
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.materials.ConfettiOverlay
import com.miambiente.app.ui.materials.Herramienta
import com.miambiente.app.ui.materials.PuntoP
import com.miambiente.app.ui.materials.conSimetria
import com.miambiente.app.ui.materials.rellenar
import com.miambiente.app.ui.materials.sellar
import com.miambiente.app.ui.materials.trazar
import kotlinx.coroutines.launch

private data class DefHerramienta(val id: Herramienta, val icono: String, val nombre: String)

private val HERRAMIENTAS = listOf(
    DefHerramienta(Herramienta.LAPIZ, "✏️", "Lápiz"),
    DefHerramienta(Herramienta.CRAYON, "🖍️", "Crayón"),
    DefHerramienta(Herramienta.MARCADOR, "🖊️", "Marcador"),
    DefHerramienta(Herramienta.NEON, "✨", "Neón"),
    DefHerramienta(Herramienta.AEROSOL, "💨", "Aerosol"),
    DefHerramienta(Herramienta.RELLENO, "🪣", "Pintar"),
    DefHerramienta(Herramienta.SELLO, "🌟", "Sellos"),
    DefHerramienta(Herramienta.BORRADOR, "🧽", "Borrar"),
)

private val COLORES = listOf(
    0xFF2F2A26, 0xFFE04A3F, 0xFFF08A3C, 0xFFF5C542, 0xFF8BBF5A, 0xFF3F9E6D,
    0xFF3FA7C4, 0xFF3F6FB5, 0xFF7A5EC4, 0xFFC25AA6, 0xFFE58FA8, 0xFF8B5E3C,
    0xFFC9A87C, 0xFF9AA5AD, 0xFFFFFFFF, 0xFF000000,
).map { Color(it) }

private data class DefGrosor(val valor: Float, val etiqueta: String)
private val GROSORES = listOf(
    DefGrosor(4f, "Fino"), DefGrosor(10f, "Medio"), DefGrosor(22f, "Grueso"), DefGrosor(44f, "Enorme"),
)

private data class DefFondo(val id: String, val nombre: String, val base: Color, val oscuro: Boolean = false)
private val FONDOS = listOf(
    DefFondo("papel", "Papel", Color(0xFFFDFAF5)),
    DefFondo("blanco", "Blanco", Color(0xFFFFFFFF)),
    DefFondo("cuadricula", "Cuadros", Color(0xFFFFFFFF)),
    DefFondo("renglones", "Renglones", Color(0xFFFFFFFF)),
    DefFondo("puntos", "Puntos", Color(0xFFFDFAF5)),
    DefFondo("pizarron", "Pizarrón", Color(0xFF26382F), oscuro = true),
    DefFondo("kraft", "Kraft", Color(0xFFD8C0A0)),
)

private val SELLOS = listOf("⭐", "🌸", "🦋", "🐞", "🌈", "☁️", "🌞", "🌙", "🐢", "🐝", "🍎", "🌵", "🐳", "🎈", "🍀", "❤️")
private val SIMETRIAS = listOf(1, 2, 4, 6, 8)
private val MISIONES = listOf("☀️", "🏠", "🐟", "🌳", "🐱", "⭐", "🌸", "🦋", "🚗", "🎈", "🐶", "🌈")

private const val MAX_HISTORIA = 14

/**
 * La pizarra grande — puerto completo de app/pizarra/page.tsx, no una
 * versión reducida: 8 herramientas con la misma textura que la web
 * (lib/pizarra.ts portado literal — mismo jitter de crayón, mismo brillo
 * de neón, mismo relleno por líneas), 16 colores, 4 grosores, modo
 * mandala, 7 fondos, guías de letras/números/formas (data/guias.ts
 * completo, parseado con PathParser en vez de reinterpretar cada figura),
 * plantillas para colorear, misiones con confeti, deshacer/rehacer, y
 * galería — aquí como archivos PNG reales, no localStorage.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PizarraScreen(onVolver: () -> Unit) {
    val services = LocalServices.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val juego = buscarJuego("pizarra")!!

    var herramienta by remember { mutableStateOf(Herramienta.CRAYON) }
    var color by remember { mutableStateOf(Color(0xFFE04A3F)) }
    var grosor by remember { mutableStateOf(10f) }
    var sello by remember { mutableStateOf("⭐") }
    var fondo by remember { mutableStateOf(FONDOS[0]) }
    var simetria by remember { mutableStateOf(1) }
    var guia by remember { mutableStateOf<Guia?>(null) }
    var plantilla by remember { mutableStateOf<Guia?>(null) }
    var mision by remember { mutableStateOf<String?>(null) }
    var misionLista by remember { mutableStateOf(false) }
    var panel by remember { mutableStateOf<String?>("pincel") }
    var galeria by remember { mutableStateOf(leerGaleria(context)) }
    var aviso by remember { mutableStateOf<String?>(null) }

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var androidCanvas by remember { mutableStateOf<android.graphics.Canvas?>(null) }
    var revision by remember { mutableIntStateOf(0) }
    val historia = remember { mutableListOf<Bitmap>() }
    val futuro = remember { mutableListOf<Bitmap>() }
    var puedeDeshacer by remember { mutableStateOf(false) }
    var puedeRehacer by remember { mutableStateOf(false) }

    fun mostrarAviso(texto: String) {
        aviso = texto
        scope.launch { kotlinx.coroutines.delay(1600); aviso = null }
    }

    fun guardarPaso() {
        val bmp = bitmap ?: return
        historia.add(bmp.copy(Bitmap.Config.ARGB_8888, true))
        if (historia.size > MAX_HISTORIA) historia.removeAt(0)
        futuro.clear()
        puedeDeshacer = true
        puedeRehacer = false
    }

    fun deshacer() {
        val bmp = bitmap ?: return
        if (historia.isEmpty()) return
        futuro.add(bmp.copy(Bitmap.Config.ARGB_8888, true))
        val anterior = historia.removeAt(historia.size - 1)
        androidCanvas?.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
        androidCanvas?.drawBitmap(anterior, 0f, 0f, null)
        revision++
        services.sound.tocar(Efecto.CLICK)
        services.haptics.vibrar(Patron.TOQUE)
        puedeDeshacer = historia.isNotEmpty()
        puedeRehacer = true
    }

    fun rehacer() {
        val bmp = bitmap ?: return
        if (futuro.isEmpty()) return
        historia.add(bmp.copy(Bitmap.Config.ARGB_8888, true))
        val siguiente = futuro.removeAt(futuro.size - 1)
        androidCanvas?.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
        androidCanvas?.drawBitmap(siguiente, 0f, 0f, null)
        revision++
        services.sound.tocar(Efecto.CLICK)
        puedeDeshacer = true
        puedeRehacer = futuro.isNotEmpty()
    }

    fun limpiar() {
        guardarPaso()
        androidCanvas?.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
        revision++
        mostrarAviso("Hoja nueva")
    }

    fun guardar() {
        val bmp = bitmap ?: return
        galeria = guardarEnGaleria(context, componerConFondo(bmp, fondo.base.toArgb()))
        services.sound.tocar(Efecto.WIN)
        services.haptics.vibrar(Patron.LOGRO)
        mostrarAviso("Guardado en la galería")
    }

    fun compartir() {
        val bmp = bitmap ?: return
        val archivo = exportarParaCompartir(context, bmp, fondo.base.toArgb())
        val uri = uriCompartible(context, archivo)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir mi dibujo"))
    }

    fun abrirDibujo(d: DibujoGuardado) {
        guardarPaso()
        val cargado = android.graphics.BitmapFactory.decodeFile(d.archivo.absolutePath)
        androidCanvas?.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
        if (cargado != null && bitmap != null) {
            androidCanvas?.drawBitmap(cargado, null, android.graphics.Rect(0, 0, bitmap!!.width, bitmap!!.height), null)
        }
        revision++
        panel = null
        mostrarAviso("Dibujo abierto")
    }

    fun completarMision() {
        services.sound.tocar(Efecto.WIN)
        services.haptics.vibrar(Patron.LOGRO)
        misionLista = true
        scope.launch { kotlinx.coroutines.delay(1400); misionLista = false }
        guardarPaso()
        androidCanvas?.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
        revision++
        mision = MISIONES.random()
    }

    GameShellPizarra(
        juego = juego,
        onVolver = onVolver,
        fondoOscuro = fondo.oscuro,
        puedeDeshacer = puedeDeshacer,
        puedeRehacer = puedeRehacer,
        panelAbierto = panel != null,
        onDeshacer = ::deshacer,
        onRehacer = ::rehacer,
        onGuardar = ::guardar,
        onCompartir = ::compartir,
        onLimpiar = ::limpiar,
        onTogglePanel = { panel = if (panel == null) "pincel" else null },
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(fondo.base),
            ) {
                // Patrón del fondo (cuadros, renglones, puntos).
                Canvas(Modifier.fillMaxSize()) { dibujarPatronFondo(this, fondo.id) }

                // Guía punteada debajo del dibujo.
                guia?.let { g -> Canvas(Modifier.fillMaxSize()) { dibujarGuia(this, g, fondo.oscuro, punteada = true, alfa = 0.30f) } }
                plantilla?.let { p -> Canvas(Modifier.fillMaxSize()) { dibujarGuia(this, p, fondo.oscuro, punteada = false, alfa = 0.70f) } }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { size ->
                            if (size.width <= 0 || size.height <= 0) return@onSizeChanged
                            if (bitmap == null || bitmap!!.width != size.width || bitmap!!.height != size.height) {
                                val nuevo = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
                                val nuevoCanvas = android.graphics.Canvas(nuevo)
                                bitmap?.let { previo -> nuevoCanvas.drawBitmap(previo, 0f, 0f, null) }
                                bitmap = nuevo
                                androidCanvas = nuevoCanvas
                                revision++
                            }
                        }
                        .pointerInput(herramienta, color, grosor, simetria, sello) {
                            val colorArgb = color.toArgb()
                            awaitPointerEventScope {
                                while (true) {
                                    val down = awaitFirstDown(pass = PointerEventPass.Main)
                                    val lienzo = androidCanvas
                                    val bmp = bitmap
                                    if (lienzo == null || bmp == null) continue
                                    guardarPaso()
                                    val inicio = PuntoP(down.position.x, down.position.y, down.pressure.takeIf { it > 0f } ?: 0.5f)

                                    when (herramienta) {
                                        Herramienta.RELLENO -> {
                                            rellenar(bmp, inicio.x.toInt(), inicio.y.toInt(), colorArgb)
                                            revision++
                                            services.sound.tocar(Efecto.STAR)
                                            services.haptics.vibrar(Patron.TOQUE)
                                        }
                                        Herramienta.SELLO -> {
                                            conSimetria(bmp.width, bmp.height, simetria, inicio, inicio) { a, _ ->
                                                sellar(lienzo, a, sello, grosor * 3.4f)
                                            }
                                            revision++
                                            services.sound.tocar(Efecto.STAR)
                                            services.haptics.vibrar(Patron.TOQUE)
                                        }
                                        else -> {
                                            var ultimo = inicio
                                            conSimetria(bmp.width, bmp.height, simetria, ultimo, ultimo.copy(x = ultimo.x + 0.6f)) { a, b ->
                                                trazar(lienzo, a, b, herramienta, colorArgb, grosor)
                                            }
                                            revision++
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val cambio = event.changes.firstOrNull { it.id == down.id } ?: break
                                                if (!cambio.pressed) { cambio.consume(); break }
                                                for (h in cambio.historical) {
                                                    val p = PuntoP(h.position.x, h.position.y, 0.5f)
                                                    conSimetria(bmp.width, bmp.height, simetria, ultimo, p) { a, b -> trazar(lienzo, a, b, herramienta, colorArgb, grosor) }
                                                    ultimo = p
                                                }
                                                val p = PuntoP(cambio.position.x, cambio.position.y, cambio.pressure.takeIf { it > 0f } ?: 0.5f)
                                                conSimetria(bmp.width, bmp.height, simetria, ultimo, p) { a, b -> trazar(lienzo, a, b, herramienta, colorArgb, grosor) }
                                                ultimo = p
                                                revision++
                                                cambio.consume()
                                            }
                                        }
                                    }
                                }
                            }
                        },
                ) {
                    // Se lee `revision` aquí para que Compose suscriba este
                    // punto de recomposición: mutar el Bitmap en su lugar no
                    // dispara redibujo por sí solo, a diferencia de un
                    // estado normal — este contador es lo que sí lo hace.
                    revision
                    bitmap?.let { bmp ->
                        androidx.compose.foundation.Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                ConfettiOverlay(activo = misionLista)

                mision?.let { m ->
                    Column(
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 56.dp, end = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier.size(64.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.95f)),
                            contentAlignment = Alignment.Center,
                        ) { Text(m, fontSize = 32.sp) }
                        Box(
                            Modifier
                                .padding(top = 6.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF3F9E6D))
                                .clickable { completarMision() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) { Text("¡Listo! 🎉", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    }
                }

                AvisoFlotante(aviso)
            }

            CajonAnimado(visible = panel != null) {
                CajonHerramientas(
                    panel = panel ?: "pincel",
                    onPanel = { panel = it },
                    herramienta = herramienta, onHerramienta = { herramienta = it },
                    color = color, onColor = { color = it },
                    grosor = grosor, onGrosor = { grosor = it },
                    sello = sello, onSello = { sello = it },
                    fondo = fondo, onFondo = { fondo = it },
                    simetria = simetria, onSimetria = { simetria = it },
                    guia = guia, onGuia = { guia = it },
                    plantilla = plantilla, onPlantilla = { plantilla = it },
                    mision = mision, onMision = { mision = it },
                    galeria = galeria,
                    onAbrirDibujo = ::abrirDibujo,
                    onBorrarDibujo = { d -> galeria = borrarDeGaleria(context, d.archivo) },
                )
            }
        }
    }
}

/**
 * `AnimatedVisibility` extraído a su propia función con receptor explícito
 * (`BoxScope`/`ColumnScope`) porque, llamado en línea dentro de un Column
 * anidado en un Box, el compilador encuentra más de un receptor implícito
 * candidato (`ColumnScope.AnimatedVisibility` además del general) y no
 * puede resolverlo solo. Como función aparte, el único receptor en su
 * propio cuerpo es el que declara la firma — sin ambigüedad.
 */
@Composable
private fun androidx.compose.foundation.layout.BoxScope.AvisoFlotante(aviso: String?) {
    AnimatedVisibility(
        visible = aviso != null,
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 56.dp),
        enter = fadeIn(), exit = fadeOut(),
    ) {
        Box(Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFF2F2A26).copy(alpha = 0.85f)).padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(aviso ?: "", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.ColumnScope.CajonAnimado(visible: Boolean, contenido: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(tween(240)) { it } + fadeIn(),
        exit = slideOutVertically(tween(200)) { it } + fadeOut(),
    ) { contenido() }
}

private fun componerConFondo(bitmap: Bitmap, colorFondoArgb: Int): Bitmap {
    val salida = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(salida)
    canvas.drawColor(colorFondoArgb)
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    return salida
}

private fun dibujarPatronFondo(scope: androidx.compose.ui.graphics.drawscope.DrawScope, fondoId: String) {
    val nativo = scope.drawContext.canvas.nativeCanvas
    val paint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 1f }
    when (fondoId) {
        "cuadricula" -> {
            paint.color = 0xFFE8E2D8.toInt()
            var x = 0f
            while (x < scope.size.width) { nativo.drawLine(x, 0f, x, scope.size.height, paint); x += 32f }
            var y = 0f
            while (y < scope.size.height) { nativo.drawLine(0f, y, scope.size.width, y, paint); y += 32f }
        }
        "renglones" -> {
            paint.color = 0xFFDFE6EF.toInt()
            var y = 0f
            while (y < scope.size.height) { nativo.drawLine(0f, y, scope.size.width, y, paint); y += 44f }
        }
        "puntos" -> {
            val relleno = Paint().apply { color = 0xFFDDD5C8.toInt(); style = Paint.Style.FILL }
            var y = 13f
            while (y < scope.size.height) {
                var x = 13f
                while (x < scope.size.width) { nativo.drawCircle(x, y, 1.5f, relleno); x += 26f }
                y += 26f
            }
        }
        else -> {}
    }
}

/** Dibuja una guía (letra/número como texto, o figura como Path SVG parseado) centrada con margen, en un viewBox lógico de 100x100. */
private fun dibujarGuia(scope: androidx.compose.ui.graphics.drawscope.DrawScope, g: Guia, esOscuro: Boolean, punteada: Boolean, alfa: Float) {
    val nativo = scope.drawContext.canvas.nativeCanvas
    val lado = minOf(scope.size.width, scope.size.height) * 0.8f
    val offX = (scope.size.width - lado) / 2f
    val offY = (scope.size.height - lado) / 2f
    val colorTrazo = if (esOscuro) android.graphics.Color.WHITE else 0xFF8A7F70.toInt()

    nativo.save()
    nativo.translate(offX, offY)
    nativo.scale(lado / 100f, lado / 100f)

    if (g.tipo == "texto") {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.1f
            color = colorTrazo
            this.alpha = (alfa * 255).toInt()
            textAlign = Paint.Align.CENTER
            textSize = 62f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            if (punteada) pathEffect = DashPathEffect(floatArrayOf(3f, 3f), 0f)
        }
        val metrics = paint.fontMetrics
        nativo.drawText(g.contenido, 50f, 50f - (metrics.ascent + metrics.descent) / 2, paint)
    } else {
        val path = PathParser.createPathFromPathData(g.contenido)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = if (punteada) 1.1f else 2.4f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            color = colorTrazo
            this.alpha = (alfa * 255).toInt()
            if (punteada) pathEffect = DashPathEffect(floatArrayOf(3f, 3f), 0f)
        }
        if (path != null) nativo.drawPath(path, paint)
    }
    nativo.restore()
}

@Composable
private fun CajonHerramientas(
    panel: String,
    onPanel: (String) -> Unit,
    herramienta: Herramienta, onHerramienta: (Herramienta) -> Unit,
    color: Color, onColor: (Color) -> Unit,
    grosor: Float, onGrosor: (Float) -> Unit,
    sello: String, onSello: (String) -> Unit,
    fondo: DefFondo, onFondo: (DefFondo) -> Unit,
    simetria: Int, onSimetria: (Int) -> Unit,
    guia: Guia?, onGuia: (Guia?) -> Unit,
    plantilla: Guia?, onPlantilla: (Guia?) -> Unit,
    mision: String?, onMision: (String?) -> Unit,
    galeria: List<DibujoGuardado>,
    onAbrirDibujo: (DibujoGuardado) -> Unit,
    onBorrarDibujo: (DibujoGuardado) -> Unit,
) {
    val servicios = LocalServices.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .background(Color.White.copy(alpha = 0.97f)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp).heightIn(min = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf(
                "pincel" to "Pincel", "fondo" to "Hoja", "guia" to "Guías",
                "colorear" to "Colorear", "mision" to "Misión", "galeria" to "Galería",
            ).forEach { (id, nombre) ->
                PestanaChica(nombre, seleccionado = panel == id) { onPanel(id) }
            }
        }
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 10.dp, vertical = 4.dp)) {
            when (panel) {
                "pincel" -> {
                    Fila { HERRAMIENTAS.forEach { h ->
                        OpcionCuadrada(
                            seleccionado = herramienta == h.id,
                            onClick = { onHerramienta(h.id); servicios.sound.tocar(Efecto.CLICK) },
                        ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(h.icono, fontSize = 18.sp); Text(h.nombre, fontSize = 8.sp, fontWeight = FontWeight.Bold) } }
                    } }
                    if (herramienta == Herramienta.SELLO) {
                        Fila { SELLOS.forEach { s ->
                            OpcionRedonda(seleccionado = sello == s, tamano = 44.dp, onClick = { onSello(s) }) { Text(s, fontSize = 20.sp) }
                        } }
                    } else {
                        Fila { COLORES.forEach { c ->
                            OpcionColor(color = c, seleccionado = color == c) { onColor(c); servicios.sound.tocar(Efecto.CLICK) }
                        } }
                    }
                    Fila { GROSORES.forEach { g ->
                        OpcionAncha(seleccionado = grosor == g.valor, onClick = { onGrosor(g.valor) }) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(Modifier.size((g.valor / 1.6f + 4).dp).clip(CircleShape).background(if (grosor == g.valor) Color.White else Color(0xFF57504A)))
                                Text(g.etiqueta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Text("Mandala", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9AA5AD), modifier = Modifier.padding(end = 6.dp))
                        SIMETRIAS.forEach { s ->
                            Box(
                                Modifier
                                    .padding(end = 4.dp)
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (simetria == s) Color(0xFF3F342C) else Color(0xFFF3EEE4))
                                    .clickable { onSimetria(s) },
                                contentAlignment = Alignment.Center,
                            ) { Text(if (s == 1) "—" else "$s", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = if (simetria == s) Color.White else Color(0xFF6B6258)) }
                        }
                    }
                }
                "fondo" -> Fila { FONDOS.forEach { f ->
                    Box(
                        modifier = Modifier
                            .size(width = 76.dp, height = 60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(f.base)
                            .border(width = if (fondo.id == f.id) 3.dp else 1.dp, color = if (fondo.id == f.id) Color(0xFF6B6258) else Color.Black.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp))
                            .clickable { onFondo(f) },
                        contentAlignment = Alignment.BottomCenter,
                    ) { Text(f.nombre, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color.White.copy(alpha = 0.8f)).padding(2.dp)) }
                } }
                "guia" -> {
                    Text("Una figura punteada para repasar con el dedo. Se queda debajo del dibujo.", fontSize = 11.sp, color = Color(0xFF6B6258))
                    Fila {
                        OpcionAncha(seleccionado = guia == null, onClick = { onGuia(null) }) { Text("Sin guía", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        GUIAS.forEach { g -> OpcionCuadrada(seleccionado = guia?.id == g.id, onClick = { onGuia(g) }) { Text(g.icono, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold) } }
                    }
                }
                "colorear" -> {
                    Text("Elige un dibujo: queda marcado sobre la hoja y lo puedes colorear.", fontSize = 11.sp, color = Color(0xFF6B6258))
                    Fila {
                        OpcionAncha(seleccionado = plantilla == null, onClick = { onPlantilla(null) }) { Text("Ninguno", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        IMAGENES_COLOREAR.forEach { img -> OpcionCuadrada(seleccionado = plantilla?.id == img.id, onClick = { onPlantilla(img) }) { Text(img.icono, fontSize = 18.sp) } }
                    }
                }
                "mision" -> {
                    Text("Toca un ícono: aparece arriba y pide dibujarlo.", fontSize = 11.sp, color = Color(0xFF6B6258))
                    Fila {
                        if (mision != null) OpcionAncha(seleccionado = false, onClick = { onMision(null) }) { Text("Terminar", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        MISIONES.forEach { m -> OpcionCuadrada(seleccionado = mision == m, onClick = { onMision(m) }) { Text(m, fontSize = 18.sp) } }
                    }
                }
                "galeria" -> {
                    if (galeria.isEmpty()) {
                        Text("Aún no hay dibujos guardados. Usa 💾 para guardar el que estás haciendo.", fontSize = 12.sp, color = Color(0xFF9AA5AD), modifier = Modifier.padding(vertical = 20.dp))
                    } else {
                        Fila { galeria.forEach { d ->
                            Box {
                                androidx.compose.foundation.Image(
                                    bitmap = android.graphics.BitmapFactory.decodeFile(d.archivo.absolutePath).asImageBitmap(),
                                    contentDescription = "Dibujo guardado",
                                    modifier = Modifier.size(width = 100.dp, height = 76.dp).clip(RoundedCornerShape(12.dp)).clickable { onAbrirDibujo(d) },
                                )
                                Box(
                                    Modifier.align(Alignment.TopEnd).size(22.dp).clip(CircleShape).background(Color.White).clickable { onBorrarDibujo(d) },
                                    contentAlignment = Alignment.Center,
                                ) { Text("✕", fontSize = 11.sp) }
                            }
                        } }
                    }
                }
            }
        }
    }
}

@Composable
private fun Fila(contenido: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) { contenido() }
}

@Composable
private fun PestanaChica(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (seleccionado) Color(0xFF3F342C) else Color(0xFFF3EEE4))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) { Text(texto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (seleccionado) Color.White else Color(0xFF6B6258)) }
}

@Composable
private fun OpcionCuadrada(seleccionado: Boolean, onClick: () -> Unit, contenido: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (seleccionado) Color(0xFF3F342C) else Color(0xFFF3EEE4))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { contenido() }
}

@Composable
private fun OpcionAncha(seleccionado: Boolean, onClick: () -> Unit, contenido: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (seleccionado) Color(0xFF3F342C) else Color(0xFFF3EEE4))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) { contenido() }
}

@Composable
private fun OpcionRedonda(seleccionado: Boolean, tamano: androidx.compose.ui.unit.Dp, onClick: () -> Unit, contenido: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(if (seleccionado) Color(0xFFE7E0D4) else Color(0xFFFAF8F4))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { contenido() }
}

@Composable
private fun OpcionColor(color: Color, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(if (seleccionado) 40.dp else 36.dp)
            .clip(CircleShape)
            .background(color)
            .border(width = if (seleccionado) 3.dp else 1.dp, color = if (seleccionado) Color(0xFF6B6258) else Color.Black.copy(alpha = 0.1f), shape = CircleShape)
            .clickable(onClick = onClick),
    )
}

/**
 * Barra superior propia de la pizarra — a diferencia de `GameShell`
 * (consigna + nota + "siguiente nivel", pensado para materiales con
 * niveles), aquí flota sobre el lienzo como en la web: deshacer, rehacer,
 * guardar, compartir, hoja nueva y mostrar/ocultar el cajón de herramientas.
 */
@Composable
private fun GameShellPizarra(
    juego: GameDef,
    onVolver: () -> Unit,
    fondoOscuro: Boolean,
    puedeDeshacer: Boolean,
    puedeRehacer: Boolean,
    panelAbierto: Boolean,
    onDeshacer: () -> Unit,
    onRehacer: () -> Unit,
    onGuardar: () -> Unit,
    onCompartir: () -> Unit,
    onLimpiar: () -> Unit,
    onTogglePanel: () -> Unit,
    contenido: @Composable () -> Unit,
) {
    val services = LocalServices.current
    LaunchedEffect(Unit) { services.speech.hablar(juego.description) }

    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
        contenido()
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BotonBarra("←", activo = true, etiqueta = "Volver", onClick = onVolver)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BotonBarra("↩︎", puedeDeshacer, "Deshacer", onDeshacer)
                BotonBarra("↪︎", puedeRehacer, "Rehacer", onRehacer)
                BotonBarra("💾", true, "Guardar", onGuardar)
                BotonBarra("📤", true, "Compartir", onCompartir)
                BotonBarra("🗑️", true, "Hoja nueva", onLimpiar)
                BotonBarra(if (panelAbierto) "▾" else "▴", true, if (panelAbierto) "Ocultar herramientas" else "Mostrar herramientas", onTogglePanel)
            }
        }
    }
}

@Composable
private fun BotonBarra(icono: String, activo: Boolean, etiqueta: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f))
            .then(if (activo) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics { contentDescription = etiqueta },
        contentAlignment = Alignment.Center,
    ) { Text(icono, fontSize = 16.sp, color = Color(0xFF3F342C).copy(alpha = if (activo) 1f else 0.35f)) }
}
