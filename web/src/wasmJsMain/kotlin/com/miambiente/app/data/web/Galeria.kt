package com.miambiente.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.bitmapDesdeImagen
import android.graphics.crearBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Galería y borrador de la pizarra en el navegador: mismas funciones que
 * Galeria.kt del APK, pero los dibujos se guardan en localStorage (como
 * imágenes codificadas en base64) y "compartir" descarga el archivo.
 */

/** Lo que en el APK es un archivo; aquí, una clave de localStorage. */
data class ArchivoWeb(val absolutePath: String, val formato: String = "jpg")

data class DibujoGuardado(val archivo: ArchivoWeb, val fecha: Long)

private const val MAX_DIBUJOS = 12
private const val INDICE = "romina:galeria"
private const val BORRADOR = "romina:borrador"

private val exportados = mutableMapOf<String, String>()

@OptIn(ExperimentalEncodingApi::class)
private fun codificar(bitmap: Bitmap, formato: EncodedImageFormat, calidad: Int): String? =
    Image.makeFromBitmap(bitmap.sk.makeClone().apply { setImmutable() }).encodeToData(formato, calidad)?.bytes?.let { Base64.encode(it) }

@OptIn(ExperimentalEncodingApi::class)
private fun decodificar(base64: String): Bitmap? =
    runCatching { bitmapDesdeImagen(Image.makeFromEncoded(Base64.decode(base64))) }.getOrNull()

private fun ids(): List<Long> = almacenLeer(INDICE).orEmpty().split(",").mapNotNull { it.toLongOrNull() }

/** Usado por BitmapFactory.decodeFile: lee la imagen guardada bajo esa clave. */
fun leerImagenGuardada(clave: String): Bitmap? = almacenLeer(clave)?.let(::decodificar)

fun leerGaleria(context: Context): List<DibujoGuardado> =
    ids().sortedDescending().map { DibujoGuardado(ArchivoWeb("romina:galeria:$it"), it) }

fun guardarEnGaleria(context: Context, bitmap: Bitmap): List<DibujoGuardado> {
    val id = ahoraMsEpoch()
    codificar(bitmap, EncodedImageFormat.JPEG, 88)?.let { almacenEscribir("romina:galeria:$id", it) }
    val todos = (ids() + id).sortedDescending()
    todos.drop(MAX_DIBUJOS).forEach { almacenBorrar("romina:galeria:$it") }
    almacenEscribir(INDICE, todos.take(MAX_DIBUJOS).joinToString(","))
    return leerGaleria(context)
}

fun leerMiniatura(archivo: ArchivoWeb, anchoMax: Int): Bitmap? = leerImagenGuardada(archivo.absolutePath)

fun borrarDeGaleria(context: Context, archivo: ArchivoWeb): List<DibujoGuardado> {
    almacenBorrar(archivo.absolutePath)
    almacenEscribir(INDICE, ids().filter { "romina:galeria:$it" != archivo.absolutePath }.joinToString(","))
    return leerGaleria(context)
}

fun uriCompartible(context: Context, archivo: ArchivoWeb): ArchivoWeb = archivo

fun componerConFondo(bitmap: Bitmap, colorFondo: Int): Bitmap {
    val salida = crearBitmap(bitmap.width, bitmap.height)
    android.graphics.Canvas(salida).apply {
        drawColor(colorFondo, android.graphics.PorterDuff.Mode.SRC)
        drawBitmap(bitmap, 0f, 0f, null)
    }
    return salida
}

fun exportarParaCompartir(context: Context, bitmap: Bitmap, colorFondo: Int): ArchivoWeb {
    val datos = codificar(componerConFondo(bitmap, colorFondo), EncodedImageFormat.PNG, 100).orEmpty()
    exportados["mi-dibujo.png"] = datos
    return ArchivoWeb("mi-dibujo.png", "png")
}

/** "Compartir" en el navegador: descarga la imagen exportada. */
fun descargarArchivo(archivo: ArchivoWeb) {
    val datos = exportados[archivo.absolutePath] ?: almacenLeer(archivo.absolutePath) ?: return
    descargarNavegador(datos, archivo.absolutePath.substringAfterLast(':'), if (archivo.formato == "png") "image/png" else "image/jpeg")
}

fun guardarBorrador(context: Context, bitmap: Bitmap) {
    codificar(bitmap, EncodedImageFormat.PNG, 100)?.let { almacenEscribir(BORRADOR, it) }
}

fun leerBorrador(context: Context): Bitmap? = leerImagenGuardada(BORRADOR)
