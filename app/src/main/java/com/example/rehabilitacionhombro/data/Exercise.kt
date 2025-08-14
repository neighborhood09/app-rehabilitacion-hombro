// Fichero: app/src/main/java/com/example/rehabilitacionhombro/data/Exercise.kt
package com.example.rehabilitacionhombro.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Exercise(
    val id: Int,
    val phase: String,
    val title: String,
    val imageResName: String,
    val description: String,
    val muscle: String,
    // **CAMBIADO:** Ahora sets y reps son números enteros.
    var sets: Int = 0,
    var reps: Int = 0,
    val isTimed: Boolean,
    var duration: Int = 0, // La duración sigue siendo en segundos, para mayor simplicidad en el temporizador
    val rest: Int = 0
) {
    // Esta función nos ayudará a obtener el ID real de la imagen a partir de su nombre
    @Transient
    val imageRes: Int = 0

    // **NUEVO:** Propiedad calculada para mostrar los valores en la UI de manera clara.
    val setsRepsDisplay: String
        get() = "${sets} series de ${reps} repeticiones"
}