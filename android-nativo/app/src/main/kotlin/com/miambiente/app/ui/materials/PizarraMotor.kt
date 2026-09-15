package com.miambiente.app.ui.materials

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Puerto directo de lib/pizarra.ts — mismo comportamiento, Canvas de Android en vez de HTML canvas. */

enum class Herramienta { LAPIZ, CRAYON, MARCADOR, NEON, AEROSOL, BORRADOR, RELLENO, SELLO }

data class PuntoP(val x: Float, val y: Float, val presion: Float = 0.5f)

private fun jitter(n: Float) = (Random.nextFloat() - 0.5f) * n

/** Dibuja un segmento del trazo con la textura propia de cada herramienta. */
fun trazar(canvas: Canvas, a: PuntoP, b: PuntoP, herramienta: Herramienta, colorArgb: Int, grosor: Float) {
    val presion = 0.55f + ((a.presion + b.presion) / 2f) * 0.9f
    val ancho = grosor * presion

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    when (herramienta) {
        Herramienta.BORRADOR -> {
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            paint.strokeWidth = grosor * 2.4f
            canvas.drawLine(a.x, a.y, b.x, b.y, paint)
        }
        Herramienta.CRAYON -> {
            paint.color = colorArgb
            paint.alpha = (255 * 0.22f).toInt()
            val pasadas = maxOf(3, (ancho / 3).toInt())
            paint.strokeWidth = maxOf(1f, ancho / 2.2f)
            repeat(pasadas) {
                canvas.drawLine(
                    a.x + jitter(ancho * 0.7f), a.y + jitter(ancho * 0.7f),
                    b.x + jitter(ancho * 0.7f), b.y + jitter(ancho * 0.7f),
                    paint,
                )
            }
        }
        Herramienta.MARCADOR -> {
            paint.color = colorArgb
            paint.alpha = (255 * 0.55f).toInt()
            paint.strokeWidth = ancho * 1.8f
            canvas.drawLine(a.x, a.y, b.x, b.y, paint)
        }
        Herramienta.NEON -> {
            paint.color = colorArgb
            paint.strokeWidth = ancho * 0.8f
            paint.setShadowLayer(ancho * 2.2f, 0f, 0f, colorArgb)
            canvas.drawLine(a.x, a.y, b.x, b.y, paint)
            val nucleo = Paint(paint).apply {
                color = android.graphics.Color.WHITE
                alpha = (255 * 0.75f).toInt()
                strokeWidth = maxOf(1f, ancho * 0.28f)
                clearShadowLayer()
            }
            canvas.drawLine(a.x, a.y, b.x, b.y, nucleo)
        }
        Herramienta.AEROSOL -> {
            val relleno = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colorArgb; style = Paint.Style.FILL }
            val radio = ancho * 1.6f
            val gotas = (radio * 1.6f).toInt()
            repeat(gotas) {
                val ang = Random.nextFloat() * PI.toFloat() * 2
                val dist = Random.nextFloat() * radio
                relleno.alpha = ((0.16f + Random.nextFloat() * 0.2f) * 255).toInt()
                canvas.drawCircle(b.x + cos(ang) * dist, b.y + sin(ang) * dist, 1.1f, relleno)
            }
        }
        else -> {
            paint.color = colorArgb
            paint.strokeWidth = ancho
            canvas.drawLine(a.x, a.y, b.x, b.y, paint)
        }
    }
}

fun sellar(canvas: Canvas, p: PuntoP, emoji: String, tamano: Float) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = tamano
        textAlign = Paint.Align.CENTER
    }
    val metrics = paint.fontMetrics
    val yCentrado = p.y - (metrics.ascent + metrics.descent) / 2
    canvas.drawText(emoji, p.x, yCentrado, paint)
}

/**
 * Relleno tipo cubeta con tolerancia — mismo algoritmo de líneas
 * (span flood fill) que la versión web, sobre un `Bitmap` mutable.
 */
fun rellenar(bitmap: Bitmap, x: Int, y: Int, colorArgb: Int, tolerancia: Int = 40) {
    val w = bitmap.width
    val h = bitmap.height
    if (x < 0 || y < 0 || x >= w || y >= h) return

    val pixeles = IntArray(w * h)
    bitmap.getPixels(pixeles, 0, w, 0, 0, w, h)

    fun canal(color: Int, corrimiento: Int) = (color shr corrimiento) and 0xFF
    val base = pixeles[y * w + x]
    if (base == colorArgb) return

    fun igual(color: Int): Boolean =
        abs(canal(color, 16) - canal(base, 16)) <= tolerancia &&
            abs(canal(color, 8) - canal(base, 8)) <= tolerancia &&
            abs(canal(color, 0) - canal(base, 0)) <= tolerancia &&
            abs(canal(color, 24) - canal(base, 24)) <= tolerancia

    val pila = ArrayDeque<Pair<Int, Int>>()
    pila.addLast(x to y)
    while (pila.isNotEmpty()) {
        val (px, py) = pila.removeLast()
        var izq = px
        while (izq >= 0 && igual(pixeles[py * w + izq])) izq--
        izq++
        var der = px
        while (der < w && igual(pixeles[py * w + der])) der++
        der--

        var spanArriba = false
        var spanAbajo = false
        for (i in izq..der) {
            pixeles[py * w + i] = colorArgb
            if (py > 0) {
                val dentro = igual(pixeles[(py - 1) * w + i])
                if (dentro && !spanArriba) { pila.addLast(i to (py - 1)); spanArriba = true }
                else if (!dentro) spanArriba = false
            }
            if (py < h - 1) {
                val dentro = igual(pixeles[(py + 1) * w + i])
                if (dentro && !spanAbajo) { pila.addLast(i to (py + 1)); spanAbajo = true }
                else if (!dentro) spanAbajo = false
            }
        }
    }
    bitmap.setPixels(pixeles, 0, w, 0, 0, w, h)
}

/**
 * Aplica `pintar` tantas veces como ejes tenga la simetría activa (modo
 * mandala): rotación alrededor del centro, más un espejo horizontal por eje
 * cuando hay más de uno — igual que `conSimetria` en la versión web.
 */
fun conSimetria(anchoLienzo: Int, altoLienzo: Int, simetria: Int, a: PuntoP, b: PuntoP, pintar: (PuntoP, PuntoP) -> Unit) {
    val cx = anchoLienzo / 2f
    val cy = altoLienzo / 2f
    fun rotar(p: PuntoP, ang: Float): PuntoP {
        val dx = p.x - cx
        val dy = p.y - cy
        return PuntoP(cx + dx * cos(ang) - dy * sin(ang), cy + dx * sin(ang) + dy * cos(ang), p.presion)
    }
    for (i in 0 until simetria) {
        val ang = (PI.toFloat() * 2 * i) / simetria
        val ra = rotar(a, ang)
        val rb = rotar(b, ang)
        pintar(ra, rb)
        if (simetria > 1) {
            pintar(ra.copy(x = anchoLienzo - ra.x), rb.copy(x = anchoLienzo - rb.x))
        }
    }
}
