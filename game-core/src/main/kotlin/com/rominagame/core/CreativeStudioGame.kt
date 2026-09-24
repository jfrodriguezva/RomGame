package com.rominagame.core

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

interface CreativePlatform {
    fun playInstrument(instrument: Int, note: Int)
    fun shareDrawing(absolutePath: String)
}

private object NoCreativePlatform : CreativePlatform {
    override fun playInstrument(instrument: Int, note: Int) = Unit
    override fun shareDrawing(absolutePath: String) = Unit
}

class CreativeStudioGame(
    private val platform: CreativePlatform = NoCreativePlatform,
    private val onComplete: (String, Int, Int) -> Unit = { _, _, _ -> },
) : ApplicationAdapter() {
    private enum class Screen { MODES, DRAW, MUSIC, SIMPLE }
    private val viewport = FitViewport(960f, 540f)
    private lateinit var shapes: ShapeRenderer
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var canvas: Pixmap
    private lateinit var texture: Texture
    private var canvasDirty = true
    private var screen = Screen.MODES
    private var simpleMode = 1
    private var tool = DrawingTool.CRAYON
    private var colorIndex = 1
    private var widthIndex = 1
    private var symmetryIndex = 0
    private var backgroundIndex = 0
    private var guideIndex = 0
    private var stampIndex = 0
    private var instrument = StudioInstrument.XYLOPHONE
    private var drawing = false
    private var lastPoint = 0 to 0
    private var feedback = "Elige una modalidad"
    private val undo = ArrayDeque<Pixmap>()
    private val redo = ArrayDeque<Pixmap>()
    private var galleryOpen = false
    private var galleryIndex = 0
    private var galleryFiles = emptyList<com.badlogic.gdx.files.FileHandle>()

    private val canvasX = 60f
    private val canvasY = 120f
    private val canvasW = 840f
    private val canvasH = 330f
    private val pixmapW = 1260
    private val pixmapH = 495
    private val backgrounds = listOf("Papel", "Blanco", "Cuadros", "Renglones", "Puntos", "Pizarrón", "Kraft")
    private val guides = listOf("Ninguna", "A", "a", "1", "2", "Círculo", "Cuadrado", "Triángulo", "Estrella", "Corazón")
    private val stamps = listOf("Estrella", "Flor", "Sol", "Corazón", "Nube", "Mariposa")
    private val modeNames = listOf("Pizarra", "Colorear", "Collage", "Xilófono", "Instrumentos", "Patrones")

    override fun create() {
        shapes = ShapeRenderer()
        batch = SpriteBatch()
        font = GameTypography.create(20)
        canvas = Pixmap(pixmapW, pixmapH, Pixmap.Format.RGBA8888).apply { setColor(0f, 0f, 0f, 0f); fill() }
        texture = Texture(canvas)
        refreshGallery()
        Gdx.input.inputProcessor = object : InputAdapter() {
            override fun touchDown(x: Int, y: Int, pointer: Int, button: Int): Boolean {
                val p = viewport.unproject(Vector3(x.toFloat(), y.toFloat(), 0f))
                return down(p.x, p.y)
            }
            override fun touchDragged(x: Int, y: Int, pointer: Int): Boolean {
                if (!drawing || screen != Screen.DRAW) return false
                val p = viewport.unproject(Vector3(x.toFloat(), y.toFloat(), 0f))
                drawTo(p.x, p.y)
                return true
            }
            override fun touchUp(x: Int, y: Int, pointer: Int, button: Int): Boolean { drawing = false; return true }
        }
    }

    private fun down(x: Float, y: Float): Boolean {
        if (screen == Screen.MODES) {
            modeNames.indices.forEach { i ->
                val bx = 90f + (i % 3) * 270f
                val by = 350f - (i / 3) * 115f
                if (x in bx..bx + 235f && y in by..by + 85f) openMode(i)
            }
            return true
        }
        if (x < 95f && y > 485f) { screen = Screen.MODES; galleryOpen = false; return true }
        if (screen == Screen.MUSIC) return musicDown(x, y)
        if (screen == Screen.SIMPLE) return simpleDown(x, y)
        if (galleryOpen) return galleryDown(x, y)
        if (y > 485f) {
            when {
                x < 205f -> undo()
                x < 315f -> redo()
                x < 425f -> saveDrawing()
                x < 535f -> shareDrawing()
                x < 645f -> clearCanvas()
                x < 755f -> { refreshGallery(); galleryOpen = true }
            }
            return true
        }
        if (y in 68f..112f) {
            val index = ((x - 8f) / 105f).toInt()
            if (index in DrawingTool.entries.indices) tool = DrawingTool.entries[index]
            return true
        }
        if (y in 15f..60f) {
            when {
                x < 520f -> colorIndex = ((x - 8f) / 32f).toInt().coerceIn(0, drawingColors.lastIndex)
                x < 625f -> widthIndex = (widthIndex + 1) % drawingWidths.size
                x < 725f -> symmetryIndex = (symmetryIndex + 1) % drawingSymmetries.size
                x < 820f -> backgroundIndex = (backgroundIndex + 1) % backgrounds.size
                x < 915f -> guideIndex = (guideIndex + 1) % guides.size
                else -> stampIndex = (stampIndex + 1) % stamps.size
            }
            return true
        }
        if (insideCanvas(x, y)) {
            pushUndo()
            val p = canvasPoint(x, y)
            lastPoint = p
            when (tool) {
                DrawingTool.FILL -> floodFill(p.first, p.second, drawingColors[colorIndex])
                DrawingTool.STAMP -> stamp(p.first, p.second)
                else -> { drawing = true; paintSegment(p, p) }
            }
            canvasDirty = true
            return true
        }
        return false
    }

    private fun openMode(index: Int) {
        when (index) {
            0 -> screen = Screen.DRAW
            3 -> { instrument = StudioInstrument.XYLOPHONE; screen = Screen.MUSIC }
            4 -> { instrument = StudioInstrument.PIANO; screen = Screen.MUSIC }
            else -> { simpleMode = index; screen = Screen.SIMPLE }
        }
    }

    private fun musicDown(x: Float, y: Float): Boolean {
        if (y in 385f..455f) {
            val i = ((x - 40f) / 125f).toInt()
            if (i in StudioInstrument.entries.indices) instrument = StudioInstrument.entries[i]
            return true
        }
        if (y in 105f..350f) {
            val note = ((x - 72f) / 117f).toInt()
            if (note in studioNotes.indices) {
                platform.playInstrument(instrument.ordinal, note)
                feedback = "${instrument.label}: ${studioNotes[note].label}"
            }
        }
        return true
    }

    private fun simpleDown(x: Float, y: Float): Boolean {
        if (y in 120f..420f) {
            feedback = when (simpleMode) {
                1 -> "Zona coloreada"
                2 -> "Pieza colocada"
                else -> "Patrón actualizado"
            }
        }
        return true
    }

    private fun galleryDown(x: Float, y: Float): Boolean {
        if (y > 455f) { galleryOpen = false; return true }
        if (galleryFiles.isEmpty()) return true
        when {
            y < 150f && x < 320f -> galleryIndex = (galleryIndex - 1).mod(galleryFiles.size)
            y < 150f && x < 640f -> galleryIndex = (galleryIndex + 1).mod(galleryFiles.size)
            y < 150f && x < 800f -> loadDrawing(galleryFiles[galleryIndex])
            y < 150f -> { galleryFiles[galleryIndex].delete(); refreshGallery() }
        }
        return true
    }

    private fun insideCanvas(x: Float, y: Float) = x in canvasX..canvasX + canvasW && y in canvasY..canvasY + canvasH
    private fun canvasPoint(x: Float, y: Float): Pair<Int, Int> =
        (((x - canvasX) / canvasW) * pixmapW).roundToInt().coerceIn(0, pixmapW - 1) to
            (((canvasY + canvasH - y) / canvasH) * pixmapH).roundToInt().coerceIn(0, pixmapH - 1)

    private fun drawTo(x: Float, y: Float) {
        if (!insideCanvas(x, y)) return
        val next = canvasPoint(x, y)
        paintSegment(lastPoint, next)
        lastPoint = next
        canvasDirty = true
    }

    private fun paintSegment(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        val radius = drawingWidths[widthIndex] * 3 / 2
        val color = if (tool == DrawingTool.ERASER) 0x00000000 else drawingColors[colorIndex]
        val steps = maxOf(abs(to.first - from.first), abs(to.second - from.second), 1)
        repeat(steps + 1) { step ->
            val x = from.first + (to.first - from.first) * step / steps
            val y = from.second + (to.second - from.second) * step / steps
            symmetricPoints(x.toFloat(), y.toFloat(), pixmapW, pixmapH, drawingSymmetries[symmetryIndex]).forEach { (sx, sy) ->
                paintPoint(sx.toInt(), sy.toInt(), radius, color)
            }
        }
    }

    private fun paintPoint(x: Int, y: Int, radius: Int, rgba: Int) {
        canvas.blending = if (tool == DrawingTool.ERASER) Pixmap.Blending.None else Pixmap.Blending.SourceOver
        canvas.setColor(rgba)
        when (tool) {
            DrawingTool.PENCIL -> canvas.fillCircle(x, y, maxOf(2, radius / 3))
            DrawingTool.CRAYON -> repeat(5) { canvas.fillCircle(x + Random.nextInt(-radius, radius + 1), y + Random.nextInt(-radius, radius + 1), maxOf(2, radius / 4)) }
            DrawingTool.MARKER -> canvas.fillCircle(x, y, radius)
            DrawingTool.NEON -> { canvas.setColor((rgba and 0xFFFFFF00.toInt()) or 0x55); canvas.fillCircle(x, y, radius * 2); canvas.setColor(rgba); canvas.fillCircle(x, y, maxOf(2, radius / 2)) }
            DrawingTool.SPRAY -> repeat(maxOf(8, radius)) { val a=Random.nextDouble()*PI*2;val d=Random.nextDouble()*radius*2;canvas.drawPixel((x+cos(a)*d).toInt(),(y+sin(a)*d).toInt()) }
            DrawingTool.ERASER -> canvas.fillCircle(x, y, radius * 2)
            else -> canvas.fillCircle(x, y, radius)
        }
        canvas.blending = Pixmap.Blending.SourceOver
    }

    private fun floodFill(startX: Int, startY: Int, replacement: Int) {
        val target = canvas.getPixel(startX, startY)
        if (target == replacement) return
        val queue = ArrayDeque<Pair<Int, Int>>(); queue += startX to startY
        var processed = 0
        canvas.blending = Pixmap.Blending.None; canvas.setColor(replacement)
        while (queue.isNotEmpty() && processed < pixmapW * pixmapH) {
            val (x, y) = queue.removeFirst()
            if (x !in 0 until pixmapW || y !in 0 until pixmapH || canvas.getPixel(x, y) != target) continue
            canvas.drawPixel(x, y); processed++
            queue += x - 1 to y; queue += x + 1 to y; queue += x to y - 1; queue += x to y + 1
        }
        canvas.blending = Pixmap.Blending.SourceOver
    }

    private fun stamp(x: Int, y: Int) {
        val r = drawingWidths[widthIndex] * 4
        canvas.setColor(drawingColors[colorIndex])
        symmetricPoints(x.toFloat(), y.toFloat(), pixmapW, pixmapH, drawingSymmetries[symmetryIndex]).forEach { (sx, sy) ->
            when (stampIndex) {
                0 -> star(sx.toInt(), sy.toInt(), r)
                1 -> repeat(6) { i -> val a=i*PI/3;canvas.fillCircle((sx+cos(a)*r*.65).toInt(),(sy+sin(a)*r*.65).toInt(),r/3) }
                2 -> { canvas.fillCircle(sx.toInt(),sy.toInt(),r/2);repeat(8){i->val a=i*PI/4;canvas.drawLine(sx.toInt(),sy.toInt(),(sx+cos(a)*r).toInt(),(sy+sin(a)*r).toInt())} }
                3 -> { canvas.fillCircle((sx-r*.28).toInt(),sy.toInt(),r/2);canvas.fillCircle((sx+r*.28).toInt(),sy.toInt(),r/2);canvas.fillTriangle((sx-r*.75).toInt(),sy.toInt(),(sx+r*.75).toInt(),sy.toInt(),sx.toInt(),(sy+r).toInt()) }
                4 -> { canvas.fillCircle((sx-r/2).toInt(),sy.toInt(),r/2);canvas.fillCircle(sx.toInt(),(sy-r/3).toInt(),r/2);canvas.fillCircle((sx+r/2).toInt(),sy.toInt(),r/2);canvas.fillRectangle((sx-r).toInt(),sy.toInt(),r*2,r/2) }
                else -> { canvas.fillCircle((sx-r/2).toInt(),sy.toInt(),r/2);canvas.fillCircle((sx+r/2).toInt(),sy.toInt(),r/2);canvas.fillRectangle((sx-r/8).toInt(),(sy-r).toInt(),r/4,r*2) }
            }
        }
    }

    private fun star(cx: Int, cy: Int, radius: Int) {
        val points = (0 until 10).map { i -> val a=-PI/2+i*PI/5;val r=if(i%2==0)radius.toDouble() else radius*.42;(cx+cos(a)*r).toInt() to (cy+sin(a)*r).toInt() }
        for (i in 1 until points.lastIndex) canvas.fillTriangle(points[0].first,points[0].second,points[i].first,points[i].second,points[i+1].first,points[i+1].second)
    }

    private fun snapshot(): Pixmap = Pixmap(pixmapW, pixmapH, Pixmap.Format.RGBA8888).also { it.drawPixmap(canvas, 0, 0) }
    private fun pushUndo() { undo += snapshot(); while (undo.size > 14) undo.removeFirst().dispose(); redo.forEach(Pixmap::dispose); redo.clear() }
    private fun restore(p: Pixmap) { canvas.setColor(0f,0f,0f,0f);canvas.fill();canvas.drawPixmap(p,0,0);canvasDirty=true }
    private fun undo() { if(undo.isEmpty())return;redo+=snapshot();val p=undo.removeLast();restore(p);p.dispose() }
    private fun redo() { if(redo.isEmpty())return;undo+=snapshot();val p=redo.removeLast();restore(p);p.dispose() }
    private fun clearCanvas() { pushUndo();canvas.blending=Pixmap.Blending.None;canvas.setColor(0f,0f,0f,0f);canvas.fill();canvas.blending=Pixmap.Blending.SourceOver;canvasDirty=true;feedback="Hoja nueva" }

    private fun galleryDir() = Gdx.files.local("galeria")
    private fun refreshGallery() { val dir=galleryDir();dir.mkdirs();galleryFiles=dir.list("png").sortedByDescending{it.lastModified()};galleryIndex=galleryIndex.coerceIn(0,maxOf(0,galleryFiles.lastIndex)) }
    private fun composedDrawing(): Pixmap = Pixmap(pixmapW, pixmapH, Pixmap.Format.RGBA8888).also { result ->
        val paper = intArrayOf(0xFDFAF5FF.toInt(), 0xFFFFFFFF.toInt(), 0xFFFFFFFF.toInt(), 0xFFFFFFFF.toInt(), 0xFDFAF5FF.toInt(), 0x26382FFF, 0xD8C0A0FF.toInt())
        result.setColor(paper[backgroundIndex]); result.fill()
        result.setColor(if (backgroundIndex == 5) 0xFFFFFF26.toInt() else 0x0000001F)
        when (backgroundIndex) {
            2 -> { for (x in 0 until pixmapW step 48) result.drawLine(x,0,x,pixmapH); for (y in 0 until pixmapH step 51) result.drawLine(0,y,pixmapW,y) }
            3 -> for (y in 0 until pixmapH step 51) result.drawLine(0,y,pixmapW,y)
            4 -> for (x in 24 until pixmapW step 42) for (y in 24 until pixmapH step 42) result.fillCircle(x,y,2)
        }
        result.drawPixmap(canvas, 0, 0)
    }
    private fun saveDrawing(): com.badlogic.gdx.files.FileHandle { val file=galleryDir().child("dibujo-${System.currentTimeMillis()}.png");val composed=composedDrawing();PixmapIO.writePNG(file,composed);composed.dispose();refreshGallery();feedback="Guardado en galería";return file }
    private fun shareDrawing() { val file=saveDrawing();platform.shareDrawing(file.file().absolutePath);feedback="Compartiendo dibujo" }
    private fun loadDrawing(file:com.badlogic.gdx.files.FileHandle){pushUndo();val loaded=Pixmap(file);canvas.setColor(0f,0f,0f,0f);canvas.fill();canvas.drawPixmap(loaded,0,0,loaded.width,loaded.height,0,0,pixmapW,pixmapH);loaded.dispose();canvasDirty=true;galleryOpen=false;feedback="Dibujo abierto"}

    override fun render() {
        Gdx.gl.glClearColor(.96f,.94f,.90f,1f);Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        viewport.apply();shapes.projectionMatrix=viewport.camera.combined;batch.projectionMatrix=viewport.camera.combined
        when(screen){Screen.MODES->renderModes();Screen.DRAW->renderDraw();Screen.MUSIC->renderMusic();Screen.SIMPLE->renderSimple()}
    }

    private fun renderModes(){text("TALLER CREATIVO",480f,500f,Color(0x332B27FF));text("Elige una modalidad",480f,462f,Color.DARK_GRAY);modeNames.forEachIndexed{i,m->val x=90f+(i%3)*270f;val y=350f-(i/3)*115f;box(x,y,235f,85f,Color(0x3154B5FF));text(m,x+117f,y+53f,Color.WHITE)}}
    private fun top(){button(8f,"MODOS");val old=font.data.scaleX;font.data.setScale(.82f);text(if(screen==Screen.DRAW)"PIZARRA LIBGDX" else "ESTUDIO MUSICAL",855f,516f,Color(0x332B27FF));font.data.setScale(old)}
    private fun renderDraw(){renderBackground();renderGuide();if(canvasDirty){texture.draw(canvas,0,0);canvasDirty=false};batch.begin();batch.color=Color.WHITE;batch.draw(texture,canvasX,canvasY,canvasW,canvasH,0,0,pixmapW,pixmapH,false,true);batch.end();top();listOf("DESHACER","REHACER","GUARDAR","COMPARTIR","NUEVA","GALERÍA").forEachIndexed{i,s->button(100f+i*110f,s)};DrawingTool.entries.forEachIndexed{i,t->smallButton(8f+i*105f,68f,100f,42f,t.label,tool==t)};repeat(16){i->circle(23f+i*32f,38f,12f,Color(drawingColors[i]),i==colorIndex)};smallButton(530f,16f,90f,42f,"Grosor ${drawingWidths[widthIndex]}",false);smallButton(630f,16f,90f,42f,"Mandala ${drawingSymmetries[symmetryIndex]}",false);smallButton(730f,16f,85f,42f,backgrounds[backgroundIndex],false);smallButton(820f,16f,85f,42f,guides[guideIndex],false);smallButton(910f,16f,45f,42f,"S",false);text(feedback,480f,116f,Color.DARK_GRAY);if(galleryOpen)renderGallery()}
    private fun renderBackground(){val colors=listOf(Color(0xFDFAF5FF.toInt()),Color.WHITE,Color.WHITE,Color.WHITE,Color(0xFDFAF5FF.toInt()),Color(0x26382FFF),Color(0xD8C0A0FF.toInt()));box(canvasX,canvasY,canvasW,canvasH,colors[backgroundIndex]);shapes.begin(ShapeRenderer.ShapeType.Line);shapes.color=if(backgroundIndex==5)Color(1f,1f,1f,.15f)else Color(0f,0f,0f,.12f);when(backgroundIndex){2->{var x=canvasX;while(x<canvasX+canvasW){shapes.line(x,canvasY,x,canvasY+canvasH);x+=32};var y=canvasY;while(y<canvasY+canvasH){shapes.line(canvasX,y,canvasX+canvasW,y);y+=32}};3->{var y=canvasY+34;while(y<canvasY+canvasH){shapes.line(canvasX,y,canvasX+canvasW,y);y+=34}};4->{var x=canvasX+16;while(x<canvasX+canvasW){var y=canvasY+16;while(y<canvasY+canvasH){shapes.circle(x,y,1.5f,8);y+=28};x+=28}}};shapes.end()}
    private fun renderGuide(){if(guideIndex==0)return;val c=if(backgroundIndex==5)Color(1f,1f,1f,.35f)else Color(0f,0f,0f,.25f);val g=guides[guideIndex];if(guideIndex<=4){val old=font.data.scaleX;font.data.setScale(7f);text(g,480f,340f,c);font.data.setScale(old)}else{shapes.begin(ShapeRenderer.ShapeType.Line);shapes.color=c;when(g){"Círculo"->shapes.circle(480f,285f,110f,64);"Cuadrado"->shapes.rect(370f,175f,220f,220f);"Triángulo"->{shapes.line(480f,405f,365f,175f);shapes.line(365f,175f,595f,175f);shapes.line(595f,175f,480f,405f)};"Estrella"->repeat(10){i->val a=-PI/2+i*PI/5;val b=-PI/2+(i+1)*PI/5;val ra=if(i%2==0)120 else 50;val rb=if((i+1)%2==0)120 else 50;shapes.line((480+cos(a)*ra).toFloat(),(285+sin(a)*ra).toFloat(),(480+cos(b)*rb).toFloat(),(285+sin(b)*rb).toFloat())};else->{shapes.circle(430f,310f,65f,32);shapes.circle(530f,310f,65f,32);shapes.line(365f,310f,480f,170f);shapes.line(595f,310f,480f,170f)}};shapes.end()}}
    private fun renderMusic(){top();StudioInstrument.entries.forEachIndexed{i,v->smallButton(40f+i*125f,385f,115f,62f,v.label,instrument==v)};val colors=listOf(Color.RED,Color.ORANGE,Color.YELLOW,Color(0x49A65AFF),Color.CYAN,Color(0x5577DDFF),Color(0x9955CCFF.toInt()));studioNotes.forEachIndexed{i,n->val x=72f+i*117f;val h=230f-i*7f;box(x,105f,105f,h,colors[i]);text(n.label,x+52f,190f,Color.WHITE)};text("Toca Do, Re, Mi, Fa, Sol, La y Si con el timbre de cada instrumento",480f,80f,Color.DARK_GRAY);text(feedback,480f,55f,Color(0x3154B5FF))}
    private fun renderSimple(){top();text(modeNames[simpleMode].uppercase(),480f,420f,Color(0x332B27FF));text("Esta modalidad conserva su espacio creativo por niveles",480f,350f,Color.DARK_GRAY);repeat(6){i->box(170f+(i%3)*220f,150f+(i/3)*110f,180f,85f,Color(drawingColors[i+1]))};text(feedback,480f,90f,Color.DARK_GRAY)}
    private fun renderGallery(){box(120f,90f,720f,370f,Color(0x111B30F2.toInt()));text("GALERÍA · ${galleryFiles.size} dibujos",480f,420f,Color.WHITE);if(galleryFiles.isEmpty()){text("Aún no hay dibujos guardados",480f,280f,Color.LIGHT_GRAY)}else{val file=galleryFiles[galleryIndex];text(file.name(),480f,330f,Color.LIGHT_GRAY);text("${galleryIndex+1}/${galleryFiles.size}",480f,285f,Color.WHITE)};smallButton(160f,105f,140f,50f,"ANTERIOR",false);smallButton(320f,105f,140f,50f,"SIGUIENTE",false);smallButton(480f,105f,140f,50f,"ABRIR",false);smallButton(640f,105f,140f,50f,"BORRAR",false);text("Toca la barra superior para cerrar",480f,190f,Color.LIGHT_GRAY)}
    private fun button(x:Float,label:String)=smallButton(x,488f,100f,40f,label,false)
    private fun smallButton(x:Float,y:Float,w:Float,h:Float,label:String,selected:Boolean){box(x,y,w,h,if(selected)Color(0xE08A3AFF.toInt())else Color(0x3D507AFF));val old=font.data.scaleX;font.data.setScale(if(label.length>10).85f else 1.05f);text(label,x+w/2,y+h*.65f,Color.WHITE);font.data.setScale(old)}
    private fun circle(x:Float,y:Float,r:Float,c:Color,selected:Boolean){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.circle(x,y,r,24);shapes.end();if(selected){shapes.begin(ShapeRenderer.ShapeType.Line);shapes.color=Color.BLACK;shapes.circle(x,y,r+4,24);shapes.end()}}
    private fun box(x:Float,y:Float,w:Float,h:Float,c:Color){shapes.begin(ShapeRenderer.ShapeType.Filled);shapes.color=c;shapes.roundedRect(x,y,w,h);shapes.end()}
    private fun text(v:String,x:Float,y:Float,c:Color){batch.begin();font.color=c;font.draw(batch,v,x-240f,y,480f,Align.center,false);batch.end()}
    override fun resize(width:Int,height:Int)=viewport.update(width,height,true)
    override fun dispose(){undo.forEach(Pixmap::dispose);redo.forEach(Pixmap::dispose);canvas.dispose();texture.dispose();shapes.dispose();batch.dispose();font.dispose()}
}
