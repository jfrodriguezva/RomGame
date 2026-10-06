package android.graphics

/*
 * Compatibilidad para la versión web: el subconjunto de android.graphics
 * que usa la pizarra (PizarraScreen.kt y PizarraMotor.kt), implementado
 * sobre Skia, el mismo motor de dibujo que usa Android por dentro. Así la
 * pizarra del APK se compila para el navegador sin cambiar ni una línea.
 * Solo existe en el módulo :web; el APK usa las clases reales de Android.
 */

import org.jetbrains.skia.BlendMode
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Font
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.PaintStrokeCap
import org.jetbrains.skia.PaintStrokeJoin
import org.jetbrains.skia.Image as SkImage
import org.jetbrains.skia.Paint as SkPaint
import org.jetbrains.skia.Path as SkPath
import org.jetbrains.skia.Rect as SkRect
import org.jetbrains.skia.Typeface as SkTypeface
import org.jetbrains.skia.Bitmap as SkBitmap
import org.jetbrains.skia.Canvas as SkCanvas

object Color {
    const val WHITE: Int = -0x1
    const val BLACK: Int = -0x1000000
}

class Typeface private constructor(val negrita: Boolean) {
    companion object {
        val DEFAULT = Typeface(false)
        val DEFAULT_BOLD = Typeface(true)
    }
}

/** Fuentes que Skia necesita para dibujar texto en la web (las carga Main.kt). */
object FuentesWeb {
    var latina: SkTypeface? = null
    var emoji: SkTypeface? = null

    internal fun para(texto: String): SkTypeface? =
        if (texto.any { it.isSurrogate() || it.code >= 0x2190 }) emoji ?: latina else latina ?: emoji
}

open class Xfermode
class PorterDuffXfermode(val modo: PorterDuff.Mode) : Xfermode()

object PorterDuff {
    enum class Mode { CLEAR, SRC, SRC_OVER }
}

open class PathEffect
class DashPathEffect(val intervalos: FloatArray, val fase: Float) : PathEffect()

class Path internal constructor(internal val sk: SkPath)

class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int)

class Paint() {
    constructor(flags: Int) : this() {
        isAntiAlias = flags and ANTI_ALIAS_FLAG != 0
    }

    constructor(otro: Paint) : this() {
        isAntiAlias = otro.isAntiAlias; style = otro.style; strokeCap = otro.strokeCap; strokeJoin = otro.strokeJoin
        strokeWidth = otro.strokeWidth; colorBase = otro.colorBase; alpha = otro.alpha; xfermode = otro.xfermode
        textSize = otro.textSize; textAlign = otro.textAlign; typeface = otro.typeface; pathEffect = otro.pathEffect
        sombra = otro.sombra
    }

    companion object {
        const val ANTI_ALIAS_FLAG = 1
    }

    enum class Style { FILL, STROKE, FILL_AND_STROKE }
    enum class Cap { BUTT, ROUND, SQUARE }
    enum class Join { MITER, ROUND, BEVEL }
    enum class Align { LEFT, CENTER, RIGHT }

    class FontMetrics(val ascent: Float, val descent: Float)

    var isAntiAlias = false
    var style = Style.FILL
    var strokeCap = Cap.BUTT
    var strokeJoin = Join.MITER
    var strokeWidth = 0f
    private var colorBase = Color.BLACK
    var alpha = 255
    var xfermode: Xfermode? = null
    var textSize = 12f
    var textAlign = Align.LEFT
    var typeface: Typeface? = null
    var pathEffect: PathEffect? = null
    private var sombra: Pair<Float, Int>? = null

    /** Igual que en Android: fijar el color también fija su alfa. */
    var color: Int
        get() = (alpha shl 24) or (colorBase and 0xFFFFFF)
        set(valor) { colorBase = valor; alpha = (valor ushr 24) and 0xFF }

    fun setShadowLayer(radio: Float, dx: Float, dy: Float, colorSombra: Int) { sombra = radio to colorSombra }
    fun clearShadowLayer() { sombra = null }

    val fontMetrics: FontMetrics
        get() = fuente("Ag").metrics.let { FontMetrics(it.ascent, it.descent) }

    internal fun fuente(texto: String): Font {
        val tf = FuentesWeb.para(texto)
        return if (tf != null) Font(tf, textSize) else Font(null, textSize)
    }

    internal fun aSkia(): SkPaint = SkPaint().also { p ->
        p.isAntiAlias = isAntiAlias
        p.mode = when (style) { Style.FILL -> PaintMode.FILL; Style.STROKE -> PaintMode.STROKE; Style.FILL_AND_STROKE -> PaintMode.STROKE_AND_FILL }
        p.strokeCap = when (strokeCap) { Cap.BUTT -> PaintStrokeCap.BUTT; Cap.ROUND -> PaintStrokeCap.ROUND; Cap.SQUARE -> PaintStrokeCap.SQUARE }
        p.strokeJoin = when (strokeJoin) { Join.MITER -> PaintStrokeJoin.MITER; Join.ROUND -> PaintStrokeJoin.ROUND; Join.BEVEL -> PaintStrokeJoin.BEVEL }
        p.strokeWidth = strokeWidth
        p.color = color
        (xfermode as? PorterDuffXfermode)?.let { if (it.modo == PorterDuff.Mode.CLEAR) p.blendMode = BlendMode.CLEAR }
        (pathEffect as? DashPathEffect)?.let { p.pathEffect = org.jetbrains.skia.PathEffect.makeDash(it.intervalos, it.fase) }
        sombra?.let { (radio, c) -> p.imageFilter = ImageFilter.makeDropShadow(0f, 0f, radio / 2f, radio / 2f, c) }
    }
}

class Bitmap internal constructor(internal val sk: SkBitmap) {
    enum class Config { ARGB_8888 }

    val width: Int get() = sk.width
    val height: Int get() = sk.height

    fun copy(config: Config, mutable: Boolean): Bitmap {
        val nuevo = crearBitmap(width, height)
        Canvas(nuevo).drawBitmap(this, 0f, 0f, null)
        return nuevo
    }

    /** Pixeles ARGB sin premultiplicar, como en Android. */
    fun getPixels(destino: IntArray, offset: Int, stride: Int, x: Int, y: Int, w: Int, h: Int) {
        val info = ImageInfo(width, height, ColorType.BGRA_8888, ColorAlphaType.UNPREMUL)
        val bytes = sk.readPixels(info, width * 4, 0, 0) ?: return
        for (fila in 0 until h) for (col in 0 until w) {
            val i = ((y + fila) * width + (x + col)) * 4
            val b = bytes[i].toInt() and 0xFF
            val g = bytes[i + 1].toInt() and 0xFF
            val r = bytes[i + 2].toInt() and 0xFF
            val a = bytes[i + 3].toInt() and 0xFF
            destino[offset + fila * stride + col] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
    }

    fun setPixels(origen: IntArray, offset: Int, stride: Int, x: Int, y: Int, w: Int, h: Int) {
        val bytes = ByteArray(w * h * 4)
        for (fila in 0 until h) for (col in 0 until w) {
            val c = origen[offset + fila * stride + col]
            val i = (fila * w + col) * 4
            bytes[i] = (c and 0xFF).toByte()
            bytes[i + 1] = ((c shr 8) and 0xFF).toByte()
            bytes[i + 2] = ((c shr 16) and 0xFF).toByte()
            bytes[i + 3] = ((c ushr 24) and 0xFF).toByte()
        }
        val imagen = SkImage.makeRaster(ImageInfo(w, h, ColorType.BGRA_8888, ColorAlphaType.UNPREMUL), bytes, w * 4)
        SkCanvas(sk).drawImage(imagen, x.toFloat(), y.toFloat(), SkPaint().apply { blendMode = BlendMode.SRC })
    }

    internal fun imagen(): SkImage = SkImage.makeFromBitmap(sk.makeClone().apply { setImmutable() })
}

internal fun crearBitmap(ancho: Int, alto: Int): Bitmap =
    Bitmap(SkBitmap().apply { allocN32Pixels(ancho, alto, false); erase(0) })

internal fun bitmapDesdeImagen(img: SkImage): Bitmap {
    val b = crearBitmap(img.width, img.height)
    SkCanvas(b.sk).drawImage(img, 0f, 0f)
    return b
}

class Canvas(bitmap: Bitmap) {
    internal val sk = SkCanvas(bitmap.sk)

    fun drawLine(x0: Float, y0: Float, x1: Float, y1: Float, paint: Paint) = sk.lineaAndroid(x0, y0, x1, y1, paint)
    fun drawCircle(x: Float, y: Float, r: Float, paint: Paint) = sk.circuloAndroid(x, y, r, paint)
    fun drawText(texto: String, x: Float, y: Float, paint: Paint) = sk.textoAndroid(texto, x, y, paint)
    fun drawPath(path: Path, paint: Paint) = sk.trazoAndroid(path, paint)

    fun drawColor(color: Int, modo: PorterDuff.Mode) {
        if (modo == PorterDuff.Mode.CLEAR) sk.clear(0) else sk.drawPaint(SkPaint().apply { this.color = color })
    }

    fun drawBitmap(bitmap: Bitmap, x: Float, y: Float, paint: Paint?) {
        sk.drawImage(bitmap.imagen(), x, y, paint?.aSkia())
    }

    fun drawBitmap(bitmap: Bitmap, origen: Rect?, destino: Rect, paint: Paint?) {
        val img = bitmap.imagen()
        val src = origen?.let { SkRect.makeLTRB(it.left.toFloat(), it.top.toFloat(), it.right.toFloat(), it.bottom.toFloat()) }
            ?: SkRect.makeWH(img.width.toFloat(), img.height.toFloat())
        val dst = SkRect.makeLTRB(destino.left.toFloat(), destino.top.toFloat(), destino.right.toFloat(), destino.bottom.toFloat())
        sk.drawImageRect(img, src, dst, paint?.aSkia())
    }

    fun save(): Int = sk.save()
    fun restore() = sk.restore()
    fun translate(dx: Float, dy: Float) { sk.translate(dx, dy) }
    fun scale(sx: Float, sy: Float) { sk.scale(sx, sy) }
}

// Dibujo sobre un canvas de Skia con el Paint/Path de Android. Lo usa este
// Canvas y, vía ui/web/CanvasCompose.kt, la pizarra cuando dibuja directo
// sobre el canvas de Compose (`nativeCanvas`), que en la web es de Skia.
internal fun SkCanvas.lineaAndroid(x0: Float, y0: Float, x1: Float, y1: Float, paint: Paint) { drawLine(x0, y0, x1, y1, paint.aSkia()) }
internal fun SkCanvas.circuloAndroid(x: Float, y: Float, r: Float, paint: Paint) { drawCircle(x, y, r, paint.aSkia()) }
internal fun SkCanvas.trazoAndroid(path: Path, paint: Paint) { drawPath(path.sk, paint.aSkia()) }
internal fun SkCanvas.textoAndroid(texto: String, x: Float, y: Float, paint: Paint) {
    val fuente = paint.fuente(texto)
    val ancho = fuente.measureTextWidth(texto)
    val inicio = when (paint.textAlign) { Paint.Align.LEFT -> x; Paint.Align.CENTER -> x - ancho / 2f; Paint.Align.RIGHT -> x - ancho }
    drawString(texto, inicio, y, fuente, paint.aSkia())
}

object BitmapFactory {
    /** En la web los "archivos" de la galería viven en localStorage. */
    fun decodeFile(ruta: String): Bitmap? = com.miambiente.app.data.leerImagenGuardada(ruta)
}
