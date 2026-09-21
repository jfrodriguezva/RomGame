package com.miambiente.app.model

import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Motor de progresión de niveles — puerto directo de lib/levels.ts.
 * 100 niveles en 10 etapas de 10; `phased`/`phasedInt` interpolan entre
 * los valores de cada etapa igual que en la versión web.
 */
const val LEVEL_COUNT = 100
const val STAGE_SIZE = 10
const val STAGE_COUNT = LEVEL_COUNT / STAGE_SIZE

data class Etapa(val numero: Int, val nombre: String, val emoji: String)

val ETAPAS = listOf(
    Etapa(1, "Primeros pasos", "🌱"),
    Etapa(2, "Ya lo entiendo", "🌿"),
    Etapa(3, "Con confianza", "🍀"),
    Etapa(4, "Más atento", "🐝"),
    Etapa(5, "Buena memoria", "🦋"),
    Etapa(6, "Manos expertas", "🌻"),
    Etapa(7, "Ojo fino", "🦉"),
    Etapa(8, "Gran reto", "🏔️"),
    Etapa(9, "Casi maestro", "🌟"),
    Etapa(10, "Maestro", "👑"),
)

fun etapaDe(nivel: Int): Int = min(STAGE_COUNT, max(1, ceil(nivel / STAGE_SIZE.toDouble()).toInt()))

fun phased(nivel: Int, paradas: List<Double>): Double {
    if (paradas.isEmpty()) return 0.0
    val etapa = etapaDe(nivel)
    val a = paradas[min(paradas.size - 1, etapa - 1)]
    val b = paradas[min(paradas.size - 1, etapa)]
    val dentro = ((nivel - 1) % STAGE_SIZE) / STAGE_SIZE.toDouble()
    return a + (b - a) * dentro
}

fun phasedInt(nivel: Int, paradas: List<Int>): Int =
    phased(nivel, paradas.map { it.toDouble() }).roundToInt()

/** Estrellas que otorga un nivel: más nivel, más estrellas (1 a 5), igual que starsForLevel(). */
fun estrellasPara(nivel: Int): Int = min(5, 1 + (nivel - 1) / 20)
