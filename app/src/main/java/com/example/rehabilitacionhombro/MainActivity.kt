// Fichero: app/src/main/java/com/example/rehabilitacionhombro/MainActivity.kt
package com.example.rehabilitacionhombro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.example.rehabilitacionhombro.ui.theme.RehabilitacionHombroTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleDailyNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // **CORREGIDO:** Configura el modo a pantalla completa antes de setContent
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                scheduleDailyNotification()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            scheduleDailyNotification()
        }

        setContent {
            // **CORREGIDO:** El rememberSystemUiController debe ir dentro de la composición
            val systemUiController = rememberSystemUiController()
            LaunchedEffect(systemUiController) {
                systemUiController.isSystemBarsVisible = false
                systemUiController.isNavigationBarVisible = false
                systemUiController.isStatusBarVisible = false
            }

            RehabilitacionHombroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RehabApp()
                }
            }
        }
    }

    private fun scheduleDailyNotification() {
        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(getNotificationInitialDelay(), TimeUnit.MILLISECONDS)
            .addTag("daily_rehab_notification")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyRehabNotification",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }

    private fun getNotificationInitialDelay(): Long {
        val now = java.util.Calendar.getInstance()
        val dueTime = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 19)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }

        if (now.after(dueTime)) {
            dueTime.add(java.util.Calendar.HOUR_OF_DAY, 24)
        }

        return dueTime.timeInMillis - now.timeInMillis
    }
}