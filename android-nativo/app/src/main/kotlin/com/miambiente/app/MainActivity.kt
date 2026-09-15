package com.miambiente.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Services
import com.miambiente.app.theme.MiAmbienteTheme
import com.miambiente.app.ui.screens.CicloVidaScreen
import com.miambiente.app.ui.screens.ColoresScreen
import com.miambiente.app.ui.screens.CuerpoScreen
import com.miambiente.app.ui.screens.DiaNocheScreen
import com.miambiente.app.ui.screens.DiasSemanaScreen
import com.miambiente.app.ui.screens.DietaAnimalScreen
import com.miambiente.app.ui.screens.ElLaScreen
import com.miambiente.app.ui.screens.EstacionesScreen
import com.miambiente.app.ui.screens.EstadosAguaScreen
import com.miambiente.app.ui.screens.FormasScreen
import com.miambiente.app.ui.screens.FrutaVerduraScreen
import com.miambiente.app.ui.screens.GatoScreen
import com.miambiente.app.ui.screens.HabitatScreen
import com.miambiente.app.ui.screens.HomeScreen
import com.miambiente.app.ui.screens.HusosScreen
import com.miambiente.app.ui.screens.InstrumentosScreen
import com.miambiente.app.ui.screens.OficiosScreen
import com.miambiente.app.ui.screens.ParesImparesScreen
import com.miambiente.app.ui.screens.PinzaScreen
import com.miambiente.app.ui.screens.RpsScreen
import com.miambiente.app.ui.screens.SelectorEdadScreen
import com.miambiente.app.ui.screens.SeresVivosScreen
import com.miambiente.app.ui.screens.SingularPluralScreen
import com.miambiente.app.ui.screens.TamanosScreen
import com.miambiente.app.ui.screens.TorreRosaScreen
import com.miambiente.app.ui.screens.TransporteScreen

/**
 * Rutas de navegación — equivalente nativo del App Router de Next.js
 * (app/page.tsx, app/games/<slug>/page.tsx). Cada ruta usa el mismo id
 * que su GameDef en model/GameDef.kt, así HomeScreen navega con
 * `navController.navigate(juego.id)` sin necesitar un mapeo aparte.
 */
object Ruta {
    const val EDAD = "edad"
    const val INICIO = "inicio"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val services = Services(applicationContext)

        setContent {
            MiAmbienteTheme {
                CompositionLocalProvider(LocalServices provides services) {
                    // `null` = todavía no llegó la primera lectura de DataStore
                    // (es async, a diferencia de zustand+localStorage que es
                    // síncrono). No se crea el NavHost hasta tener el valor
                    // real: el `startDestination` de NavHost se fija una sola
                    // vez al crearse, así que decidirlo con un valor por
                    // defecto y "corregirlo después" no funciona — mandaría
                    // siempre al selector de edad aunque ya hubiera una
                    // guardada, sin forma de navegar sola a inicio.
                    val settings by produceState<com.miambiente.app.data.Settings?>(initialValue = null, services) {
                        services.settings.settings.collect { value = it }
                    }
                    val actual = settings ?: return@CompositionLocalProvider

                    val navController = rememberNavController()
                    val volver: () -> Unit = { navController.popBackStack() }

                    NavHost(
                        navController = navController,
                        startDestination = if (actual.edad == null) Ruta.EDAD else Ruta.INICIO,
                    ) {
                        composable(Ruta.EDAD) {
                            SelectorEdadScreen(onElegida = {
                                navController.navigate(Ruta.INICIO) {
                                    popUpTo(Ruta.EDAD) { inclusive = true }
                                }
                            })
                        }
                        composable(Ruta.INICIO) {
                            HomeScreen(
                                edad = actual.edad ?: 6,
                                onAbrirJuego = { id -> navController.navigate(id) },
                                onCambiarEdad = {
                                    navController.navigate(Ruta.EDAD) {
                                        popUpTo(0)
                                    }
                                },
                            )
                        }

                        // --- MaterialQuiz ---
                        composable("formas") { FormasScreen(volver) }
                        composable("cuerpo") { CuerpoScreen(volver) }
                        composable("colores") { ColoresScreen(volver) }
                        composable("instrumentos") { InstrumentosScreen(volver) }
                        composable("oficios") { OficiosScreen(volver) }

                        // --- MaterialOrdenar ---
                        composable("torre-rosa") { TorreRosaScreen(volver) }
                        composable("dias-semana") { DiasSemanaScreen(volver) }
                        composable("estaciones") { EstacionesScreen(volver) }
                        composable("ciclo-vida") { CicloVidaScreen(volver) }

                        // --- MaterialClasificar ---
                        composable("seres-vivos") { SeresVivosScreen(volver) }
                        composable("habitat") { HabitatScreen(volver) }
                        composable("dieta-animal") { DietaAnimalScreen(volver) }
                        composable("fruta-verdura") { FrutaVerduraScreen(volver) }
                        composable("el-la") { ElLaScreen(volver) }
                        composable("pares-impares") { ParesImparesScreen(volver) }
                        composable("tamanos") { TamanosScreen(volver) }
                        composable("estados-agua") { EstadosAguaScreen(volver) }
                        composable("dia-noche") { DiaNocheScreen(volver) }
                        composable("singular-plural") { SingularPluralScreen(volver) }
                        composable("transporte") { TransporteScreen(volver) }

                        // --- MaterialTransferir ---
                        composable("pinza") { PinzaScreen(volver) }
                        composable("husos") { HusosScreen(volver) }

                        // --- Independientes ---
                        composable("gato") { GatoScreen(volver) }
                        composable("rps") { RpsScreen(volver) }
                    }
                }
            }
        }
    }
}
