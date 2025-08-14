package com.example.rehabilitacionhombro.data

import androidx.annotation.DrawableRes

data class Exercise(
    val id: Int,
    val phase: String,
    val title: String,
    @DrawableRes val imageRes: Int,
    val description: String,
    val muscle: String,
    val setsReps: String,
    val isTimed: Boolean,
    val duration: Int = 0,
    val rest: Int = 0
)
