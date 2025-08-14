// Fichero: app/src/main/java/com/example/rehabilitacionhombro/viewmodel/StreakViewModel.kt
package com.example.rehabilitacionhombro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rehabilitacionhombro.data.Achievement
import com.example.rehabilitacionhombro.data.AchievementData
import com.example.rehabilitacionhombro.data.Exercise
import com.example.rehabilitacionhombro.data.StreakDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StreakViewModel(private val streakDataStore: StreakDataStore) : ViewModel() {

    // --- Flujos de datos para la UI ---
    val streakCount: StateFlow<Int> = streakDataStore.streakCount
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    val completedDates: StateFlow<Set<String>> = streakDataStore.completedDates
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptySet())

    val userName: StateFlow<String> = streakDataStore.userName
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = "")

    val streakSavers: StateFlow<Int> = streakDataStore.streakSavers
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = 0)

    val exercises: StateFlow<List<Exercise>> = streakDataStore.exercises
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    private val _newRewardEarned = MutableStateFlow(false)
    val newRewardEarned = _newRewardEarned.asStateFlow()

    val canUseShield: StateFlow<Boolean> = combine(streakSavers, completedDates) { savers, dates ->
        val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        savers > 0 && yesterday !in dates && today !in dates
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false)

    val unlockedAchievements = streakDataStore.unlockedAchievements
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptySet())

    private val _newAchievementUnlocked = MutableStateFlow<Achievement?>(null)
    val newAchievementUnlocked = _newAchievementUnlocked.asStateFlow()

    val firstStreakSaverUsed: StateFlow<Boolean> = streakDataStore.firstStreakSaverUsed
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false)


    // --- Acciones que la UI puede llamar ---

    fun onRoutineCompleted() {
        viewModelScope.launch {
            val rewarded = streakDataStore.updateStreakAndDates()
            _newRewardEarned.value = rewarded
        }
    }

    fun resetRewardState() {
        _newRewardEarned.value = false
    }

    fun resetAchievementState() {
        _newAchievementUnlocked.value = null
    }

    fun saveUserName(name: String) {
        viewModelScope.launch {
            streakDataStore.saveUserName(name)
        }
    }

    fun useStreakSaver() {
        viewModelScope.launch {
            streakDataStore.useStreakSaverToFillYesterday()
        }
    }

    fun saveExercises(updatedExercises: List<Exercise>) {
        viewModelScope.launch {
            streakDataStore.saveExercises(updatedExercises)
        }
    }

    // **ACTUALIZADO:** Función que comprueba y desbloquea logros
    fun checkAndUnlockAchievements() {
        viewModelScope.launch {
            val currentStreak = streakCount.value
            val completedCount = completedDates.value.size
            val saversCount = streakSavers.value
            val firstTimeSaverUsed = firstStreakSaverUsed.value

            // **CORREGIDO:** Obtenemos los logros desbloqueados más recientes directamente del DataStore
            val currentUnlocked = streakDataStore.unlockedAchievements.first()

            AchievementData.allAchievements.forEach { achievement ->
                if (achievement.id !in currentUnlocked) {
                    val isUnlocked = when (achievement.isStreakBased) {
                        true -> currentStreak >= achievement.progressGoal
                        false -> {
                            when (achievement.id) {
                                4 -> saversCount >= achievement.progressGoal
                                5, 7, 9 -> completedCount >= achievement.progressGoal
                                10 -> firstTimeSaverUsed
                                else -> false
                            }
                        }
                    }

                    if (isUnlocked) {
                        streakDataStore.unlockAchievement(achievement.id)
                        _newAchievementUnlocked.value = achievement
                    }
                }
            }
        }
    }
}

class StreakViewModelFactory(private val streakDataStore: StreakDataStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreakViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StreakViewModel(streakDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}