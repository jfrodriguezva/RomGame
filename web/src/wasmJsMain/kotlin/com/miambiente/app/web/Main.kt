package com.miambiente.app.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.read
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Services
import com.miambiente.app.theme.MiAmbienteTheme
import com.miambiente.app.ui.PANTALLAS
import com.miambiente.app.ui.screens.AjustesScreen
import com.miambiente.app.ui.screens.HomeScreen
import com.miambiente.app.ui.screens.MaterialConsolidadoScreen
import com.miambiente.app.ui.screens.ProgresoScreen
import com.miambiente.app.web.recursos.Res
import com.miambiente.app.web.recursos.noto_emoji
import android.graphics.FuentesWeb
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.skia.Data
import org.jetbrains.skia.FontMgr
import org.jetbrains.compose.resources.preloadFont

/** Arranque en el navegador: el equivalente de MainActivity del APK. */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "romina") { App() }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun App() {
    // La app usa muchos emojis y el navegador, al dibujar en canvas, no
    // trae ninguna fuente que los tenga: se carga Noto Color Emoji y se
    // registra como respaldo antes de mostrar nada.
    val resolver = LocalFontFamilyResolver.current
    val emoji by preloadFont(Res.font.noto_emoji)
    var fuentesListas by remember { mutableStateOf(false) }
    LaunchedEffect(emoji) {
        emoji?.let {
            resolver.preload(FontFamily(it))
            // La pizarra dibuja texto directo con Skia (letras guía y
            // sellos), que necesita sus propias fuentes cargadas.
            val mgr = FontMgr.default
            FuentesWeb.emoji = mgr.makeFromData(Data.makeFromBytes(Res.readBytes("font/noto_emoji.ttf")))
            FuentesWeb.latina = mgr.makeFromData(Data.makeFromBytes(Res.readBytes("font/nunito.ttf")))
            fuentesListas = true
        }
    }
    if (!fuentesListas) {
        Box(Modifier.fillMaxSize().background(Color(0xFFFBF7F0)), contentAlignment = Alignment.Center) {
            Text("Cargando RominaGame…", color = Color(0xFF3F342C))
        }
        return
    }

    val services = remember { Services() }
    MiAmbienteTheme {
        CompositionLocalProvider(LocalServices provides services) {
            val settings by services.settings.actual.collectAsState()
            val actual = settings ?: return@CompositionLocalProvider
            val navController = rememberNavController()
            val volver: () -> Unit = { navController.popBackStack() }

            // Mismas rutas que MainActivity.
            NavHost(navController = navController, startDestination = "inicio") {
                composable("inicio") {
                    HomeScreen(
                        onAbrirJuego = { id -> navController.navigate(id) },
                        onAjustes = { navController.navigate("ajustes") },
                        nombre = actual.nombre,
                    )
                }
                composable("ajustes") {
                    AjustesScreen(onVolver = volver, onVerProgreso = { navController.navigate("progreso") })
                }
                composable("progreso") {
                    ProgresoScreen(onVolver = volver, onAbrirJuego = { id -> navController.navigate(id) })
                }
                composable("material/{materialId}") { entrada ->
                    MaterialConsolidadoScreen(
                        materialId = entrada.arguments?.read { getStringOrNull("materialId") }.orEmpty(),
                        onAbrirModo = { id -> navController.navigate(id) },
                        onVolver = volver,
                    )
                }
                PANTALLAS.forEach { (id, pantalla) ->
                    composable(id) { pantalla(volver) }
                }
            }
        }
    }
}
