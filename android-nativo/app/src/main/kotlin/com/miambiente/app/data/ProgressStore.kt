package com.miambiente.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.miambiente.app.model.estrellasPara
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.progressDataStore by preferencesDataStore(name = "progreso")

/** Puerto directo de GameProgress en lib/progressStore.ts. */
data class GameProgress(
    val completados: Set<Int> = emptySet(),
    val nivelDesbloqueado: Int = 1,
    val estrellas: Int = 0,
    val vecesJugado: Int = 0,
)

/**
 * Equivalente nativo de progressStore.ts. Sin backend, todo en el
 * dispositivo — se pierde si se desinstala, por diseño (igual que la
 * versión web).
 */
class ProgressStore(private val context: Context) {
    private fun claveCompletados(id: String) = stringPreferencesKey("${id}_completados")
    private fun claveNivel(id: String) = intPreferencesKey("${id}_nivel")
    private fun claveEstrellas(id: String) = intPreferencesKey("${id}_estrellas")
    private fun claveVeces(id: String) = intPreferencesKey("${id}_veces")

    fun progresoDe(id: String): Flow<GameProgress> = context.progressDataStore.data.map { p ->
        GameProgress(
            completados = (p[claveCompletados(id)] ?: "")
                .split(",").filter { it.isNotBlank() }.map { it.toInt() }.toSet(),
            nivelDesbloqueado = p[claveNivel(id)] ?: 1,
            estrellas = p[claveEstrellas(id)] ?: 0,
            vecesJugado = p[claveVeces(id)] ?: 0,
        )
    }

    suspend fun registrarJugada(id: String) {
        context.progressDataStore.edit { p ->
            val actual = p[claveVeces(id)] ?: 0
            p[claveVeces(id)] = actual + 1
        }
    }

    /** Cierra un nivel: agrega a completados, desbloquea el siguiente y suma estrellas la primera vez. */
    suspend fun completarNivel(id: String, nivel: Int): Int {
        var ganadas = 0
        context.progressDataStore.edit { p ->
            val completados = (p[claveCompletados(id)] ?: "")
                .split(",").filter { it.isNotBlank() }.map { it.toInt() }.toMutableSet()
            val primeraVez = completados.add(nivel)
            p[claveCompletados(id)] = completados.joinToString(",")
            p[claveNivel(id)] = maxOf(p[claveNivel(id)] ?: 1, minOf(nivel + 1, 100))
            if (primeraVez) {
                ganadas = estrellasPara(nivel)
                p[claveEstrellas(id)] = (p[claveEstrellas(id)] ?: 0) + ganadas
            }
        }
        return ganadas
    }
}
