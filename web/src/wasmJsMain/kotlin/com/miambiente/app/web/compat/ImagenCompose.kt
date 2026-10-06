package androidx.compose.ui.graphics

// Compatibilidad web: `Bitmap.asImageBitmap()` para el Bitmap de Android
// que reconstruye Graficos.kt. Cada llamada toma una copia del estado
// actual (la pizarra la vuelve a pedir en cada trazo).

fun android.graphics.Bitmap.asImageBitmap(): ImageBitmap = imagen().toComposeImageBitmap()
