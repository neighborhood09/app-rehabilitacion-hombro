// Fichero: app/src/main/java/com/example/rehabilitacionhombro/ui/screens/ExerciseScreen.kt
package com.example.rehabilitacionhombro.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rehabilitacionhombro.R
import com.example.rehabilitacionhombro.data.Exercise
import com.example.rehabilitacionhombro.ui.components.TimerView
import com.example.rehabilitacionhombro.viewmodel.StreakViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    var showEditDialog by remember { mutableStateOf(false) }
    var currentSets by remember(exercise.id) { mutableStateOf(exercise.sets) }
    var currentReps by remember(exercise.id) { mutableStateOf(exercise.reps) }
    var currentDuration by remember(exercise.id) { mutableStateOf(exercise.duration) }

    val imageResId = remember(exercise.imageResName) {
        getImageResourceId(context, exercise.imageResName)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Fila para el progreso y el botón de ajustes (Parte superior Fija)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(exercise.phase, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (exerciseIndex.toFloat() + 1) / exerciseCount },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustar valores",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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

            // Columna con solo la descripción y campos de valores que se desplazan
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Ocupa todo el espacio restante de forma flexible
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp)
            ) {
                Text("Músculos: ${exercise.muscle}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(exercise.description, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(24.dp))

                if (currentSets > 0) {
                    Text("Series: ${currentSets}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
                if (currentReps > 0) {
                    if (currentSets > 0) { Spacer(modifier = Modifier.height(8.dp)) }
                    Text("Repeticiones: ${currentReps}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
                if (exercise.isTimed && currentDuration > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Duración: ${currentDuration}s", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (exercise.isTimed) {
                    TimerView(
                        key = exercise.id,
                        totalTime = currentDuration.toLong() * 1000,
                        restTime = exercise.rest.toLong() * 1000
                    )
                }
            }
        }

        // Fila de botones de navegación fijos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = onPrevious,
                enabled = exerciseIndex > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text("Anterior")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    streakViewModel.saveExercises(
                        exercises.toMutableList().also {
                            it[exerciseIndex] = it[exerciseIndex].copy(
                                sets = currentSets,
                                reps = currentReps,
                                duration = currentDuration
                            )
                        }
                    )
                    onNext()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (exerciseIndex < exerciseCount - 1) "Siguiente" else "Finalizar")
            }
        }
    }

    if (showEditDialog) {
        var expandedSets by remember { mutableStateOf(false) }
        var expandedReps by remember { mutableStateOf(false) }
        var expandedMinutes by remember { mutableStateOf(false) }
        var expandedSeconds by remember { mutableStateOf(false) }

        var setsDialog by remember { mutableStateOf(currentSets) }
        var repsDialog by remember { mutableStateOf(currentReps) }
        var durationMinutesDialog by remember { mutableStateOf(currentDuration / 60) }
        var durationSecondsDialog by remember { mutableStateOf(currentDuration % 60) }

        val setsOptions = (0..10).toList()
        val repsOptions = (0..30).toList()
        val minutesOptions = (0..5).toList()
        val secondsOptions = (0..59).toList()

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = "Ajustar ejercicio") },
            text = {
                Column {
                    Text("Series", style = MaterialTheme.typography.titleSmall)
                    ExposedDropdownMenuBox(
                        expanded = expandedSets,
                        onExpandedChange = { expandedSets = !expandedSets },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = setsDialog.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Series") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSets) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSets,
                            onDismissRequest = { expandedSets = false }
                        ) {
                            setsOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.toString()) },
                                    onClick = {
                                        setsDialog = option
                                        expandedSets = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Repeticiones", style = MaterialTheme.typography.titleSmall)
                    ExposedDropdownMenuBox(
                        expanded = expandedReps,
                        onExpandedChange = { expandedReps = !expandedReps },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = repsDialog.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Repeticiones") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedReps) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedReps,
                            onDismissRequest = { expandedReps = false }
                        ) {
                            repsOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.toString()) },
                                    onClick = {
                                        repsDialog = option
                                        expandedReps = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (exercise.isTimed) {
                        Text("Duración", style = MaterialTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expandedMinutes,
                                onExpandedChange = { expandedMinutes = !expandedMinutes },
                                modifier = Modifier.weight(1f)
                            ) {
                                TextField(
                                    value = durationMinutesDialog.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Min") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMinutes) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedMinutes,
                                    onDismissRequest = { expandedMinutes = false }
                                ) {
                                    minutesOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.toString()) },
                                            onClick = {
                                                durationMinutesDialog = option
                                                expandedMinutes = false
                                            }
                                        )
                                    }
                                }
                            }
                            Text(":", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                            ExposedDropdownMenuBox(
                                expanded = expandedSeconds,
                                onExpandedChange = { expandedSeconds = !expandedSeconds },
                                modifier = Modifier.weight(1f)
                            ) {
                                TextField(
                                    value = durationSecondsDialog.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Seg") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSeconds) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedSeconds,
                                    onDismissRequest = { expandedSeconds = false }
                                ) {
                                    secondsOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.toString()) },
                                            onClick = {
                                                durationSecondsDialog = option
                                                expandedSeconds = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        currentSets = setsDialog
                        currentReps = repsDialog
                        currentDuration = durationMinutesDialog * 60 + durationSecondsDialog
                        showEditDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
private fun getImageResourceId(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}