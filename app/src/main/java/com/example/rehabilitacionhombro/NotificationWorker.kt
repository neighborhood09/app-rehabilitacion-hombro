// Fichero: app/src/main/java/com/example/rehabilitacionhombro/NotificationWorker.kt
package com.example.rehabilitacionhombro

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rehabilitacionhombro.data.StreakDataStore
import kotlinx.coroutines.flow.first

class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) { // **CORREGIDO:** Aquí pasamos el appContext al constructor padre

    override suspend fun doWork(): Result {
        // Obtenemos el nombre del usuario de forma asíncrona para personalizar la notificación
        val streakDataStore = StreakDataStore(applicationContext) // **CORREGIDO:** Usamos applicationContext
        val userName = streakDataStore.userName.first()

        // Solo enviamos la notificación si el usuario ha completado la pantalla de bienvenida
        if (userName.isNotBlank()) {
            showNotification(userName)
        }

        return Result.success()
    }

    private fun showNotification(userName: String) {
        // Verificamos si la app tiene permiso para enviar notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Si no tiene permiso, no mostramos la notificación
                return
            }
        }

        val channelId = "rehab_notification_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Creamos el canal de notificación para versiones de Android 8.0 y superiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Rutina",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios para completar la rutina diaria de rehabilitación."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Construimos la notificación
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Usamos un icono por defecto
            .setContentTitle("¡Hola, $userName!")
            .setContentText("No olvides hacer tu rutina de hoy para mantener tu racha.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}