package com.miambiente.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.miambiente.app.model.estrellasPara
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.progressDataStore by preferencesDataStore(name = "progreso")

/** Estado persistido de una familia o modalidad. */
data class GameProgress(
    val completados: Set<Int> = emptySet(),
    val nivelDesbloqueado: Int = 1,
    val estrellas: Int = 0,
    val vecesJugado: Int = 0,
)

/**
 * Sin backend: todo permanece en el dispositivo y se elimina al borrar los
 * datos de la aplicación o desinstalarla.
 */
class ProgressStore(private val context: Context) {
    private val claveMigracionFamilias = booleanPreferencesKey("migracion_familias_v1")
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

    /**
     * Conserva el progreso escrito con los identificadores anteriores a la
     * consolidación. Los datos originales permanecen intactos y cada nivel se
     * codifica dentro de su familia como `modo * 100 + nivel`, evitando
     * colisiones entre modalidades.
     */
    suspend fun migrarFamilias(familias: Map<String, List<String>>) {
        context.progressDataStore.edit { p ->
            if (p[claveMigracionFamilias] == true) return@edit
            familias.forEach { (familia, modos) ->
                val completados = (p[claveCompletados(familia)] ?: "")
                    .split(",").filter(String::isNotBlank).map(String::toInt).toMutableSet()
                var estrellas = p[claveEstrellas(familia)] ?: 0
                var jugadas = p[claveVeces(familia)] ?: 0
                var desbloqueado = p[claveNivel(familia)] ?: 1
                modos.forEachIndexed { indice, modo ->
                    if (modo == familia) return@forEachIndexed
                    val anteriores = (p[claveCompletados(modo)] ?: "")
                        .split(",").filter(String::isNotBlank).map(String::toInt)
                    anteriores.forEach { completados += indice * 100 + it }
                    estrellas += p[claveEstrellas(modo)] ?: 0
                    jugadas += p[claveVeces(modo)] ?: 0
                    desbloqueado = maxOf(desbloqueado, p[claveNivel(modo)] ?: 1)
                }
                p[claveCompletados(familia)] = completados.sorted().joinToString(",")
                p[claveEstrellas(familia)] = estrellas
                p[claveVeces(familia)] = jugadas
                p[claveNivel(familia)] = desbloqueado
            }
            p[claveMigracionFamilias] = true
        }
    }
}
