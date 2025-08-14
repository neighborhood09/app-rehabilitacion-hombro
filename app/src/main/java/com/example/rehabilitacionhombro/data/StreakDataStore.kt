// Fichero: app/src/main/java/com/example/rehabilitacionhombro/data/StreakDataStore.kt
package com.example.rehabilitacionhombro.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rehab_settings")

class StreakDataStore(context: Context) {

    private val appContext = context.applicationContext

    private companion object {
        val STREAK_COUNT_KEY = intPreferencesKey("streak_count")
        val COMPLETED_DATES_KEY = stringSetPreferencesKey("completed_dates")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val STREAK_SAVERS_KEY = intPreferencesKey("streak_savers")
        val EXERCISES_LIST_KEY = stringPreferencesKey("exercises_list")
        val UNLOCKED_ACHIEVEMENTS_KEY = stringSetPreferencesKey("unlocked_achievements")
        val STREAK_SAVER_USED_KEY = booleanPreferencesKey("first_streak_saver_used")
    }

    val streakCount: Flow<Int> = appContext.dataStore.data.map { preferences -> preferences[STREAK_COUNT_KEY] ?: 0 }
    val completedDates: Flow<Set<String>> = appContext.dataStore.data.map { preferences -> preferences[COMPLETED_DATES_KEY] ?: emptySet() }
    val userName: Flow<String> = appContext.dataStore.data.map { preferences -> preferences[USER_NAME_KEY] ?: "" }
    val streakSavers: Flow<Int> = appContext.dataStore.data.map { preferences -> preferences[STREAK_SAVERS_KEY] ?: 0 }
    val exercises: Flow<List<Exercise>> = appContext.dataStore.data.map { preferences ->
        val jsonString = preferences[EXERCISES_LIST_KEY]
        if (jsonString.isNullOrBlank()) {
            // Si no hay rutina guardada, carga la de por defecto
            ExerciseData.getDefaultExercises()
        } else {
            try {
                Json.decodeFromString<List<Exercise>>(jsonString)
            } catch (e: Exception) {
                // En caso de error de deserialización, devolvemos la lista por defecto para evitar que la app se rompa
                ExerciseData.getDefaultExercises()
            }
        }
    }
    val unlockedAchievements: Flow<Set<Int>> = appContext.dataStore.data.map { preferences ->
        preferences[UNLOCKED_ACHIEVEMENTS_KEY]?.map { it.toInt() }?.toSet() ?: emptySet()
    }
    val firstStreakSaverUsed: Flow<Boolean> = appContext.dataStore.data.map { preferences ->
        preferences[STREAK_SAVER_USED_KEY] ?: false
    }

    suspend fun saveExercises(exercises: List<Exercise>) {
        val jsonString = Json.encodeToString(exercises)
        appContext.dataStore.edit { preferences ->
            preferences[EXERCISES_LIST_KEY] = jsonString
        }
    }

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
                val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
                val currentDates = preferences[COMPLETED_DATES_KEY] ?: emptySet()
                preferences[COMPLETED_DATES_KEY] = currentDates + yesterday
            }
        }
    }

    suspend fun updateStreakAndDates(): Boolean {
        var newRewardEarned = false
        appContext.dataStore.edit { preferences ->
            val todayString = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val currentDates = preferences[COMPLETED_DATES_KEY] ?: emptySet()
            if (todayString in currentDates) return@edit

            preferences[COMPLETED_DATES_KEY] = currentDates + todayString

            val yesterdayString = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
            val currentStreak = preferences[STREAK_COUNT_KEY] ?: 0
            val newStreak = if (yesterdayString in currentDates) currentStreak + 1 else 1
            preferences[STREAK_COUNT_KEY] = newStreak

            if (newStreak > 0 && newStreak % 6 == 0) {
                val currentSavers = preferences[STREAK_SAVERS_KEY] ?: 0
                preferences[STREAK_SAVERS_KEY] = currentSavers + 1
                newRewardEarned = true
            }
        }
        return newRewardEarned
    }

    suspend fun unlockAchievement(id: Int) {
        appContext.dataStore.edit { preferences ->
            val currentAchievements = preferences[UNLOCKED_ACHIEVEMENTS_KEY] ?: emptySet()
            val newAchievements = currentAchievements + id.toString()
            preferences[UNLOCKED_ACHIEVEMENTS_KEY] = newAchievements
        }
    }
}