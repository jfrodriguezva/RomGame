package androidx.compose.ui.platform

// Compatibilidad web: `LocalContext` solo existe en Compose para Android.
// La pizarra lo pide para la galería y para compartir; aquí devuelve un
// Context web (ver android/content en Contexto.kt).

import androidx.compose.runtime.staticCompositionLocalOf

val LocalContext = staticCompositionLocalOf { android.content.Context() }
