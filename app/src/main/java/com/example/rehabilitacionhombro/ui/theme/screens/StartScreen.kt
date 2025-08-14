// Fichero: app/src/main/java/com/example/rehabilitacionhombro/ui/theme/screens/StartScreen.kt
package com.example.rehabilitacionhombro.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents // **NUEVO:** Importamos el icono de la copa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rehabilitacionhombro.R
import com.example.rehabilitacionhombro.ui.components.CoachAvatar
import com.example.rehabilitacionhombro.viewmodel.StreakViewModel

@Composable
fun StartScreen(
    streakViewModel: StreakViewModel,
    onStartClick: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    // **NUEVO:** Callback para navegar a la pantalla de logros
    onNavigateToAchievements: () -> Unit
) {
    val streakCount by streakViewModel.streakCount.collectAsState()
    val userName by streakViewModel.userName.collectAsState()
    val streakSavers by streakViewModel.streakSavers.collectAsState()
    val canUseShield by streakViewModel.canUseShield.collectAsState()

    var showShieldDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Contenedor para el calendario y los logros
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón del calendario
            IconButton(onClick = onNavigateToCalendar) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Ver Calendario de Progreso",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // **NUEVO:** Botón de logros
            IconButton(onClick = onNavigateToAchievements) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = "Ver Logros",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Icono y contador de Escudos (estático)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_escudo),
                contentDescription = "Protectores de Racha",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = streakSavers.toString(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Avatar del entrenador en la esquina inferior derecha
        CoachAvatar(
            streakCount = streakCount,
            userName = userName,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 60.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Título
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val titleText = if (userName.isNotBlank()) "¡A por ello, $userName!" else "Mi Rehabilitación"
                Text(titleText, style = MaterialTheme.typography.headlineMedium)
                Text("¡Bienvenido/a de nuevo!", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Círculo de la racha
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF34D399), Color(0xFF10B981))
                        )
                    )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = streakCount.toString(),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (streakCount == 1) "día de racha" else "días de racha",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                }
            }

            // Botón para usar el escudo, visible solo si se cumplen las condiciones
            if (canUseShield) {
                Button(
                    onClick = { showShieldDialog = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Usar Escudo Protector")
                }
            }

            // Botón de acción principal
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Comenzar Rutina de Hoy")
            }
        }

        // Diálogo de confirmación
        if (showShieldDialog) {
            AlertDialog(
                onDismissRequest = { showShieldDialog = false },
                title = { Text("Usar Escudo Protector") },
                text = { Text("¿Estás seguro de que quieres usar un escudo para recuperar tu racha de ayer? Te quedan $streakSavers escudos.") },
                confirmButton = {
                    Button(
                        onClick = {
                            streakViewModel.useStreakSaver()
                            showShieldDialog = false
                        }
                    ) {
                        Text("Sí, usarlo")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showShieldDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}