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
import com.miambiente.app.ui.screens.GatoScreen
import com.miambiente.app.ui.screens.HomeScreen
import com.miambiente.app.ui.screens.SelectorEdadScreen
import com.miambiente.app.ui.screens.SeresVivosScreen
import com.miambiente.app.ui.screens.FormasScreen
import com.miambiente.app.ui.screens.TorreRosaScreen

/**
 * Rutas de navegación — equivalente nativo del App Router de Next.js
 * (app/page.tsx, app/games/<slug>/page.tsx).
 */
object Ruta {
    const val EDAD = "edad"
    const val INICIO = "inicio"
    const val FORMAS = "formas"
    const val TORRE_ROSA = "torre-rosa"
    const val SERES_VIVOS = "seres-vivos"
    const val GATO = "gato"
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
                        composable(Ruta.FORMAS) { FormasScreen(onVolver = { navController.popBackStack() }) }
                        composable(Ruta.TORRE_ROSA) { TorreRosaScreen(onVolver = { navController.popBackStack() }) }
                        composable(Ruta.SERES_VIVOS) { SeresVivosScreen(onVolver = { navController.popBackStack() }) }
                        composable(Ruta.GATO) { GatoScreen(onVolver = { navController.popBackStack() }) }
                    }
                }
            }
        }
    }
}
