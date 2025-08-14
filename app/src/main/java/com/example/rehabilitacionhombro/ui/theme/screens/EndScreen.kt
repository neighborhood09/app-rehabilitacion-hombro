package com.example.rehabilitacionhombro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.rehabilitacionhombro.R
import com.example.rehabilitacionhombro.viewmodel.StreakViewModel

@Composable
fun EndScreen(
    streakViewModel: StreakViewModel,
    onRestartClick: () -> Unit
) {
    val streakCount by streakViewModel.streakCount.collectAsState()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animacion_tick_verde))

    // **NUEVO:** Leemos si se ha ganado una recompensa
    val newRewardEarned by streakViewModel.newRewardEarned.collectAsState()

    // Este efecto se ejecuta una sola vez para actualizar la racha y comprobar si hay recompensa
    LaunchedEffect(Unit) {
        streakViewModel.onRoutineCompleted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "¡Rutina Completada!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (streakCount > 0) {
            Text(
                text = "¡Felicidades! Has alcanzado una racha de $streakCount ${if (streakCount == 1) "día" else "días"}. ¡Sigue así!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // **NUEVO:** Mensaje de recompensa que aparece si se ha ganado un Escudo
        AnimatedVisibility(visible = newRewardEarned) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Escudo",
                    tint = Color(0xFF4CAF50) // Verde
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¡Has ganado 1 Escudo Protector!",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50) // Verde
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Reseteamos el estado de la recompensa antes de volver
                streakViewModel.resetRewardState()
                onRestartClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hecho")
        }
    }
}
