package com.miambiente.app.ui.screens

import com.miambiente.app.data.ahoraMs

/**
 * `System.nanoTime()` es de la JVM y no existe en el navegador. Reflejo de
 * color lo usa para medir el tiempo de reacción; este objeto, del mismo
 * paquete, lo resuelve con `performance.now()` sin tocar esa pantalla.
 */
internal object System {
    fun nanoTime(): Long = (ahoraMs() * 1_000_000.0).toLong()
}
