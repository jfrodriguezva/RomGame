package com.miambiente.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "ajustes")

/**
 * Equivalente nativo de lib/settings.ts (zustand + persist en localStorage).
 * DataStore es async y basado en Flow por diseño — no hay analogía directa
 * a la lectura síncrona de zustand, así que la UI observa los cambios con
 * `collectAsState()` en vez de leer un valor de una vez.
 */
data class Settings(
    val edad: Int? = null,
    val nombre: String = "",
    val sonido: Boolean = true,
    val voz: Boolean = true,
    val vibracion: Boolean = true,
    val musica: Boolean = true,
    val calma: Boolean = false,
)

class SettingsStore(private val context: Context) {
    private object Claves {
        val EDAD = intPreferencesKey("edad")
        val NOMBRE = stringPreferencesKey("nombre")
        val SONIDO = booleanPreferencesKey("sonido")
        val VOZ = booleanPreferencesKey("voz")
        val VIBRACION = booleanPreferencesKey("vibracion")
        val MUSICA = booleanPreferencesKey("musica")
        val CALMA = booleanPreferencesKey("calma")
    }

    val settings: Flow<Settings> = context.settingsDataStore.data.map { p ->
        Settings(
            edad = p[Claves.EDAD]?.takeIf { it > 0 },
            nombre = p[Claves.NOMBRE] ?: "",
            sonido = p[Claves.SONIDO] ?: true,
            voz = p[Claves.VOZ] ?: true,
            vibracion = p[Claves.VIBRACION] ?: true,
            musica = p[Claves.MUSICA] ?: true,
            calma = p[Claves.CALMA] ?: false,
        )
    }

    suspend fun setEdad(edad: Int) {
        context.settingsDataStore.edit { it[Claves.EDAD] = edad }
    }

    suspend fun setNombre(nombre: String) {
        context.settingsDataStore.edit { it[Claves.NOMBRE] = nombre }
    }

    suspend fun toggleSonido() = toggle(Claves.SONIDO)
    suspend fun toggleVoz() = toggle(Claves.VOZ)
    suspend fun toggleVibracion() = toggle(Claves.VIBRACION)
    suspend fun toggleMusica() = toggle(Claves.MUSICA)
    suspend fun toggleCalma() = toggle(Claves.CALMA)

    private suspend fun toggle(clave: androidx.datastore.preferences.core.Preferences.Key<Boolean>) {
        context.settingsDataStore.edit { it[clave] = !(it[clave] ?: true) }
    }
}
