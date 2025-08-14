// Fichero: app/src/main/java/com/example/rehabilitacionhombro/data/Achievement.kt
package com.example.rehabilitacionhombro.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Achievement(
    val id: Int,
    val name: String,
    val description: String,
    val iconResName: String,
    val isStreakBased: Boolean = true,
    val progressGoal: Int
)

object AchievementData {
    val allAchievements = listOf(
        Achievement(
            id = 1,
            name = "Medalla de Bronce a la Constancia",
            description = "Tu primer hito en el camino a la recuperación. ¡Sigue así!",
            iconResName = "ic_achievement_bronze",
            progressGoal = 7
        ),
        Achievement(
            id = 2,
            name = "Medalla de Plata del Mes",
            description = "Un mes de disciplina inquebrantable. Tu hombro te lo agradecerá.",
            iconResName = "ic_achievement_silver",
            progressGoal = 30
        ),
        Achievement(
            id = 3,
            name = "Medalla de Oro del Hábito",
            description = "Has convertido la rehabilitación en un hábito. ¡Ya eres imparable!",
            iconResName = "ic_achievement_gold",
            progressGoal = 90
        ),
        Achievement(
            id = 4,
            name = "Guardián de la Racha",
            description = "Has sabido proteger tu esfuerzo cuando más lo necesitabas.",
            iconResName = "ic_achievement_guardian",
            progressGoal = 3,
            isStreakBased = false
        ),
        Achievement(
            id = 5,
            name = "Héroe de la Rehabilitación",
            description = "La suma de tu esfuerzo diario te ha convertido en un verdadero héroe.",
            iconResName = "ic_achievement_hero",
            progressGoal = 50,
            isStreakBased = false
        ),
        Achievement(
            id = 6,
            name = "Medalla del Semestre",
            description = "Seis meses de dedicación. ¡Mira cuánto has avanzado!",
            iconResName = "ic_achievement_semester",
            progressGoal = 180
        ),
        Achievement(
            id = 7,
            name = "Maestro de la Rutina",
            description = "¡Has realizado 100 rutinas! Tu constancia es digna de admiración.",
            iconResName = "ic_achievement_master",
            progressGoal = 100,
            isStreakBased = false
        ),
        Achievement(
            id = 8,
            name = "El Año del Fénix",
            description = "Has resurgido de las cenizas. Un año de dedicación total a tu salud.",
            iconResName = "ic_achievement_fenix",
            progressGoal = 365
        ),
        Achievement(
            id = 9,
            name = "Conquistador",
            description = "¡Has conquistado tu rutina! La fuerza de tu hombro es un reflejo de tu voluntad.",
            iconResName = "ic_achievement_conqueror",
            progressGoal = 1,
            isStreakBased = false
        ),
        // **NUEVO LOGRO AÑADIDO**
        Achievement(
            id = 10,
            name = "Superviviente",
            description = "Has usado un Escudo para mantener viva tu racha. ¡Nada te detiene!",
            iconResName = "ic_achievement_survivor",
            progressGoal = 1,
            isStreakBased = false
        )
    )
}