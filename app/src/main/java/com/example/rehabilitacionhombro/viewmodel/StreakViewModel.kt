package com.example.rehabilitacionhombro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rehabilitacionhombro.data.StreakDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StreakViewModel(private val streakDataStore: StreakDataStore) : ViewModel() {

    // --- Flujos de datos para la UI ---
    val streakCount: StateFlow<Int> = streakDataStore.streakCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val completedDates: StateFlow<Set<String>> = streakDataStore.completedDates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val userName: StateFlow<String> = streakDataStore.userName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    // **NUEVO:** Exponemos el número de Escudos para que la UI lo pueda leer
    val streakSavers: StateFlow<Int> = streakDataStore.streakSavers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Flujo para comunicar a la UI si se ha ganado una recompensa
    private val _newRewardEarned = MutableStateFlow(false)
    val newRewardEarned = _newRewardEarned.asStateFlow()


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

    fun saveUserName(name: String) {
        viewModelScope.launch {
            streakDataStore.saveUserName(name)
        }
    }

    // **NUEVO:** Función para que la UI pueda gastar un Escudo
    fun useStreakSaver() {
        viewModelScope.launch {
            streakDataStore.useStreakSaverToFillYesterday()
        }
    }
}

// El Factory no necesita cambios
class StreakViewModelFactory(private val streakDataStore: StreakDataStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreakViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StreakViewModel(streakDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
