package com.miambiente.app.data

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Agrupa los servicios (persistencia, voz, sonido, vibración) en un solo
 * objeto, expuesto por CompositionLocal — el equivalente de tener stores
 * de zustand disponibles en cualquier componente sin pasarlos por props.
 */
class Services(context: Context) {
    val settings = SettingsStore(context)
    val progress = ProgressStore(context)
    val speech = Speech(context)
    val sound = SoundPlayer()
    val musica = AmbientMusic()
    val haptics = Haptics(context)
}

val LocalServices = staticCompositionLocalOf<Services> {
    error("Services no está provisto — envuelve la UI en CompositionLocalProvider(LocalServices provides ...)")
}
