package com.miambiente.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.miambiente.app.model.estrellasPara
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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
class ProgressStore(private val context: Context, scope: CoroutineScope) {
    private fun claveCompletados(id: String) = stringPreferencesKey("${id}_completados")
    private fun claveNivel(id: String) = intPreferencesKey("${id}_nivel")
    private fun claveEstrellas(id: String) = intPreferencesKey("${id}_estrellas")
    private fun claveVeces(id: String) = intPreferencesKey("${id}_veces")

    // Copia en memoria, siempre al día, de todo el progreso. Permite que un
    // material sepa en qué nivel arrancar sin esperar una lectura async (que
    // haría aparecer el nivel 1 un instante y leer su consigna en voz alta).
    private val cache: StateFlow<Preferences?> =
        context.progressDataStore.data.stateIn(scope, SharingStarted.Eagerly, null)

    /** `true` en cuanto el progreso guardado terminó de leerse por primera vez. */
    val listo: StateFlow<Boolean> =
        cache.map { it != null }.stateIn(scope, SharingStarted.Eagerly, false)

    /**
     * Último nivel desbloqueado de un material, donde conviene retomarlo.
     * `minimo` es el nivel inicial sugerido por la edad: nunca se retoma
     * por debajo de él.
     */
    fun nivelGuardado(id: String, minimo: Int = 1): Int =
        maxOf(cache.value?.get(claveNivel(id)) ?: 1, minimo).coerceIn(1, 100)

    private fun leer(p: Preferences, id: String) = GameProgress(
        completados = leerCompletados(p[claveCompletados(id)]),
        nivelDesbloqueado = p[claveNivel(id)] ?: 1,
        estrellas = p[claveEstrellas(id)] ?: 0,
        vecesJugado = p[claveVeces(id)] ?: 0,
    )

    fun progresoDe(id: String): Flow<GameProgress> = context.progressDataStore.data.map { p -> leer(p, id) }

    /** Progreso de varios materiales a la vez, para el resumen del adulto. */
    fun resumen(ids: List<String>): Flow<Map<String, GameProgress>> =
        context.progressDataStore.data.map { p -> ids.associateWith { leer(p, it) } }

    /** Cuenta una apertura del material (se muestra en el resumen del adulto). */
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
            val completados = leerCompletados(p[claveCompletados(id)]).toMutableSet()
            val primeraVez = completados.add(nivel)
            p[claveCompletados(id)] = completados.joinToString(",")
            p[claveNivel(id)] = nivelTrasCompletar(p[claveNivel(id)], nivel)
            if (primeraVez) {
                ganadas = estrellasPara(nivel)
                p[claveEstrellas(id)] = (p[claveEstrellas(id)] ?: 0) + ganadas
            }
        }
        return ganadas
    }
}

/** Los niveles completados se guardan como "1,2,5"; tolera texto vacío o dañado. */
internal fun leerCompletados(texto: String?): Set<Int> =
    texto.orEmpty().split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

/** Completar un nivel desbloquea el siguiente, sin retroceder nunca ni pasar de 100. */
internal fun nivelTrasCompletar(desbloqueado: Int?, nivel: Int): Int =
    maxOf(desbloqueado ?: 1, minOf(nivel + 1, 100))
