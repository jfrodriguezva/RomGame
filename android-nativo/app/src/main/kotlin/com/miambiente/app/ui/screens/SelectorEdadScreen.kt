package com.miambiente.app.ui.screens

import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miambiente.app.data.LocalServices
import com.miambiente.app.theme.Papel
import com.miambiente.app.theme.TextoSuave
import com.miambiente.app.theme.Tinta
import kotlinx.coroutines.launch

private data class Edad(val valor: Int, val emoji: String)

private val EDADES = listOf(
    Edad(2, "🐣"), Edad(3, "🌱"), Edad(4, "🌿"), Edad(5, "🍀"), Edad(6, "🌟"),
)

/**
 * Primera pantalla, equivalente nativo de components/SelectorEdad.tsx.
 * Aquí un toque SIEMPRE se registra bien — es un botón de Compose de
 * verdad, no un elemento dentro de un WebView donde un gesto ambiguo
 * puede cancelarse a medio camino (la causa más probable del bug
 * reportado en la versión empaquetada con Capacitor).
 */
@Composable
fun SelectorEdadScreen(onElegida: () -> Unit) {
    val services = LocalServices.current
    val scope = rememberCoroutineScope()
    var eligiendo by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Papel)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        MascotaFlotante()
        Text(
            "¿Cuántos años tiene?",
            style = MaterialTheme.typography.headlineMedium,
            color = Tinta,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        Text(
            "Así mostramos primero los materiales que le quedan bien. Se puede cambiar cuando quieras.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSuave,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(220.dp),
        ) {
            items(EDADES) { e ->
                BotonEdad(
                    edad = e,
                    seleccionando = eligiendo == e.valor,
                    habilitado = eligiendo == null,
                    onClick = {
                        eligiendo = e.valor
                        scope.launch {
                            services.settings.setEdad(e.valor)
                            onElegida()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun BotonEdad(edad: Edad, seleccionando: Boolean, habilitado: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        shape = RoundedCornerShape(28.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = if (seleccionando) Color(0xFFE7E0D4) else Color.White,
            contentColor = Tinta,
            disabledContainerColor = Color(0xFFF3EEE4),
        ),
        modifier = Modifier.size(96.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(edad.emoji, fontSize = 28.sp)
            Text("${edad.valor}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text("años", fontSize = 10.sp, color = TextoSuave)
        }
    }
}

@Composable
private fun MascotaFlotante() {
    val transicion = rememberInfiniteTransition(label = "mascota")
    val y by transicion.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "y",
    )
    Text("🦉", fontSize = 48.sp, modifier = Modifier.offset(y = y.dp))
}
