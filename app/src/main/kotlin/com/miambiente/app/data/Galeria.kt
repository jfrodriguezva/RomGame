package com.miambiente.app.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Galería de dibujos de la pizarra — equivalente nativo de la parte de
 * lib/pizarra.ts que usaba localStorage. Aquí se guardan PNG reales en el
 * almacenamiento privado de la app: sin el límite de tamaño de
 * localStorage (unos 5MB totales para todo lo demás también), y son
 * archivos de verdad que se pueden compartir por `FileProvider` sin
 * necesitar convertir data URLs.
 */
data class DibujoGuardado(val archivo: File, val fecha: Long)

private const val MAX_DIBUJOS = 12

private fun carpetaGaleria(context: Context): File =
    File(context.filesDir, "galeria").apply { mkdirs() }

fun leerGaleria(context: Context): List<DibujoGuardado> =
    carpetaGaleria(context).listFiles { f -> f.extension == "png" }
        ?.sortedByDescending { it.lastModified() }
        ?.map { DibujoGuardado(it, it.lastModified()) }
        ?: emptyList()

fun guardarEnGaleria(context: Context, bitmap: Bitmap): List<DibujoGuardado> {
    val carpeta = carpetaGaleria(context)
    val archivo = File(carpeta, "dibujo-${System.currentTimeMillis()}.png")
    FileOutputStream(archivo).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    val lista = leerGaleria(context)
    if (lista.size > MAX_DIBUJOS) {
        lista.drop(MAX_DIBUJOS).forEach { it.archivo.delete() }
    }
    return leerGaleria(context)
}

fun borrarDeGaleria(context: Context, archivo: File): List<DibujoGuardado> {
    archivo.delete()
    return leerGaleria(context)
}

/** URI compartible (content://) del archivo, vía el FileProvider declarado en el manifest. */
fun uriCompartible(context: Context, archivo: File) =
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

/** Compone el dibujo sobre un fondo sólido y lo guarda como PNG temporal listo para compartir. */
fun exportarParaCompartir(context: Context, bitmap: Bitmap, colorFondo: Int): File {
    val compuesto = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(compuesto)
    canvas.drawColor(colorFondo)
    canvas.drawBitmap(bitmap, 0f, 0f, null)
    val carpeta = File(context.cacheDir, "compartir").apply { mkdirs() }
    val archivo = File(carpeta, "mi-dibujo.png")
    FileOutputStream(archivo).use { compuesto.compress(Bitmap.CompressFormat.PNG, 100, it) }
    return archivo
}
