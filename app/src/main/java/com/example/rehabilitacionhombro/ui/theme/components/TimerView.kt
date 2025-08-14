package com.example.rehabilitacionhombro.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun TimerView(key: Any, totalTime: Long, restTime: Long, modifier: Modifier = Modifier) {
    var timeLeft by remember(key) { mutableStateOf(totalTime) }
    var isRunning by remember(key) { mutableStateOf(false) }
    var isResting by remember(key) { mutableStateOf(false) }

    LaunchedEffect(key1 = isRunning, key2 = timeLeft, key3 = isResting) {
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1000L
            if (timeLeft <= 0) {
                isRunning = false
                if (!isResting && restTime > 0) {
                    isResting = true
                    timeLeft = restTime
                }
            }
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if(isResting) "Descanso: ${(timeLeft / 1000)}s" else "Tiempo: ${(timeLeft / 1000)}s",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            if (timeLeft <= 0 && !isResting) { // Reset and start only if not in rest
                timeLeft = totalTime
            }
            isRunning = !isRunning
        }) {
            Text(if (isRunning) "Pausa" else if (timeLeft <= 0 && !isResting) "Iniciar" else "Continuar")
        }
    }
}
