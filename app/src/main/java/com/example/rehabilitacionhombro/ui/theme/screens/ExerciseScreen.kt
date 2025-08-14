package com.example.rehabilitacionhombro.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rehabilitacionhombro.R
import com.example.rehabilitacionhombro.data.Exercise
import com.example.rehabilitacionhombro.ui.components.TimerView
import com.example.rehabilitacionhombro.viewmodel.StreakViewModel

@Composable
fun ExerciseScreen(
    streakViewModel: StreakViewModel,
    exercise: Exercise,
    exerciseIndex: Int,
    exerciseCount: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val exercises by streakViewModel.exercises.collectAsState()
    val context = LocalContext.current

    var setsReps by remember(exercise.id) { mutableStateOf(exercise.setsReps) }
    var duration by remember(exercise.id) { mutableStateOf(exercise.duration.toString()) }

    val saveChanges = {
        val updatedExercises = exercises.toMutableList()
        val exerciseToUpdate = updatedExercises.getOrNull(exerciseIndex)
        exerciseToUpdate?.let {
            it.setsReps = setsReps
            it.duration = duration.toIntOrNull() ?: it.duration
            streakViewModel.saveExercises(updatedExercises)
        }
    }

    val imageResId = remember(exercise.imageResName) {
        getImageResourceId(context, exercise.imageResName)
    }

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
            painter = painterResource(id = if (imageResId != 0) imageResId else R.drawable.ic_launcher_background),
            contentDescription = exercise.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- EXPLICACIONES FIJAS (AÑADIDAS DE NUEVO) ---
        Text("Músculos: ${exercise.muscle}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(exercise.description, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp)) // Más espacio antes de los campos editables

        // --- CAMPOS EDITABLES ---
        OutlinedTextField(
            value = setsReps,
            onValueChange = { setsReps = it },
            label = { Text("Series y Repeticiones (Editable)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (exercise.isTimed) {
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duración (segundos, Editable)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TimerView(
                key = exercise.id,
                totalTime = (duration.toLongOrNull() ?: exercise.duration.toLong()) * 1000,
                restTime = exercise.rest.toLong() * 1000,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                saveChanges()
                onPrevious()
            }, enabled = exerciseIndex > 0) {
                Text("Anterior")
            }
            Button(onClick = {
                saveChanges()
                onNext()
            }) {
                Text(if (exerciseIndex < exerciseCount - 1) "Siguiente" else "Finalizar")
            }
        }
    }
}

private fun getImageResourceId(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}
