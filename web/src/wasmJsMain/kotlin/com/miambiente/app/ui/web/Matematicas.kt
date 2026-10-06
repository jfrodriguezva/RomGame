package com.miambiente.app.ui.screens

/**
 * `Math` (java.lang.Math) es de la JVM y no existe en el navegador. Las
 * pantallas que lo usan lo resuelven con este objeto del mismo paquete,
 * sin cambiar su código. Ver también model/web/Matematicas.kt.
 */
internal object Math {
    const val PI: Double = kotlin.math.PI
    fun toRadians(grados: Double): Double = grados * kotlin.math.PI / 180.0
    fun toDegrees(radianes: Double): Double = radianes * 180.0 / kotlin.math.PI
}
