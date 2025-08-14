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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rehabilitacionhombro.ui.theme.RehabilitacionHombroTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // **NUEVO:** Preparamos el lanzador de permisos de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // El permiso ha sido concedido
            scheduleDailyNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // **NUEVO:** Comprobamos y solicitamos el permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                scheduleDailyNotification()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Para versiones anteriores a Android 13, no se necesita pedir permiso
            scheduleDailyNotification()
        }

        setContent {
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

    // **NUEVO:** Función para programar la notificación diaria
    private fun scheduleDailyNotification() {
        // La notificación se programará para las 7 PM (hora local del dispositivo)
        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(getNotificationInitialDelay(), TimeUnit.MILLISECONDS)
            .addTag("daily_rehab_notification")
            .build()

        // Usamos ExistingPeriodicWorkPolicy.KEEP para no sobreescribir la tarea si ya existe
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyRehabNotification",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }

    private fun getNotificationInitialDelay(): Long {
        val now = java.util.Calendar.getInstance()
        val dueTime = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 19) // 7 PM
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }

        if (now.after(dueTime)) {
            // Si ya pasaron las 7 PM, programamos para mañana
            dueTime.add(java.util.Calendar.HOUR_OF_DAY, 24)
        }

        return dueTime.timeInMillis - now.timeInMillis
    }
}