package com.miambiente.app.data

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

/** Qué tan bien puede hablar el dispositivo; Ajustes lo muestra al adulto. */
enum class EstadoVoz { CARGANDO, LISTA, SIN_ESPANOL, SIN_MOTOR }

// De la preferida a la más general: si falta el español de México, cualquier
// otro español sirve mejor que quedarse mudo.
private val LOCALES_ESPANOL = listOf("es-MX", "es-US", "es-ES", "es").map(Locale::forLanguageTag)

/**
 * Voz de las consignas con el TextToSpeech del sistema. No todos los
 * dispositivos traen voces en español: antes se pedía es-MX sin revisar el
 * resultado y, si faltaba, la app quedaba muda sin avisar. Ahora prueba
 * varias variantes del español y publica el resultado en `estado`.
 */
class Speech(context: Context) {
    private var listo = false
    private var tts: TextToSpeech? = null
    private val _estado = MutableStateFlow(EstadoVoz.CARGANDO)
    val estado: StateFlow<EstadoVoz> = _estado

    /** Modo calma: habla más despacio. `Services` lo mantiene al día. */
    @Volatile var calma: Boolean = false

    init {
        tts = TextToSpeech(context.applicationContext) { resultado ->
            if (resultado != TextToSpeech.SUCCESS) {
                _estado.value = EstadoVoz.SIN_MOTOR
                return@TextToSpeech
            }
            if (!elegirEspanol()) {
                _estado.value = EstadoVoz.SIN_ESPANOL
                return@TextToSpeech
            }
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
            seleccionarMejorVoz()
            listo = true
            _estado.value = EstadoVoz.LISTA
        }
    }

    private fun elegirEspanol(): Boolean {
        val motor = tts ?: return false
        return LOCALES_ESPANOL.any { locale ->
            val r = motor.setLanguage(locale)
            r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
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
        tts?.setSpeechRate(if (calma) velocidad * 0.82f else velocidad)
        tts?.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun callar() {
        tts?.stop()
    }

    fun liberar() {
        tts?.shutdown()
    }
}
