package com.example.rehabilitacionhombro.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.rehabilitacionhombro.viewmodel.StreakViewModel

@Composable
fun StartScreen(
    streakViewModel: StreakViewModel,
    onStartClick: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    val streakCount by streakViewModel.streakCount.collectAsState()
    val userName by streakViewModel.userName.collectAsState()
    val streakSavers by streakViewModel.streakSavers.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botón del calendario en la esquina superior izquierda
        IconButton(
            onClick = onNavigateToCalendar,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Ver Calendario de Progreso",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // **CAMBIO AQUÍ:** Icono y contador de Escudos como un elemento estático y simétrico
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(horizontal = 12.dp, vertical = 12.dp), // Padding para simetría
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
    }
}
