package com.miambiente.app.data

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Voz local mediante `android.speech.tts.TextToSpeech`; no requiere red ni
 * archivos de audio incluidos en el APK.
 */
class Speech(context: Context) {
    private var listo = false
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context.applicationContext) { estado ->
            if (estado == TextToSpeech.SUCCESS) {
                tts?.language = Locale.Builder().setLanguage("es").setRegion("MX").build()
                // Bug real reportado: "voz menos robótica y más cálida y
                // amigable". El motor por defecto suena plano porque usa
                // la voz compacta de menor calidad y el tono/velocidad
                // neutros de fábrica. Un tono un poco más alto y una
                // velocidad un poco más pausada ya se sienten menos "de
                // GPS" y más cercanos a como un adulto le habla a un niño
                // pequeño — y se elige, de las voces instaladas, la de
                // mejor calidad que no dependa de datos móviles (para no
                // fallar en silencio sin conexión).
                tts?.setPitch(1.08f)
                tts?.setSpeechRate(0.95f)
                seleccionarMejorVoz()
                listo = true
            }
        }
    }

    private fun seleccionarMejorVoz() {
        val motor = tts ?: return
        val esVoces = motor.voices?.filter { it.locale.language == "es" } ?: return
        val mejor = esVoces.filter { !it.isNetworkConnectionRequired }.maxByOrNull { it.quality }
            ?: esVoces.maxByOrNull { it.quality }
        if (mejor != null) motor.voice = mejor
    }

    fun hablar(texto: String, velocidad: Float = 0.95f) {
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
