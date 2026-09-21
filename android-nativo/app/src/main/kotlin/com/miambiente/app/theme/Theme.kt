package com.miambiente.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val esquemaClaro = lightColorScheme(
    background = Papel,
    surface = Blanco,
    onBackground = Tinta,
    onSurface = Tinta,
    primary = Tinta,
)

// Antes solo estaban los 5 estilos que alguna pantalla usaba explícitamente;
// el resto (`headlineSmall`, `titleMedium`, `titleSmall`, `bodySmall`,
// `labelLarge`, `labelMedium`) los rellenaba Material3 con su tamaño/peso
// por defecto — funcionaban, pero sin la identidad tipográfica del resto
// (ExtraBold en títulos, Bold en labels). Se completan aquí para que Home y
// GameShell (los que ya los usan por nombre) hereden el mismo criterio.
private val tipografia = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.sp),
)

@Composable
fun MiAmbienteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = esquemaClaro,
        typography = tipografia,
        content = content,
    )
}
