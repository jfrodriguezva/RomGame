package com.rominagame.core

enum class CurriculumMechanic { QUIZ, SORT, ORDER, COUNT }
data class CurriculumMode(val title: String, val mechanic: CurriculumMechanic)
data class CurriculumFamily(val id: String, val title: String, val modes: List<CurriculumMode>)
data class CurriculumChallenge(val prompt: String, val options: List<String>, val answer: Int)

private fun modes(vararg values: Pair<String, CurriculumMechanic>) = values.map { CurriculumMode(it.first, it.second) }

val curriculumFamilies = listOf(
    CurriculumFamily("numeros-cantidades", "Números y cantidades", modes(
        "Contar" to CurriculumMechanic.COUNT, "Husos" to CurriculumMechanic.COUNT, "Barras numéricas" to CurriculumMechanic.ORDER,
        "Números" to CurriculumMechanic.QUIZ, "Tabla del cien" to CurriculumMechanic.COUNT, "Banco dorado" to CurriculumMechanic.COUNT,
        "Pares e impares" to CurriculumMechanic.SORT, "Reloj" to CurriculumMechanic.QUIZ, "Mitades" to CurriculumMechanic.QUIZ,
    )),
    CurriculumFamily("clasifica-mundo", "Clasifica el mundo", modes(
        "Seres vivos" to CurriculumMechanic.SORT, "Hábitat" to CurriculumMechanic.SORT, "Dieta animal" to CurriculumMechanic.SORT,
        "Fruta o verdura" to CurriculumMechanic.SORT, "Estados del agua" to CurriculumMechanic.SORT,
        "Día o noche" to CurriculumMechanic.SORT, "Transportes" to CurriculumMechanic.SORT,
    )),
    CurriculumFamily("naturaleza-planeta", "Naturaleza y planeta", modes(
        "Animales" to CurriculumMechanic.QUIZ, "Partes de la planta" to CurriculumMechanic.QUIZ, "Continentes" to CurriculumMechanic.QUIZ,
        "Tierra y agua" to CurriculumMechanic.SORT, "Sistema solar" to CurriculumMechanic.ORDER, "Tiempo" to CurriculumMechanic.QUIZ,
        "Banderas" to CurriculumMechanic.QUIZ,
    )),
    CurriculumFamily("personas-comunidad", "Personas y comunidad", modes(
        "Cuerpo" to CurriculumMechanic.QUIZ, "Cara" to CurriculumMechanic.QUIZ, "Emociones" to CurriculumMechanic.QUIZ,
        "Oficios" to CurriculumMechanic.QUIZ, "Mesa de silencio" to CurriculumMechanic.ORDER,
    )),
    CurriculumFamily("vida-practica", "Vida práctica", modes(
        "Transferencias" to CurriculumMechanic.SORT, "Pinza" to CurriculumMechanic.SORT, "Tamaños" to CurriculumMechanic.ORDER,
        "Rutina" to CurriculumMechanic.ORDER, "Poner la mesa" to CurriculumMechanic.ORDER, "Lavado de manos" to CurriculumMechanic.ORDER,
    )),
)

fun curriculumFamily(id: String) = curriculumFamilies.first { it.id == id }

private val quizBank = mapOf(
    "numeros-cantidades" to listOf("¿Cuántos hay?", "¿Cuántos husos?", "Ordena las barras", "Selecciona el número 5", "Encuentra 25", "¿Cuántas decenas hay?", "¿Cuál es par?", "¿Qué hora marca?", "Selecciona una mitad"),
    "naturaleza-planeta" to listOf("¿Cuál es un mamífero?", "¿Dónde están las raíces?", "¿Cuál es América?", "¿Dónde hay más agua?", "Ordena los planetas", "¿Qué tiempo hace?", "¿Cuál es la bandera indicada?"),
    "personas-comunidad" to listOf("Señala la mano", "Señala los ojos", "¿Quién está alegre?", "¿Quién cura personas?", "Ordena la actividad silenciosa"),
)

fun curriculumChallenge(familyId: String, mode: Int, level: Int): CurriculumChallenge {
    val prompt = quizBank[familyId]?.getOrNull(mode) ?: curriculumFamily(familyId).modes[mode].title
    val base = when (familyId) {
        "numeros-cantidades" -> listOf("5", "4", "6", "3")
        "naturaleza-planeta" -> listOf("Correcto", "Opción B", "Opción C", "Opción D")
        "personas-comunidad" -> listOf("Correcto", "Opción B", "Opción C", "Opción D")
        else -> listOf("Grupo A", "Grupo B", "Grupo C", "Grupo D")
    }
    val shift = (level + mode) % 4
    val options = base.drop(shift) + base.take(shift)
    return CurriculumChallenge(prompt, options, options.indexOf(base.first()))
}

fun sortLabels(familyId: String, mode: Int): Pair<String, String> = when (familyId) {
    "numeros-cantidades" -> "PAR" to "IMPAR"
    "clasifica-mundo" -> listOf("VIVO" to "NO VIVO", "TIERRA" to "AGUA", "HERBÍVORO" to "CARNÍVORO", "FRUTA" to "VERDURA", "SÓLIDO" to "LÍQUIDO", "DÍA" to "NOCHE", "TIERRA" to "AIRE")[mode]
    "naturaleza-planeta" -> "TIERRA" to "AGUA"
    else -> "DESTINO A" to "DESTINO B"
}

fun sortItem(familyId: String, mode: Int, step: Int): SortItem {
    val destination = step % 2
    val label = when (familyId) {
        "numeros-cantidades" -> if (destination == 0) "8" else "7"
        "clasifica-mundo" -> listOf(
            listOf("Árbol", "Roca"), listOf("León", "Pez"), listOf("Vaca", "León"), listOf("Manzana", "Zanahoria"),
            listOf("Hielo", "Agua"), listOf("Sol", "Luna"), listOf("Auto", "Avión"),
        )[mode][destination]
        "naturaleza-planeta" -> if (destination == 0) "Montaña" else "Océano"
        else -> if (destination == 0) "Cuchara" else "Vaso"
    }
    return SortItem(label, destination)
}

fun orderItems(familyId: String, mode: Int, level: Int): List<String> {
    val all = when (familyId) {
        "numeros-cantidades" -> listOf("1", "2", "3", "4", "5", "6", "7")
        "naturaleza-planeta" -> listOf("Mercurio", "Venus", "Tierra", "Marte", "Júpiter")
        "personas-comunidad" -> listOf("Respira", "Escucha", "Camina", "Descansa")
        else -> when (mode) {
            2 -> listOf("Grande", "Mediano", "Pequeño")
            3 -> listOf("Despertar", "Vestirse", "Desayunar", "Salir")
            4 -> listOf("Plato", "Vaso", "Cubiertos", "Servilleta")
            else -> listOf("Mojar", "Enjabonar", "Frotar", "Enjuagar", "Secar")
        }
    }
    return all.take((3 + level / 5).coerceAtMost(all.size))
}

fun countTarget(level: Int) = (2 + level / 2).coerceAtMost(12)
