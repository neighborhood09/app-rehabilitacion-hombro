package com.example.rehabilitacionhombro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rehabilitacionhombro.data.StreakDataStore
import com.example.rehabilitacionhombro.ui.screens.CalendarScreen
import com.example.rehabilitacionhombro.ui.screens.EndScreen
import com.example.rehabilitacionhombro.ui.screens.ExerciseScreen
import com.example.rehabilitacionhombro.ui.screens.StartScreen
import com.example.rehabilitacionhombro.ui.screens.WelcomeScreen
import com.example.rehabilitacionhombro.viewmodel.StreakViewModel
import com.example.rehabilitacionhombro.viewmodel.StreakViewModelFactory

@Composable
fun RehabApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val streakViewModel: StreakViewModel = viewModel(
        factory = StreakViewModelFactory(StreakDataStore(context))
    )

    val userName by streakViewModel.userName.collectAsState()
    val exercises by streakViewModel.exercises.collectAsState()

    val startDestination = if (userName.isBlank()) "welcome" else "start"

    if (exercises.isNotEmpty()) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("welcome") {
                WelcomeScreen(
                    streakViewModel = streakViewModel,
                    onNameSaved = {
                        navController.navigate("start") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                )
            }
            composable("start") {
                StartScreen(
                    streakViewModel = streakViewModel,
                    onStartClick = { navController.navigate("exercise/0") },
                    onNavigateToCalendar = { navController.navigate("calendar") }
                )
            }
            composable("exercise/{index}") { backStackEntry ->
                val index = backStackEntry.arguments?.getString("index")?.toInt() ?: 0
                ExerciseScreen(
                    // **LÍNEA CORREGIDA:** Ahora le pasamos el ViewModel
                    streakViewModel = streakViewModel,
                    exercise = exercises[index],
                    exerciseIndex = index,
                    exerciseCount = exercises.size,
                    onNext = {
                        if (index < exercises.size - 1) {
                            navController.navigate("exercise/${index + 1}")
                        } else {
                            navController.navigate("end")
                        }
                    },
                    onPrevious = {
                        if (index > 0) {
                            navController.popBackStack()
                        }
                    }
                )
            }
            composable("end") {
                EndScreen(
                    streakViewModel = streakViewModel,
                    onRestartClick = {
                        navController.navigate("start") {
                            popUpTo("start") { inclusive = true }
                        }
                    }
                )
            }
            composable("calendar") {
                CalendarScreen(
                    streakViewModel = streakViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
