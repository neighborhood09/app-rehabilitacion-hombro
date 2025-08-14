// Fichero: app/src/main/java/com/example/rehabilitacionhombro/ui/components/TimerView.kt
package com.example.rehabilitacionhombro.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedback
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

    // **NUEVO:** Lógica para el cronómetro con un ciclo completo (ejercicio + descanso)
    LaunchedEffect(key1 = isRunning, key2 = timeLeft) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1000L
            }
            // Cuando el temporizador llega a 0
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

    val progress = animateFloatAsState(
        targetValue = if (totalTime > 0) timeLeft.toFloat() / (if (isResting) restTime.toFloat() else totalTime.toFloat()) else 0f
    )

    // **NUEVO:** Animación de color según el tiempo restante
    val animatedColor = animateColorAsState(
        targetValue = when {
            isResting -> Color(0xFFF44336) // Rojo para el descanso
            timeLeft > 10000L -> Color(0xFF4CAF50) // Verde
            timeLeft > 5000L -> Color(0xFFFFC107) // Amarillo
            else -> Color(0xFFE53935) // Rojo intenso al final
        }
    )

    Box(
        modifier = modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        // **NUEVO:** Implementación del cronómetro circular
        CircularProgressIndicator(
            progress = { 1f - progress.value },
            modifier = Modifier.size(150.dp),
            color = animatedColor.value,
            strokeCap = StrokeCap.Round,
            strokeWidth = 8.dp
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = (timeLeft / 1000L).toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isResting) "Descanso" else "Tiempo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    // **NUEVO:** Botones con iconos y lógica mejorada
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isRunning) {
            Button(
                onClick = {
                    if (timeLeft <= 0) { // Reset y empezar
                        timeLeft = totalTime
                        isResting = false
                    }
                    isRunning = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Iniciar"
                )
                Spacer(Modifier.width(8.dp))
                Text("Iniciar")
            }
        } else {
            Button(onClick = { isRunning = false }) {
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = "Pausa"
                )
                Spacer(Modifier.width(8.dp))
                Text("Pausa")
            }
        }
    }
}