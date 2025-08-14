package com.example.rehabilitacionhombro.data

import androidx.annotation.DrawableRes
// --- LÍNEA NUEVA A AÑADIR ---
import kotlinx.serialization.Serializable

// --- LÍNEA NUEVA A AÑADIR ---
@Serializable // Esta etiqueta permite que el objeto se guarde como texto (JSON)
data class Exercise(
    val id: Int,
    val phase: String,
    val title: String,
    // La anotación @DrawableRes no se puede serializar, la gestionaremos de otra forma
    val imageResName: String, // Guardaremos el nombre del recurso en lugar del ID
    val description: String,
    val muscle: String,
    var setsReps: String, // Cambiado a 'var' para que sea modificable
    val isTimed: Boolean,
    var duration: Int = 0, // Cambiado a 'var' para que sea modificable
    val rest: Int = 0
) {
    // Esta función nos ayudará a obtener el ID real de la imagen a partir de su nombre
    @Transient
    val imageRes: Int = 0
}
