package com.miambiente.app.data

import androidx.compose.runtime.staticCompositionLocalOf
import com.miambiente.app.theme.Area
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Versión para navegador de los servicios de la app. Tiene la misma forma
 * que la de Android (mismos nombres y métodos), así las pantallas
 * compartidas los usan sin cambios. Lo que cambia es por dentro:
 * localStorage en lugar de DataStore, WebAudio en lugar de AudioTrack,
 * Web Speech en lugar de TextToSpeech.
 */
class Services {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val settings = SettingsStore()
    val progress = ProgressStore()
    val speech = Speech()
    val sound = SoundPlayer()
    val musica = AmbientMusic()
    val haptics = Haptics()

    init {
        scope.launch {
            settings.settings.collect { s ->
                sound.activo = s.sonido
                haptics.activo = s.vibracion
                musica.activo = s.musica
                speech.calma = s.calma
                sound.calma = s.calma
                haptics.calma = s.calma
                musica.calma = s.calma
            }
        }
    }
}

val LocalServices = staticCompositionLocalOf<Services> {
    error("Services no está provisto — envuelve la UI en CompositionLocalProvider(LocalServices provides ...)")
}

// ------------------------------ Ajustes ------------------------------

const val NOMBRE_POR_DEFECTO = "Romina"

data class Settings(
    val edad: Int? = null,
    val nombre: String = NOMBRE_POR_DEFECTO,
    val sonido: Boolean = true,
    val voz: Boolean = true,
    val vibracion: Boolean = true,
    val musica: Boolean = true,
    val calma: Boolean = false,
)

class SettingsStore {
    private fun leer() = Settings(
        edad = almacenLeer("romina:edad")?.toIntOrNull()?.takeIf { it > 0 },
        nombre = almacenLeer("romina:nombre") ?: NOMBRE_POR_DEFECTO,
        sonido = almacenLeer("romina:sonido")?.toBoolean() ?: true,
        voz = almacenLeer("romina:voz")?.toBoolean() ?: true,
        vibracion = almacenLeer("romina:vibracion")?.toBoolean() ?: true,
        musica = almacenLeer("romina:musica")?.toBoolean() ?: true,
        calma = almacenLeer("romina:calma")?.toBoolean() ?: false,
    )

    private val estado = MutableStateFlow(leer())
    val settings: Flow<Settings> = estado

    /** En el navegador la lectura es inmediata: nunca queda en `null`. */
    val actual: StateFlow<Settings?> = estado

    private fun guardar(clave: String, valor: String) {
        almacenEscribir("romina:$clave", valor)
        estado.value = leer()
    }

    suspend fun setEdad(edad: Int) = guardar("edad", edad.toString())
    suspend fun setNombre(nombre: String) = guardar("nombre", nombre)
    suspend fun toggleSonido() = guardar("sonido", (!estado.value.sonido).toString())
    suspend fun toggleVoz() = guardar("voz", (!estado.value.voz).toString())
    suspend fun toggleVibracion() = guardar("vibracion", (!estado.value.vibracion).toString())
    suspend fun toggleMusica() = guardar("musica", (!estado.value.musica).toString())
    suspend fun toggleCalma() = guardar("calma", (!estado.value.calma).toString())
}

// ------------------------------ Progreso ------------------------------

data class GameProgress(
    val completados: Set<Int> = emptySet(),
    val nivelDesbloqueado: Int = 1,
    val estrellas: Int = 0,
    val vecesJugado: Int = 0,
    val dias: Set<Long> = emptySet(),
    val aciertos: Int = 0,
    val errores: Int = 0,
)

internal const val DIAS_GUARDADOS = 60

/** Mismas reglas que en Android: ver ProgressStore.kt de :app. */
class ProgressStore {
    private val prefijo = "romina:progreso:"
    private val datos = MutableStateFlow(cargar())

    private fun cargar(): Map<String, String> = buildMap {
        for (i in 0 until almacenCantidad()) {
            val clave = almacenClave(i) ?: continue
            if (clave.startsWith(prefijo)) almacenLeer(clave)?.let { put(clave.removePrefix(prefijo), it) }
        }
    }

    private fun escribir(cambios: Map<String, String>) {
        cambios.forEach { (k, v) -> almacenEscribir(prefijo + k, v) }
        datos.value = datos.value + cambios
    }

    val listo: StateFlow<Boolean> = MutableStateFlow(true)

    fun nivelGuardado(id: String, minimo: Int = 1): Int =
        maxOf(datos.value["${id}_nivel"]?.toIntOrNull() ?: 1, minimo).coerceIn(1, 100)

    private fun leer(p: Map<String, String>, id: String) = GameProgress(
        completados = leerCompletados(p["${id}_completados"]),
        nivelDesbloqueado = p["${id}_nivel"]?.toIntOrNull() ?: 1,
        estrellas = p["${id}_estrellas"]?.toIntOrNull() ?: 0,
        vecesJugado = p["${id}_veces"]?.toIntOrNull() ?: 0,
        dias = leerDias(p["${id}_dias"]),
        aciertos = p["${id}_aciertos"]?.toIntOrNull() ?: 0,
        errores = p["${id}_errores"]?.toIntOrNull() ?: 0,
    )

    fun progresoDe(id: String): Flow<GameProgress> = datos.map { leer(it, id) }

    fun resumen(ids: List<String>): Flow<Map<String, GameProgress>> = datos.map { p -> ids.associateWith { leer(p, it) } }

    suspend fun registrarJugada(id: String, hoy: Long = hoyEpoch()) {
        val p = datos.value
        escribir(
            mapOf(
                "${id}_veces" to ((p["${id}_veces"]?.toIntOrNull() ?: 0) + 1).toString(),
                "${id}_dias" to escribirDias(agregarDia(leerDias(p["${id}_dias"]), hoy)),
            ),
        )
    }

    suspend fun registrarRespuesta(id: String, acierto: Boolean) {
        val clave = if (acierto) "${id}_aciertos" else "${id}_errores"
        escribir(mapOf(clave to ((datos.value[clave]?.toIntOrNull() ?: 0) + 1).toString()))
    }

    suspend fun completarNivel(id: String, nivel: Int): Int {
        val p = datos.value
        val completados = leerCompletados(p["${id}_completados"]).toMutableSet()
        val primeraVez = completados.add(nivel)
        val ganadas = if (primeraVez) com.miambiente.app.model.estrellasPara(nivel) else 0
        escribir(
            mapOf(
                "${id}_completados" to completados.joinToString(","),
                "${id}_nivel" to nivelTrasCompletar(p["${id}_nivel"]?.toIntOrNull(), nivel).toString(),
                "${id}_estrellas" to ((p["${id}_estrellas"]?.toIntOrNull() ?: 0) + ganadas).toString(),
            ),
        )
        return ganadas
    }
}

internal fun leerCompletados(texto: String?): Set<Int> =
    texto.orEmpty().split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

internal fun nivelTrasCompletar(desbloqueado: Int?, nivel: Int): Int =
    maxOf(desbloqueado ?: 1, minOf(nivel + 1, 100))

internal fun hoyEpoch(): Long = diaLocalNavegador().toLong()

internal fun leerDias(texto: String?): Set<Long> =
    texto.orEmpty().split(",").mapNotNull { it.trim().toLongOrNull() }.toSet()

internal fun escribirDias(dias: Set<Long>): String = dias.sorted().joinToString(",")

internal fun agregarDia(dias: Set<Long>, hoy: Long): Set<Long> =
    (dias + hoy).filter { it > hoy - DIAS_GUARDADOS }.toSet()

// ------------------------------ Voz ------------------------------

enum class EstadoVoz { CARGANDO, LISTA, SIN_ESPANOL, SIN_MOTOR }

class Speech {
    private val _estado = MutableStateFlow(if (vozDisponible()) EstadoVoz.LISTA else EstadoVoz.SIN_MOTOR)
    val estado: StateFlow<EstadoVoz> = _estado
    var calma: Boolean = false

    fun hablar(texto: String, velocidad: Float = 0.95f) {
        if (_estado.value != EstadoVoz.LISTA) return
        hablarNavegador(texto, (if (calma) velocidad * 0.82f else velocidad).toDouble())
    }

    fun callar() = callarNavegador()
    fun liberar() = callarNavegador()
}

// ------------------------------ Sonido ------------------------------

enum class Efecto { CORRECT, WRONG, WIN, CLICK, STAR }

enum class InstrumentoSonoro { XILOFONO, PIANO, GUITARRA, FLAUTA, TROMPETA, ACORDEON, ARPA }

private val FRECUENCIAS_NOTAS = doubleArrayOf(261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88)

class SoundPlayer {
    var activo: Boolean = true
    var calma: Boolean = false

    fun tocarNota(indice: Int) = tocarInstrumento(InstrumentoSonoro.XILOFONO, indice)

    fun tocarInstrumento(instrumento: InstrumentoSonoro, nota: Int) {
        if (!activo) return
        val f = FRECUENCIAS_NOTAS[nota.coerceIn(0, FRECUENCIAS_NOTAS.lastIndex)]
        when (instrumento) {
            InstrumentoSonoro.XILOFONO -> { tonoNavegador(f, 700, "sine", 0.35, 0); tonoNavegador(f * 2.76, 250, "sine", 0.08, 0) }
            InstrumentoSonoro.PIANO -> { tonoNavegador(f, 1100, "triangle", 0.3, 0); tonoNavegador(f * 2, 600, "sine", 0.08, 0) }
            InstrumentoSonoro.GUITARRA -> tonoNavegador(f, 900, "sawtooth", 0.12, 0)
            InstrumentoSonoro.FLAUTA -> tonoNavegador(f * 2, 800, "sine", 0.25, 0)
            InstrumentoSonoro.TROMPETA -> tonoNavegador(f, 700, "square", 0.1, 0)
            InstrumentoSonoro.ACORDEON -> { tonoNavegador(f, 800, "square", 0.07, 0); tonoNavegador(f * 1.005, 800, "square", 0.07, 0) }
            InstrumentoSonoro.ARPA -> { tonoNavegador(f, 1200, "triangle", 0.28, 0); tonoNavegador(f * 2, 900, "sine", 0.06, 0) }
        }
    }

    fun tocar(efecto: Efecto) {
        if (!activo || (calma && efecto == Efecto.WRONG)) return
        when (efecto) {
            Efecto.CLICK -> tonoNavegador(880.0, 60, "sine", 0.15, 0)
            Efecto.CORRECT -> { tonoNavegador(660.0, 110, "sine", 0.22, 0); tonoNavegador(990.0, 140, "sine", 0.22, 90) }
            Efecto.WRONG -> tonoNavegador(196.0, 220, "triangle", 0.2, 0)
            Efecto.STAR -> { tonoNavegador(1046.5, 120, "sine", 0.2, 0); tonoNavegador(1318.5, 160, "sine", 0.2, 80) }
            Efecto.WIN -> listOf(523.25, 659.25, 783.99, 1046.5).forEachIndexed { i, f -> tonoNavegador(f, 220, "triangle", 0.22, i * 110) }
        }
    }

    fun liberar() {}
}

// ------------------------------ Música de fondo ------------------------------

private val ACORDES: Map<Area, List<Double>> = mapOf(
    Area.PRACTICA to listOf(261.63, 329.63, 392.00),
    Area.SENSORIAL to listOf(293.66, 369.99, 440.00),
    Area.LENGUAJE to listOf(261.63, 329.63, 440.00),
    Area.MATEMATICAS to listOf(220.00, 261.63, 329.63),
    Area.CULTURA to listOf(196.00, 246.94, 293.66),
    Area.CREATIVA to listOf(293.66, 440.00, 587.33),
    Area.COMPANIA to listOf(174.61, 220.00, 261.63),
    Area.MOVIMIENTO to listOf(164.81, 196.00, 246.94),
)

class AmbientMusic {
    var activo: Boolean = true
        set(valor) { field = valor; if (!valor) silenciarAcordeNavegador() }
    var calma: Boolean = false
    private var actual: Area? = null

    fun sonarPara(area: Area) {
        actual = area
        if (!activo) return
        acordeNavegador(ACORDES.getValue(area).joinToString(","), if (calma) 0.02 else 0.045)
    }

    fun detener() { actual = null; silenciarAcordeNavegador() }
    fun pausar() = silenciarAcordeNavegador()
    fun reanudar() { actual?.let { sonarPara(it) } }
}

// ------------------------------ Vibración ------------------------------

enum class Patron { TOQUE, ACIERTO, ERROR, LOGRO }

class Haptics {
    var activo: Boolean = true
    var calma: Boolean = false

    fun vibrar(patron: Patron) {
        if (!activo || (calma && patron == Patron.ERROR)) return
        vibrarNavegador(
            when (patron) {
                Patron.TOQUE -> "10"
                Patron.ACIERTO -> "12,40,18"
                Patron.ERROR -> "28"
                Patron.LOGRO -> "18,60,18,60,30"
            },
        )
    }
}
