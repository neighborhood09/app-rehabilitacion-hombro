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

    // **ACTUALIZADO:** Este será el flujo que usaremos en StartScreen para detectar un nuevo logro.
    private val _newAchievementUnlocked = MutableStateFlow<Achievement?>(null)
    val newAchievementUnlocked = _newAchievementUnlocked.asStateFlow()

    val firstStreakSaverUsed: StateFlow<Boolean> = streakDataStore.firstStreakSaverUsed
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false)

    // Esta función solo se encarga de actualizar la racha y los escudos.
    fun onRoutineCompleted() {
        viewModelScope.launch {
            val rewarded = streakDataStore.updateStreakAndDates()
            _newRewardEarned.value = rewarded
            // **NUEVO:** Después de actualizar, comprobamos los logros.
            checkAndUnlockAchievements()
        }
    }

    // Esta función comprueba los logros y actualiza el estado si se desbloquea uno nuevo.
    fun checkAndUnlockAchievements() {
        viewModelScope.launch {
            val updatedCompletedDates = completedDates.first()
            val updatedStreakCount = streakCount.first()
            val updatedSaversCount = streakSavers.first()
            val updatedFirstTimeSaverUsed = firstStreakSaverUsed.first()
            val updatedUnlocked = unlockedAchievements.first()

            val newAchievement = AchievementData.allAchievements.firstOrNull { achievement ->
                achievement.id !in updatedUnlocked && when (achievement.isStreakBased) {
                    true -> updatedStreakCount >= achievement.progressGoal
                    false -> {
                        when (achievement.id) {
                            4 -> updatedSaversCount >= achievement.progressGoal
                            5, 7, 9 -> updatedCompletedDates.size >= achievement.progressGoal
                            10 -> updatedFirstTimeSaverUsed
                            else -> false
                        }
                    }
                }
            }

            if (newAchievement != null) {
                streakDataStore.unlockAchievement(newAchievement.id)
                _newAchievementUnlocked.value = newAchievement
            }
        }
    }

    fun resetRewardState() { _newRewardEarned.value = false }
    fun resetAchievementState() { _newAchievementUnlocked.value = null }
    fun saveUserName(name: String) { viewModelScope.launch { streakDataStore.saveUserName(name) } }
    fun useStreakSaver() { viewModelScope.launch { streakDataStore.useStreakSaverToFillYesterday() } }
    fun saveExercises(updatedExercises: List<Exercise>) { viewModelScope.launch { streakDataStore.saveExercises(updatedExercises) } }
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