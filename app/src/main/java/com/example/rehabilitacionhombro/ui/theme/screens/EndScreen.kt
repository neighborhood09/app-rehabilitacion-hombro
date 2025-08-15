// Fichero: app/src/main/java/com/example/rehabilitacionhombro/ui/screens/EndScreen.kt
package com.example.rehabilitacionhombro.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.rehabilitacionhombro.R
import com.example.rehabilitacionhombro.data.Achievement
import com.example.rehabilitacionhombro.viewmodel.StreakViewModel
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.DisposableEffect


@Composable
fun EndScreen(
    streakViewModel: StreakViewModel,
    onRestartClick: () -> Unit
) {
    val streakCount by streakViewModel.streakCount.collectAsState()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animacion_tick_verde))
    val context = androidx.compose.ui.platform.LocalContext.current
    val haptic = LocalHapticFeedback.current

    val newRewardEarned by streakViewModel.newRewardEarned.collectAsState()
    val newAchievement by streakViewModel.newAchievementUnlocked.collectAsState()

    // **ACTUALIZADO:** Lógica de vibración y sonido
    LaunchedEffect(Unit) {
        // Vibración
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        // Sonido
        val mediaPlayer = MediaPlayer.create(context, R.raw.completed_sound) // Reemplaza "completed_sound" con el nombre de tu archivo
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }

        streakViewModel.onRoutineCompleted()
    }

    DisposableEffect(Unit) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.completed_sound) // Reemplaza "completed_sound" con el nombre de tu archivo
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mp -> mp.release() }
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "¡Rutina Completada!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (streakCount > 0) {
            Text(
                text = "¡Felicidades! Has alcanzado una racha de $streakCount ${if (streakCount == 1) "día" else "días"}. ¡Sigue así!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(visible = newRewardEarned) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Escudo",
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "¡Has ganado 1 Escudo Protector!",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                streakViewModel.resetRewardState()
                onRestartClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hecho")
        }
    }
}