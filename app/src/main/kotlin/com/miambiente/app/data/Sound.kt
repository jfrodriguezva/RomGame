package com.miambiente.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sign
import kotlin.math.sin

/**
 * Equivalente nativo de lib/audio.ts. La versión web sintetiza tonos con
 * WebAudio porque no puede tocar hardware de audio real; en nativo,
 * `ToneGenerator` genera los mismos tonos cortos directo del sistema, sin
 * necesitar un `AudioContext` ni desbloquear audio con el primer toque
 * (una limitación real de los navegadores que aquí no existe).
 */
enum class Efecto { CORRECT, WRONG, WIN, CLICK, STAR }

private const val TASA_MUESTREO = 44100

// Escala pentatónica mayor (Do-Re-Mi-Sol-La): sin semitonos disonantes, así
// que cualquier combinación de teclas que un niño toque suena musical —
// nunca hay una "nota equivocada", como en un xilófono real de jardín.
private val FRECUENCIAS_NOTAS = doubleArrayOf(
    261.63, // Do4
    293.66, // Re4
    329.63, // Mi4
    349.23, // Fa4
    392.00, // Sol4
    440.00, // La4
    493.88, // Si4
)

enum class InstrumentoSonoro { XILOFONO, PIANO, GUITARRA, FLAUTA, TROMPETA, ACORDEON, ARPA }

/**
 * Sintetiza una barra de xilófono percutida: antes esto usaba tonos DTMF de
 * teléfono (bug real reportado: "sonidos de un xilófono", no un beep de
 * teclado). Una barra real tiene ataque casi instantáneo, decaimiento
 * exponencial rápido y un sobretono inarmónico (~2.76x la fundamental, la
 * razón real del primer modo de una barra libre-libre) — eso es lo que la
 * distingue de un tono puro y la hace sonar "de madera" en vez de "de
 * teléfono".
 */
private fun sintetizarInstrumento(instrumento: InstrumentoSonoro, frecuencia: Double, duracionMs: Int = 900): ShortArray {
    val muestras = (TASA_MUESTREO * duracionMs / 1000.0).toInt()
    val buffer = ShortArray(muestras)
    for (i in 0 until muestras) {
        val t = i / TASA_MUESTREO.toDouble()
        val fase = 2 * PI * frecuencia * t
        val ataque = (t / 0.018).coerceIn(0.0, 1.0)
        val muestra = when (instrumento) {
            InstrumentoSonoro.XILOFONO -> exp(-6.2 * t) * (sin(fase) + .34 * sin(fase * 2.76) * exp(-3 * t) + .15 * sin(fase * 5.4) * exp(-8 * t))
            InstrumentoSonoro.PIANO -> ataque * exp(-2.7 * t) * (sin(fase) + .48 * sin(fase * 2) + .22 * sin(fase * 3) + .10 * sin(fase * 4))
            InstrumentoSonoro.GUITARRA -> ataque * exp(-4.0 * t) * (sin(fase) + .52 * sin(fase * 2) + .28 * sin(fase * 3) + .14 * sin(fase * 4))
            InstrumentoSonoro.FLAUTA -> ataque * exp(-.65 * t) * (sin(fase) + .11 * sin(fase * 2) + .035 * sin(fase * 3))
            InstrumentoSonoro.TROMPETA -> ataque * exp(-.9 * t) * (sin(fase) + .62 * sin(fase * 2) + .38 * sin(fase * 3) + .23 * sin(fase * 4))
            InstrumentoSonoro.ACORDEON -> ataque * exp(-.75 * t) * (.72 * sign(sin(fase)) + .18 * sin(fase * 2) + .10 * sin(fase * 3)) * (.92 + .08 * sin(2 * PI * 5.2 * t))
            InstrumentoSonoro.ARPA -> ataque * exp(-3.25 * t) * (sin(fase) + .38 * sin(fase * 2) + .21 * sin(fase * 3) + .12 * sin(fase * 4))
        }
        buffer[i] = (muestra.coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.85).toInt().toShort()
    }
    return buffer
}

private fun crearPistaEstatica(datos: ShortArray): AudioTrack =
    AudioTrack.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
        )
        .setAudioFormat(
            AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(TASA_MUESTREO)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build(),
        )
        .setBufferSizeInBytes(datos.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()
        .apply { write(datos, 0, datos.size) }

class SoundPlayer {
    /** Gatea desde Ajustes ("Sonido"); `Services` lo mantiene al día. */
    @Volatile var activo: Boolean = true

    private val generador = ToneGenerator(AudioManager.STREAM_MUSIC, 70)

    // Una pista por nota (no una compartida): así dos teclas tocadas rápido
    // seguido pueden sonar superpuestas, como mallets reales, en vez de
    // cortarse una a la otra.
    // Se genera cada nota la primera vez que se toca. Precargar 49 buffers PCM
    // en el hilo principal hacía que Android mantuviera visible el splash
    // durante varios segundos antes de mostrar el menú.
    private val sonidosInstrumentos = Array(InstrumentoSonoro.entries.size) {
        arrayOfNulls<ShortArray>(FRECUENCIAS_NOTAS.size)
    }
    private var pistaInstrumento: AudioTrack? = null

    /** Nota real de xilófono (barra percutida, sintetizada) — cada índice es un grado de la escala pentatónica. */
    fun tocarNota(indice: Int) {
        tocarInstrumento(InstrumentoSonoro.XILOFONO, indice)
    }

    @Synchronized
    fun tocarInstrumento(instrumento: InstrumentoSonoro, nota: Int) {
        if (!activo) return
        val indice = nota.coerceIn(0, FRECUENCIAS_NOTAS.lastIndex)
        val sonido = sonidosInstrumentos[instrumento.ordinal][indice]
            ?: sintetizarInstrumento(instrumento, FRECUENCIAS_NOTAS[indice]).also {
                sonidosInstrumentos[instrumento.ordinal][indice] = it
            }
        pistaInstrumento?.run { stop(); release() }
        pistaInstrumento = crearPistaEstatica(sonido).also { it.play() }
    }

    fun tocar(efecto: Efecto) {
        if (!activo) return
        val tono = when (efecto) {
            Efecto.CLICK -> ToneGenerator.TONE_PROP_BEEP
            Efecto.CORRECT -> ToneGenerator.TONE_PROP_ACK
            Efecto.WRONG -> ToneGenerator.TONE_PROP_NACK
            Efecto.WIN -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
            Efecto.STAR -> ToneGenerator.TONE_PROP_PROMPT
        }
        generador.startTone(tono, 120)
    }

    fun liberar() {
        generador.release()
        pistaInstrumento?.run { stop(); release() }
        pistaInstrumento = null
    }
}
