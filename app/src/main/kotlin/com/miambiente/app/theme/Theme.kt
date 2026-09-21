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

private val tipografia = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
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
