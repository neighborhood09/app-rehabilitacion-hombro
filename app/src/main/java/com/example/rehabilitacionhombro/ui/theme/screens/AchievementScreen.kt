// Fichero: app/src/main/java/com/example/rehabilitacionhombro/ui/theme/screens/AchievementsScreen.kt
package com.example.rehabilitacionhombro.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rehabilitacionhombro.data.Achievement
import com.example.rehabilitacionhombro.data.AchievementData
import com.example.rehabilitacionhombro.R
import com.example.rehabilitacionhombro.viewmodel.StreakViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    streakViewModel: StreakViewModel,
    onBack: () -> Unit
) {
    val unlockedAchievements by streakViewModel.unlockedAchievements.collectAsState()
    val allAchievements = AchievementData.allAchievements

    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Logros", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(allAchievements) { achievement ->
                val isUnlocked = unlockedAchievements.contains(achievement.id)
                AchievementCard(
                    achievement = achievement,
                    isUnlocked = isUnlocked,
                    onAchievementClick = {
                        if (isUnlocked) {
                            selectedAchievement = achievement
                        }
                    }
                )
            }
        }
    }

    if (selectedAchievement != null) {
        AchievementDetailsDialog(
            achievement = selectedAchievement!!,
            onDismiss = { selectedAchievement = null }
        )
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    isUnlocked: Boolean,
    onAchievementClick: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(achievement.iconResName) {
        getImageResourceId(context, achievement.iconResName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isUnlocked, onClick = onAchievementClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageResId != 0) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    colorFilter = if (!isUnlocked) ColorFilter.tint(Color.Gray) else null
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = if (isUnlocked) Color(0xFFE5B50A) else Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                // **ACTUALIZADO:** El nombre del logro solo se muestra si está desbloqueado
                Text(
                    text = if (isUnlocked) achievement.name else "Logro Bloqueado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
                )
                if (isUnlocked) {
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Completa tu rutina para descubrir cómo desbloquearlo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementDetailsDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val imageResId = remember(achievement.iconResName) {
        getImageResourceId(context, achievement.iconResName)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = achievement.name, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
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

// Función auxiliar para obtener ID de recurso de imagen
private fun getImageResourceId(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}