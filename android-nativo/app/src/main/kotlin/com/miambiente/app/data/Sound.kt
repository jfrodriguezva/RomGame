package com.miambiente.app.data

import android.media.AudioManager
import android.media.ToneGenerator

/**
 * Equivalente nativo de lib/audio.ts. La versión web sintetiza tonos con
 * WebAudio porque no puede tocar hardware de audio real; en nativo,
 * `ToneGenerator` genera los mismos tonos cortos directo del sistema, sin
 * necesitar un `AudioContext` ni desbloquear audio con el primer toque
 * (una limitación real de los navegadores que aquí no existe).
 */
enum class Efecto { CORRECT, WRONG, WIN, CLICK, STAR }

class SoundPlayer {
    private val generador = ToneGenerator(AudioManager.STREAM_MUSIC, 70)

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
    }
}
