package androidx.core.graphics

// Compatibilidad web: las funciones de androidx.core.graphics que usa la
// pizarra. Ver android/graphics en Graficos.kt.

import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.crearBitmap
import org.jetbrains.skia.Canvas as SkCanvas
import org.jetbrains.skia.Path as SkPath

object PathParser {
    /** Mismo formato de "d" de SVG que usa Android. */
    fun createPathFromPathData(datos: String): Path = Path(SkPath.makeFromSVGString(datos))
}

fun createBitmap(ancho: Int, alto: Int): Bitmap = crearBitmap(ancho, alto)

inline fun SkCanvas.withTranslation(x: Float, y: Float, bloque: SkCanvas.() -> Unit) {
    val guardado = save()
    translate(x, y)
    try { bloque() } finally { restoreToCount(guardado) }
}

inline fun SkCanvas.withScale(x: Float, y: Float, bloque: SkCanvas.() -> Unit) {
    val guardado = save()
    scale(x, y)
    try { bloque() } finally { restoreToCount(guardado) }
}
