package com.miambiente.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Services
import com.miambiente.app.theme.MiAmbienteTheme
import com.miambiente.app.ui.PANTALLAS
import com.miambiente.app.ui.screens.AjustesScreen
import com.miambiente.app.ui.screens.HomeScreen
import com.miambiente.app.ui.screens.MaterialConsolidadoScreen
import com.miambiente.app.ui.screens.ProgresoScreen

/**
 * Rutas de navegación. Cada material usa como ruta el mismo id que su
 * GameDef en model/GameDef.kt (ver `PANTALLAS`), así HomeScreen navega con
 * `navController.navigate(juego.id)` sin necesitar un mapeo aparte.
 */
object Ruta {
    const val INICIO = "inicio"
    const val AJUSTES = "ajustes"
    const val PROGRESO = "progreso"
}

class MainActivity : ComponentActivity() {
    private val services: Services get() = (application as RominaApp).services

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MiAmbienteTheme {
                CompositionLocalProvider(LocalServices provides services) {
                    // Espera la primera lectura de ajustes y progreso para
                    // mostrar el nombre correcto desde el inicio y que cada
                    // material arranque en su nivel guardado, leído de forma
                    // síncrona al abrirse.
                    val settings by services.settings.actual.collectAsState()
                    val progresoListo by services.progress.listo.collectAsState()
                    val actual = settings ?: return@CompositionLocalProvider
                    if (!progresoListo) return@CompositionLocalProvider

                    val navController = rememberNavController()
                    val volver: () -> Unit = { navController.popBackStack() }

                    NavHost(
                        navController = navController,
                        startDestination = Ruta.INICIO,
                    ) {
                        composable(Ruta.INICIO) {
                            HomeScreen(
                                onAbrirJuego = { id -> navController.navigate(id) },
                                onAjustes = { navController.navigate(Ruta.AJUSTES) },
                                nombre = actual.nombre,
                            )
                        }
                        composable(Ruta.AJUSTES) {
                            AjustesScreen(onVolver = volver, onVerProgreso = { navController.navigate(Ruta.PROGRESO) })
                        }
                        composable(Ruta.PROGRESO) {
                            ProgresoScreen(onVolver = volver, onAbrirJuego = { id -> navController.navigate(id) })
                        }
                        composable("material/{materialId}") { entrada ->
                            MaterialConsolidadoScreen(
                                materialId = entrada.arguments?.getString("materialId").orEmpty(),
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
    }

    // Al pasar a segundo plano (botón de inicio, pantalla apagada) se
    // silencia todo: sin esto el fondo musical seguía en bucle y la voz
    // terminaba su consigna con la app ya cerrada.
    override fun onStop() {
        super.onStop()
        services.musica.pausar()
        services.speech.callar()
    }

    override fun onStart() {
        super.onStart()
        services.musica.reanudar()
    }
}
