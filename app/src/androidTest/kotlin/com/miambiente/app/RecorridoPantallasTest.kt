package com.miambiente.app

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Services
import com.miambiente.app.theme.Area
import com.miambiente.app.model.CATALOGO
import com.miambiente.app.model.MATERIALES_CONSOLIDADOS
import com.miambiente.app.theme.MiAmbienteTheme
import com.miambiente.app.ui.PANTALLAS
import com.miambiente.app.ui.screens.AjustesScreen
import com.miambiente.app.ui.screens.HomeScreen
import com.miambiente.app.ui.screens.MaterialConsolidadoScreen
import com.miambiente.app.ui.screens.ProgresoScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Abre cada pantalla de la app, una por una, y deja correr su reloj un
 * momento: si alguna lanza una excepción al componerse o en su primer
 * segundo (bucles de juego, lectura de archivos, mediciones), la prueba
 * falla nombrando la pantalla. Cubre lo que las pruebas unitarias no ven:
 * hoy hay más de 90 pantallas y revisarlas a mano no escala.
 *
 * Además exige que todo elemento tocable tenga un área de al menos 48dp (la
 * guía de accesibilidad de Android; importa más con dedos chicos) y los
 * reporta en logcat con la etiqueta `Recorrido`.
 */
@RunWith(AndroidJUnit4::class)
class RecorridoPantallasTest {
    @get:Rule
    val regla = createAndroidComposeRule<ComponentActivity>()

    private val services: Services
        get() = (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as RominaApp).services

    private val pantallas: Map<String, @Composable () -> Unit> = buildMap {
        put("inicio") { HomeScreen(onAbrirJuego = {}, onAjustes = {}, nombre = "Prueba") }
        put("ajustes") { AjustesScreen(onVolver = {}, onVerProgreso = {}) }
        put("progreso") { ProgresoScreen(onVolver = {}) }
        MATERIALES_CONSOLIDADOS.forEach { m ->
            put("material/${m.id}") { MaterialConsolidadoScreen(materialId = m.id, onAbrirModo = {}, onVolver = {}) }
        }
        PANTALLAS.forEach { (id, pantalla) -> put(id) { pantalla {} } }
    }

    @Before
    fun esperarDatos() {
        runBlocking {
            services.settings.actual.first { it != null }
            services.progress.listo.first { it }
        }
    }

    @Test
    fun cadaPantallaAbreSinCerrarse() {
        var actual by mutableStateOf<String?>(null)
        regla.mainClock.autoAdvance = false
        regla.setContent {
            MiAmbienteTheme {
                CompositionLocalProvider(LocalServices provides services) {
                    key(actual) { actual?.let { pantallas.getValue(it)() } }
                }
            }
        }

        val pequenos = mutableMapOf<String, Int>()
        val sinSelector = mutableListOf<String>()
        for (id in pantallas.keys) {
            try {
                regla.runOnUiThread { actual = id }
                regla.mainClock.advanceTimeBy(1_500)
                regla.onRoot().assertExists()
                val n = tocablesPequenos()
                if (n > 0) pequenos[id] = n
                // Un modo que anuncia "100 niveles progresivos" debe dejar
                // elegir nivel; antes ~20 no tenían selector.
                if (id in conNiveles && !tieneSelector()) sinSelector += id
            } catch (e: Throwable) {
                throw AssertionError("La pantalla '$id' falló al abrirse", e)
            }
        }
        regla.runOnUiThread { actual = null }

        Log.i("Recorrido", "Pantallas abiertas: ${pantallas.size}")
        pequenos.forEach { (id, n) -> Log.i("Recorrido", "Tocables de menos de 48dp en '$id': $n") }
        assertTrue("Áreas táctiles de menos de 48dp en: $pequenos", pequenos.isEmpty())
        assertTrue("Modos con niveles sin selector de nivel: $sinSelector", sinSelector.isEmpty())
    }

    /** Modos que el menú presenta con "100 niveles progresivos". */
    private val conNiveles = CATALOGO.filter { !it.libre && it.area != Area.COMPANIA }.map { it.id }.toSet()

    private fun tieneSelector() =
        regla.onAllNodes(hasContentDescription("Toca para elegir otro nivel", substring = true))
            .fetchSemanticsNodes().isNotEmpty()

    /** Elementos con acción de toque cuyo lado menor mide menos de 48dp. */
    private fun tocablesPequenos(): Int {
        val minimo = with(regla.density) { 48.dp.toPx() } - 1f
        // touchBoundsInRoot, no el tamaño dibujado: los botones de Material
        // amplían su área de toque a 48dp por fuera de lo que se ve.
        // Se omiten los que el desplazamiento deja cortados en el borde: su
        // área visible es menor que su tamaño real y darían falso positivo.
        return regla.onAllNodes(hasClickAction()).fetchSemanticsNodes().count { nodo ->
            val visible = nodo.boundsInRoot
            val cortado = visible.width < nodo.size.width - 1 || visible.height < nodo.size.height - 1
            val area = nodo.touchBoundsInRoot
            !cortado && minOf(area.width, area.height) < minimo
        }
    }
}
