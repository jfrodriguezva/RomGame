package com.miambiente.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.model.buscarJuego
import com.miambiente.app.ui.GameShell

/**
 * En la web la pizarra todavía no está: su motor de dibujo usa Bitmap y
 * Canvas de Android. Esta pantalla ocupa su lugar mientras se porta (fase 3).
 */
@Composable
fun PizarraScreen(onVolver: () -> Unit) {
    GameShell(juego = buscarJuego("pizarra")!!, onVolver = onVolver) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                "La pizarra todavía no está disponible en la versión web.\nPruébala en el APK o en el emulador.",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
