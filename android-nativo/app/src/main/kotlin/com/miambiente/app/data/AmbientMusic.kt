package com.miambiente.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.miambiente.app.theme.Area
import kotlin.math.PI
import kotlin.math.sin

/**
 * Fondo musical suave por área — pedido real: "agrega un fondo musical más
 * tierno por cada material". No hay 98 pistas distintas (eso necesitaría
 * archivos de audio reales, que este proyecto no tiene); en cambio cada una
 * de las 8 áreas Montessori tiene su propio acorde tierno, sintetizado con
 * `AudioTrack` igual que las notas del xilófono, así que el "ambiente" de
 * cada rincón de la app suena distinto sin depender de assets externos.
 */
private const val TASA_MUESTREO = 44100
private const val DURACION_LOOP_S = 6.0

// Acordes suaves, consonantes, sin semitonos que choquen — uno por área,
// pensados para sonar de fondo sin competir con la voz ni los efectos.
private val ACORDES: Map<Area, List<Double>> = mapOf(
    Area.PRACTICA to listOf(261.63, 329.63, 392.00), // Do mayor
    Area.SENSORIAL to listOf(293.66, 369.99, 440.00), // Re mayor
    Area.LENGUAJE to listOf(261.63, 329.63, 440.00), // Do con sexta
    Area.MATEMATICAS to listOf(220.00, 261.63, 329.63), // La menor
    Area.CULTURA to listOf(196.00, 246.94, 293.66), // Sol mayor
    Area.CREATIVA to listOf(293.66, 440.00, 587.33), // Re quinta + octava, aireado
    Area.COMPANIA to listOf(174.61, 220.00, 261.63), // Fa mayor
    Area.MOVIMIENTO to listOf(164.81, 196.00, 246.94), // Mi menor
)

/** Ajusta la frecuencia para que quepa un número entero de ciclos en el loop, sin chasquido al repetir. */
private fun frecuenciaLoopeable(frecuencia: Double): Double {
    val ciclos = Math.round(frecuencia * DURACION_LOOP_S).coerceAtLeast(1)
    return ciclos / DURACION_LOOP_S
}

private fun sintetizarPad(acorde: List<Double>): ShortArray {
    val muestras = (TASA_MUESTREO * DURACION_LOOP_S).toInt()
    val buffer = ShortArray(muestras)
    val frecuencias = acorde.map { frecuenciaLoopeable(it) }
    for (i in 0 until muestras) {
        val t = i / TASA_MUESTREO.toDouble()
        // Un solo respiro por vuelta del loop (perfectamente periódico):
        // sube y baja de volumen una vez, como una ola suave, en vez de un
        // zumbido plano sin vida.
        val respiro = 0.7 + 0.3 * sin(2 * PI * t / DURACION_LOOP_S - PI / 2)
        var muestra = 0.0
        frecuencias.forEachIndexed { indice, f ->
            val amplitud = 0.05 * (1.0 - indice * 0.18)
            muestra += amplitud * respiro * sin(2 * PI * f * t)
        }
        buffer[i] = (muestra.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
    }
    return buffer
}

class AmbientMusic {
    private val pistas: Map<Area, AudioTrack> = Area.entries.associateWith { area ->
        val datos = sintetizarPad(ACORDES.getValue(area))
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
            .apply {
                write(datos, 0, datos.size)
                setLoopPoints(0, datos.size, -1)
            }
    }
    private var actual: Area? = null

    /** Gatea desde Ajustes ("Música"); si se apaga a medio material, se detiene al toque. */
    var activo: Boolean = true
        set(valor) {
            field = valor
            if (!valor) detener()
        }

    /** Cambia el fondo musical al acorde del área dada; no repite si ya es la misma. */
    fun sonarPara(area: Area) {
        if (!activo || actual == area) return
        pistas[actual]?.pause()
        pistas[actual]?.let { it.stop(); it.reloadStaticData() }
        actual = area
        pistas[area]?.play()
    }

    fun detener() {
        pistas[actual]?.pause()
        pistas[actual]?.let { it.stop(); it.reloadStaticData() }
        actual = null
    }

    fun liberar() {
        pistas.values.forEach { it.release() }
    }
}
