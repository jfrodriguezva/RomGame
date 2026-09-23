package com.miambiente.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import com.rominagame.core.StudioInstrument
import com.rominagame.core.studioNotes
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

/** Síntesis aditiva con envolvente propia para cada instrumento. */
private fun sintetizarInstrumento(instrumento: StudioInstrument, frecuencia: Double, duracionMs: Int = 900): ShortArray {
    val muestras = (TASA_MUESTREO * duracionMs / 1000.0).toInt()
    val buffer = ShortArray(muestras)
    for (i in 0 until muestras) {
        val t = i / TASA_MUESTREO.toDouble()
        val phase = 2 * PI * frecuencia * t
        val attack = (t / 0.018).coerceIn(0.0, 1.0)
        val muestra = when (instrumento) {
            StudioInstrument.XYLOPHONE -> exp(-6.2*t)*(sin(phase)+.34*sin(phase*2.76)*exp(-3*t)+.15*sin(phase*5.4)*exp(-8*t))
            StudioInstrument.PIANO -> attack*exp(-2.7*t)*(sin(phase)+.48*sin(phase*2)+.22*sin(phase*3)+.10*sin(phase*4))
            StudioInstrument.GUITAR -> attack*exp(-4.0*t)*(sin(phase)+.52*sin(phase*2)+.28*sin(phase*3)+.14*sin(phase*4))
            StudioInstrument.FLUTE -> attack*exp(-.65*t)*(sin(phase)+.11*sin(phase*2)+.035*sin(phase*3)+.018*sin(phase*5))
            StudioInstrument.TRUMPET -> attack*exp(-.9*t)*(sin(phase)+.62*sin(phase*2)+.38*sin(phase*3)+.23*sin(phase*4)+.12*sin(phase*5))
            StudioInstrument.ACCORDION -> attack*exp(-.75*t)*(.72*sign(sin(phase))+.18*sin(phase*2)+.10*sin(phase*3))*(.92+.08*sin(2*PI*5.2*t))
            StudioInstrument.HARP -> attack*exp(-3.25*t)*(sin(phase)+.38*sin(phase*2)+.21*sin(phase*3)+.12*sin(phase*4)+.06*sin(phase*5))
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

    private val sonidosInstrumentos: List<List<ShortArray>> = StudioInstrument.entries.map { instrumento ->
        studioNotes.map { nota -> sintetizarInstrumento(instrumento, nota.frequency) }
    }
    private var pistaInstrumento: AudioTrack? = null

    /** Nota natural de xilófono (barra percutida y sintetizada). */
    fun tocarNota(indice: Int) {
        if (!activo) return
        tocarInstrumento(StudioInstrument.XYLOPHONE.ordinal, indice)
    }

    /** Siete notas naturales con un timbre y envolvente distintos por instrumento. */
    @Synchronized fun tocarInstrumento(instrumento: Int, nota: Int) {
        if (!activo) return
        val banco = sonidosInstrumentos[instrumento.coerceIn(0, sonidosInstrumentos.lastIndex)]
        val sonido = banco[nota.coerceIn(0, banco.lastIndex)]
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
