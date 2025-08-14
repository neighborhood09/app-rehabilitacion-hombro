// Fichero: app/src/main/java/com/example/rehabilitacionhombro/ui/theme/screens/StartScreen.kt
package com.example.rehabilitacionhombro.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
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
        // Contenedor de iconos superiores
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateToCalendar) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Ver Calendario de Progreso",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onNavigateToAchievements) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Ver Logros",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
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
        }

        // Contenido principal de la pantalla, centrado verticalmente
        Column(
            modifier = Modifier.fillMaxSize().offset(y = (-60).dp), // Ajuste para que baje un poco
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val titleText = if (userName.isNotBlank()) "¡A por ello, $userName!" else "Mi Rehabilitación"
                Text(titleText, style = MaterialTheme.typography.headlineMedium)
                Text("¡Bienvenido/a de nuevo!", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(32.dp))

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
        }

        // Contenedor para botones y coach en la parte inferior
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        ) {
            // **CORREGIDO:** El coach en la esquina inferior derecha
            CoachAvatar(
                streakCount = streakCount,
                userName = userName,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 60.dp)
            )

            // Columna para los botones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                if (canUseShield) {
                    Button(
                        onClick = { showShieldDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text("Usar Escudo Protector")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Comenzar Rutina de Hoy")
                }
            }
        }
    }

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