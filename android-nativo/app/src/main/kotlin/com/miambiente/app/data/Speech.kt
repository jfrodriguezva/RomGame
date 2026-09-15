package com.miambiente.app.data

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Equivalente nativo de lib/speech.ts. La versión web depende de que el
 * navegador tenga voces en español instaladas (variable según el
 * dispositivo); `android.speech.tts.TextToSpeech` es parte del sistema
 * operativo, así que esa dependencia desaparece por completo en nativo.
 */
class Speech(context: Context) {
    private var listo = false
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context.applicationContext) { estado ->
            if (estado == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "MX")
                listo = true
            }
        }
    }

    fun hablar(texto: String, velocidad: Float = 1f) {
        if (!listo) return
        tts?.setSpeechRate(velocidad)
        tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun callar() {
        tts?.stop()
    }

    fun liberar() {
        tts?.shutdown()
    }
}
