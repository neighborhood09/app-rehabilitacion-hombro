package com.example.rehabilitacionhombro.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rehab_settings")

class StreakDataStore(context: Context) {

    private val appContext = context.applicationContext

    companion object {
        val STREAK_COUNT_KEY = intPreferencesKey("streak_count")
        val COMPLETED_DATES_KEY = stringSetPreferencesKey("completed_dates")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val STREAK_SAVERS_KEY = intPreferencesKey("streak_savers") // "Escudos"
    }

    // --- Flujos para leer los datos ---
    val streakCount: Flow<Int> = appContext.dataStore.data.map { it[STREAK_COUNT_KEY] ?: 0 }
    val completedDates: Flow<Set<String>> = appContext.dataStore.data.map { it[COMPLETED_DATES_KEY] ?: emptySet() }
    val userName: Flow<String> = appContext.dataStore.data.map { it[USER_NAME_KEY] ?: "" }
    val streakSavers: Flow<Int> = appContext.dataStore.data.map { it[STREAK_SAVERS_KEY] ?: 0 }


    // --- Funciones para escribir los datos ---

    suspend fun saveUserName(name: String) {
        appContext.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    suspend fun useStreakSaverToFillYesterday() {
        appContext.dataStore.edit { preferences ->
            val currentSavers = preferences[STREAK_SAVERS_KEY] ?: 0
            if (currentSavers > 0) {
                preferences[STREAK_SAVERS_KEY] = currentSavers - 1

                val yesterday = LocalDate.now().minusDays(1)
                val yesterdayString = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val currentDates = preferences[COMPLETED_DATES_KEY] ?: emptySet()
                preferences[COMPLETED_DATES_KEY] = currentDates + yesterdayString
            }
        }
    }

    suspend fun updateStreakAndDates(): Boolean {
        var newRewardEarned = false
        appContext.dataStore.edit { preferences ->
            val today = LocalDate.now()
            val todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

            val currentDates = preferences[COMPLETED_DATES_KEY] ?: emptySet()
            if (todayString in currentDates) return@edit

            preferences[COMPLETED_DATES_KEY] = currentDates + todayString

            val yesterday = today.minusDays(1)
            val yesterdayString = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val currentStreak = preferences[STREAK_COUNT_KEY] ?: 0
            val newStreak: Int

            if (yesterdayString in currentDates) {
                newStreak = currentStreak + 1
            } else {
                newStreak = 1
            }
            preferences[STREAK_COUNT_KEY] = newStreak

            // **LÓGICA DE RECOMPENSA ACTUALIZADA:** Gana un Escudo cada 6 días
            if (newStreak > 0 && newStreak % 6 == 0) {
                val currentSavers = preferences[STREAK_SAVERS_KEY] ?: 0
                preferences[STREAK_SAVERS_KEY] = currentSavers + 1
                newRewardEarned = true
            }
        }
        return newRewardEarned
    }
}
