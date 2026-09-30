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
    /** Días (desde la época) en que se abrió; solo los últimos [DIAS_GUARDADOS]. */
    val dias: Set<Long> = emptySet(),
    val aciertos: Int = 0,
    val errores: Int = 0,
)

/** Cuántos días de historial se conservan por modo para el resumen del adulto. */
internal const val DIAS_GUARDADOS = 60

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
    private fun claveDias(id: String) = stringPreferencesKey("${id}_dias")
    private fun claveAciertos(id: String) = intPreferencesKey("${id}_aciertos")
    private fun claveErrores(id: String) = intPreferencesKey("${id}_errores")

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
        dias = leerDias(p[claveDias(id)]),
        aciertos = p[claveAciertos(id)] ?: 0,
        errores = p[claveErrores(id)] ?: 0,
    )

    fun progresoDe(id: String): Flow<GameProgress> = context.progressDataStore.data.map { p -> leer(p, id) }

    /** Progreso de varios materiales a la vez, para el resumen del adulto. */
    fun resumen(ids: List<String>): Flow<Map<String, GameProgress>> =
        context.progressDataStore.data.map { p -> ids.associateWith { leer(p, it) } }

    /** Cuenta una apertura del material y el día (se muestran en el resumen del adulto). */
    suspend fun registrarJugada(id: String, hoy: Long = hoyEpoch()) {
        context.progressDataStore.edit { p ->
            val actual = p[claveVeces(id)] ?: 0
            p[claveVeces(id)] = actual + 1
            p[claveDias(id)] = escribirDias(agregarDia(leerDias(p[claveDias(id)]), hoy))
        }
    }

    /** Cuenta un acierto o un intento fallido, para saber qué le está costando. */
    suspend fun registrarRespuesta(id: String, acierto: Boolean) {
        context.progressDataStore.edit { p ->
            val clave = if (acierto) claveAciertos(id) else claveErrores(id)
            p[clave] = (p[clave] ?: 0) + 1
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

/**
 * Hoy, en días desde la época, según la zona horaria del dispositivo. Sin
 * `java.time`: no existe antes de Android 8 y la app soporta desde el 7.
 */
internal fun hoyEpoch(ahora: Long = System.currentTimeMillis()): Long =
    Math.floorDiv(ahora + java.util.TimeZone.getDefault().getOffset(ahora), 86_400_000L)

internal fun leerDias(texto: String?): Set<Long> =
    texto.orEmpty().split(",").mapNotNull { it.trim().toLongOrNull() }.toSet()

internal fun escribirDias(dias: Set<Long>): String = dias.sorted().joinToString(",")

/** Agrega el día y descarta los que quedaron fuera de la ventana de [DIAS_GUARDADOS]. */
internal fun agregarDia(dias: Set<Long>, hoy: Long): Set<Long> =
    (dias + hoy).filter { it > hoy - DIAS_GUARDADOS }.toSet()

/** Completar un nivel desbloquea el siguiente, sin retroceder nunca ni pasar de 100. */
internal fun nivelTrasCompletar(desbloqueado: Int?, nivel: Int): Int =
    maxOf(desbloqueado ?: 1, minOf(nivel + 1, 100))
