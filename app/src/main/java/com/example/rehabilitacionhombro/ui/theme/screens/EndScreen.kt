// Fichero: app/src/main/java/com/example/rehabilitacionhombro/ui/theme/screens/EndScreen.kt
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

@Composable
fun EndScreen(
    streakViewModel: StreakViewModel,
    onRestartClick: () -> Unit
) {
    val streakCount by streakViewModel.streakCount.collectAsState()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animacion_tick_verde))
    val context = androidx.compose.ui.platform.LocalContext.current

    val newRewardEarned by streakViewModel.newRewardEarned.collectAsState()
    val newAchievement by streakViewModel.newAchievementUnlocked.collectAsState()

    LaunchedEffect(Unit) {
        // **CORREGIDO:** Aseguramos que la rutina se complete y los datos se actualicen ANTES de comprobar los logros.
        streakViewModel.onRoutineCompleted()
        streakViewModel.checkAndUnlockAchievements()
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

    if (newAchievement != null) {
        AchievementUnlockedDialog(
            achievement = newAchievement!!,
            onDismiss = {
                streakViewModel.resetAchievementState()
            }
        )
    }
}

@Composable
fun AchievementUnlockedDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageResId = remember(achievement.iconResName) {
        context.resources.getIdentifier(achievement.iconResName, "drawable", context.packageName)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "¡Logro Desbloqueado!", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = Color(0xFFE5B50A)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = achievement.name, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = achievement.description, textAlign = TextAlign.Center)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}