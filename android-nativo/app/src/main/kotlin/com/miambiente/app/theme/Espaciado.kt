package com.miambiente.app.theme

import androidx.compose.ui.unit.dp

/**
 * Escala de espaciado compartida — antes `GameShell.kt` mezclaba 4dp, 8dp,
 * 12dp, 16dp y 24dp sueltos sin ninguna relación declarada entre pantallas.
 * No se tocan los ~98 materiales individuales (fuera de alcance), solo los
 * componentes compartidos que ya usan estos valores por nombre.
 */
val EspacioXS = 4.dp
val EspacioS = 8.dp
val EspacioM = 16.dp
val EspacioL = 24.dp
