package com.miambiente.app.ui.materials

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.miambiente.app.data.Efecto
import com.miambiente.app.data.LocalServices
import com.miambiente.app.data.Patron
import com.miambiente.app.data.Services
import com.miambiente.app.model.GameDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Estado común a todos los materiales — puerto directo de
 * lib/useMaterial.ts. Concentrarlo aquí (en vez de repetirlo en cada
 * pantalla) es lo que permite que los 4 patrones compartidos se
 * comporten todos igual ante el acierto y el intento, igual que en la
 * versión web.
 */
class MaterialState(
    private val services: Services,
    val juego: GameDef,
    private val scope: CoroutineScope,
) {
    var nivel by mutableStateOf(1)
        private set
    var nota by mutableStateOf<String?>(null)
        private set
    var logrado by mutableStateOf(false)
        private set
    var estrellas by mutableStateOf(0)
        private set

    private var notaJob: Job? = null

    fun irANivel(n: Int) {
        nivel = n.coerceIn(1, 100)
        logrado = false
        nota = null
    }

    fun siguiente() = irANivel(nivel + 1)
    fun repetir() = irANivel(nivel)

    /** Acierto parcial: una pieza quedó en su lugar, el nivel sigue. */
    fun acierto(texto: String = "¡Muy bien!") {
        services.sound.tocar(Efecto.CORRECT)
        services.haptics.vibrar(Patron.ACIERTO)
        mostrar(texto)
    }

    /** Control del error: nunca hay penalización, ni vidas, ni "perdiste". */
    fun intento(texto: String = "Esa todavía no. Busca otra") {
        services.sound.tocar(Efecto.WRONG)
        services.haptics.vibrar(Patron.ERROR)
        mostrar(texto)
    }

    /** Cierra el nivel: otorga estrellas, guarda el progreso y celebra. */
    fun completar() {
        scope.launch {
            val ganadas = services.progress.completarNivel(juego.id, nivel)
            estrellas = ganadas
            logrado = true
            services.haptics.vibrar(Patron.LOGRO)
            services.sound.tocar(Efecto.WIN)
        }
    }

    private fun mostrar(texto: String) {
        nota = texto
        notaJob?.cancel()
        notaJob = scope.launch {
            delay(1500)
            nota = null
        }
    }
}

@Composable
fun rememberMaterialState(juego: GameDef): MaterialState {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    return remember(juego.id) { MaterialState(services, juego, scope) }
}
