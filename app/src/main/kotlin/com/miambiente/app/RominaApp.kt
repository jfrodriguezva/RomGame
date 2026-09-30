package com.miambiente.app

import android.app.Application
import com.miambiente.app.data.Services

/**
 * Dueña única de los servicios (voz, sonido, música, vibración y
 * persistencia) durante toda la vida del proceso. Antes `MainActivity`
 * creaba un `Services` nuevo en cada `onCreate`, y como nadie los liberaba,
 * cada recreación de la Activity dejaba vivos otro TextToSpeech, otro
 * ToneGenerator y otras pistas de música, hasta agotar las pistas de audio
 * del sistema.
 */
class RominaApp : Application() {
    val services: Services by lazy { Services(this) }
}
