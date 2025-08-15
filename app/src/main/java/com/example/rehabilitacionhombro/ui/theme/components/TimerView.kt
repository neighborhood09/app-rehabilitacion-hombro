// Fichero: app/src/main/java/com/example/rehabilitacionhombro/ui/components/TimerView.kt
package com.example.rehabilitacionhombro.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TimerView(
    key: Any,
    totalTime: Long,
    restTime: Long,
    modifier: Modifier = Modifier
) {
    var timeLeft by remember(key) { mutableStateOf(totalTime) }
    var isRunning by remember(key) { mutableStateOf(false) }
    var isResting by remember(key) { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(key1 = isRunning, key2 = timeLeft) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1000L
            }
            isRunning = false
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)

            if (isResting) {
                isResting = false
                timeLeft = totalTime
            } else if (!isResting && restTime > 0) {
                isResting = true
                timeLeft = restTime
                isRunning = true
            }
        }
    }

    // **ACTUALIZADO:** Animamos el progreso de la barra de forma suave
    val progress = animateFloatAsState(
        targetValue = if (isResting)
            (restTime - timeLeft).toFloat() / restTime
        else
            (totalTime - timeLeft).toFloat() / totalTime
    )

    // **NUEVO:** Animación de color de la barra de progreso
    val animatedColor = animateColorAsState(
        targetValue = when {
            isResting -> Color(0xFFE53935) // Rojo para el descanso
            timeLeft > 10000L -> Color(0xFF4CAF50) // Verde si queda mucho tiempo
            timeLeft > 5000L -> Color(0xFFFFC107) // Amarillo si queda poco
            else -> Color(0xFFE53935) // Rojo si queda muy poco
        }
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isResting) "Descanso: " else "Tiempo: ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = (timeLeft / 1000L).toString() + "s",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.width(16.dp))

            IconButton(
                onClick = {
                    if (timeLeft <= 0) {
                        timeLeft = totalTime
                        isResting = false
                    }
                    isRunning = !isRunning
                }
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isRunning) "Pausa" else "Iniciar",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // **ACTUALIZADO:** La barra de progreso ahora usa el color animado
        LinearProgressIndicator(
            progress = { progress.value },
            modifier = Modifier.fillMaxWidth(),
            color = animatedColor.value,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}