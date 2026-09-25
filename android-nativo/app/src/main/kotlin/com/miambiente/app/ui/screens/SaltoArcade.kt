package com.miambiente.app.ui.screens

/**
 * Física de salto compartida entre los arcades que esquivan saltando (Nieve,
 * Vaqueros) — antes cada screen mantenía su propio par `alturaSalto`/
 * `velocidadSalto` con la misma integración de gravedad copiada, solo con
 * distintas constantes. Un único lugar para el sube-baja parabólico.
 */
internal fun iniciarSalto(impulso: Float): Pair<Float, Float> = 1f to impulso

/** Un cuadro de gravedad sobre un salto en curso; `(0f, 0f)` si ya tocó el piso. */
internal fun avanzarSalto(alturaSalto: Float, velocidadSalto: Float, gravedad: Float, dt: Float): Pair<Float, Float> {
    if (alturaSalto <= 0f) return 0f to 0f
    val nuevaVelocidad = velocidadSalto - gravedad * dt
    val nuevaAltura = (alturaSalto + nuevaVelocidad * dt).coerceAtLeast(0f)
    return if (nuevaAltura <= 0f) 0f to 0f else nuevaAltura to nuevaVelocidad
}
