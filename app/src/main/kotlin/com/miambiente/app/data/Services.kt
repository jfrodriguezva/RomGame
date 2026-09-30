package com.miambiente.app.data

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Agrupa los servicios (persistencia, voz, sonido, vibración) en un solo
 * objeto, expuesto por CompositionLocal — el equivalente de tener stores
 * de zustand disponibles en cualquier componente sin pasarlos por props.
 */
class Services(context: Context) {
    // `tocar()`/`vibrar()`/`sonarPara()` no son funciones suspend — se
    // llaman desde cualquier lugar del código de cada material, no solo
    // desde composables — así que no pueden leer el Flow de ajustes
    // directamente. El colector de `init` mantiene una copia síncrona
    // (`activo`) al día en cada servicio, para que las pantallas de ajustes
    // (voz aparte, que sí vive en un composable) puedan apagar/encender
    // sonido, vibración y música de verdad.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val settings = SettingsStore(context, scope)
    val progress = ProgressStore(context, scope)
    val speech = Speech(context)
    val sound = SoundPlayer()
    val musica = AmbientMusic()
    val haptics = Haptics(context)

    init {
        scope.launch {
            settings.settings.collect { s ->
                sound.activo = s.sonido
                haptics.activo = s.vibracion
                musica.activo = s.musica
                // Modo calma: voz más lenta, música más baja, sin tono ni
                // vibración de error (el confeti lo apaga `GameShell`).
                speech.calma = s.calma
                sound.calma = s.calma
                haptics.calma = s.calma
                if (musica.calma != s.calma) musica.calma = s.calma
            }
        }
    }
}

val LocalServices = staticCompositionLocalOf<Services> {
    error("Services no está provisto — envuelve la UI en CompositionLocalProvider(LocalServices provides ...)")
}
