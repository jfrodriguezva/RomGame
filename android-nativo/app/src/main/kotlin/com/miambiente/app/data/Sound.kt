package com.miambiente.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import kotlin.math.PI
import kotlin.math.exp
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
private val FRECUENCIAS_XILOFONO = doubleArrayOf(
    261.63, // Do4
    293.66, // Re4
    329.63, // Mi4
    392.00, // Sol4
    440.00, // La4
    523.25, // Do5
    587.33, // Re5
    659.25, // Mi5
)

/**
 * Sintetiza una barra de xilófono percutida: antes esto usaba tonos DTMF de
 * teléfono (bug real reportado: "sonidos de un xilófono", no un beep de
 * teclado). Una barra real tiene ataque casi instantáneo, decaimiento
 * exponencial rápido y un sobretono inarmónico (~2.76x la fundamental, la
 * razón real del primer modo de una barra libre-libre) — eso es lo que la
 * distingue de un tono puro y la hace sonar "de madera" en vez de "de
 * teléfono".
 */
private fun sintetizarNotaXilofono(frecuencia: Double, duracionMs: Int = 550): ShortArray {
    val muestras = (TASA_MUESTREO * duracionMs / 1000.0).toInt()
    val buffer = ShortArray(muestras)
    for (i in 0 until muestras) {
        val t = i / TASA_MUESTREO.toDouble()
        val envolvente = exp(-5.5 * t)
        val fundamental = sin(2 * PI * frecuencia * t)
        val sobretono = 0.32 * sin(2 * PI * frecuencia * 2.76 * t) * exp(-9.0 * t)
        val golpeInicial = if (t < 0.004) 0.4 * sin(2 * PI * frecuencia * 6 * t) * (1 - t / 0.004) else 0.0
        val muestra = envolvente * fundamental + sobretono + golpeInicial
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
        .setSessionId(AudioManager.AUDIO_SESSION_ID_GENERATE)
        .build()
        .apply { write(datos, 0, datos.size) }

class SoundPlayer {
    private val generador = ToneGenerator(AudioManager.STREAM_MUSIC, 70)

    // Una pista por nota (no una compartida): así dos teclas tocadas rápido
    // seguido pueden sonar superpuestas, como mallets reales, en vez de
    // cortarse una a la otra.
    private val pistasXilofono: List<AudioTrack> = FRECUENCIAS_XILOFONO.map { crearPistaEstatica(sintetizarNotaXilofono(it)) }

    /** Nota real de xilófono (barra percutida, sintetizada) — cada índice es un grado de la escala pentatónica. */
    fun tocarNota(indice: Int) {
        val pista = pistasXilofono[indice.coerceIn(0, pistasXilofono.size - 1)]
        pista.stop()
        pista.reloadStaticData()
        pista.play()
    }

    fun tocar(efecto: Efecto) {
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
        pistasXilofono.forEach { it.release() }
    }
}
