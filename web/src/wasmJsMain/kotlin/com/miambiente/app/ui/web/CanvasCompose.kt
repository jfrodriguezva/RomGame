package com.miambiente.app.ui.screens

import android.graphics.Paint
import android.graphics.Path
import android.graphics.circuloAndroid
import android.graphics.lineaAndroid
import android.graphics.textoAndroid
import android.graphics.trazoAndroid
import org.jetbrains.skia.Canvas

/*
 * La pizarra dibuja el fondo y las guías directo sobre el canvas de Compose
 * (`drawContext.canvas.nativeCanvas`). En Android ese canvas acepta Paint y
 * Path de Android; en la web es de Skia. Estas extensiones, en el mismo
 * paquete que PizarraScreen, le permiten usar el mismo código sin cambios.
 */
internal fun Canvas.drawLine(x0: Float, y0: Float, x1: Float, y1: Float, paint: Paint) = lineaAndroid(x0, y0, x1, y1, paint)
internal fun Canvas.drawCircle(x: Float, y: Float, r: Float, paint: Paint) = circuloAndroid(x, y, r, paint)
internal fun Canvas.drawText(texto: String, x: Float, y: Float, paint: Paint) = textoAndroid(texto, x, y, paint)
internal fun Canvas.drawPath(path: Path, paint: Paint) = trazoAndroid(path, paint)
