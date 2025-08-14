package com.example.rehabilitacionhombro.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
// Estas son las importaciones que hemos corregido
import com.example.rehabilitacionhombro.data.Exercise
import com.example.rehabilitacionhombro.ui.components.TimerView

@Composable
fun ExerciseScreen(
    exercise: Exercise,
    exerciseIndex: Int,
    exerciseCount: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(exercise.phase, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (exerciseIndex.toFloat() + 1) / exerciseCount },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(exercise.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = exercise.imageRes),
            contentDescription = exercise.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (exercise.isTimed) {
            TimerView(
                key = exercise.id,
                totalTime = exercise.duration.toLong() * 1000,
                restTime = exercise.rest.toLong() * 1000,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text("Músculos: ${exercise.muscle}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(exercise.description, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Series/Reps: ${exercise.setsReps}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onPrevious, enabled = exerciseIndex > 0) {
                Text("Anterior")
            }
            Button(onClick = onNext) {
                Text(if (exerciseIndex < exerciseCount - 1) "Siguiente" else "Finalizar")
            }
        }
    }
}