package com.miambiente.app.model

/** Igual que ui/web/Matematicas.kt, para el paquete model (DificultadJuegos). */
internal object Math {
    const val PI: Double = kotlin.math.PI
    fun toRadians(grados: Double): Double = grados * kotlin.math.PI / 180.0
    fun toDegrees(radianes: Double): Double = radianes * 180.0 / kotlin.math.PI
}
