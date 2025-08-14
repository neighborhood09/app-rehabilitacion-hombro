package com.example.rehabilitacionhombro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarView(
    currentMonth: YearMonth,
    completedDates: Set<String>,
    modifier: Modifier = Modifier
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value // Lunes=1, Domingo=7
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    Column(modifier = modifier) {
        // Encabezado con los días de la semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val daysOfWeek = listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Cuadrícula de los días
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val totalCells = (startOffset(firstDayOfMonth) + daysInMonth)
            val rows = (totalCells + 6) / 7
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (col in 0 until 7) {
                        val dayIndex = row * 7 + col
                        val dayOfMonth = dayIndex - startOffset(firstDayOfMonth) + 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dayOfMonth in 1..daysInMonth) {
                                val date = currentMonth.atDay(dayOfMonth)
                                val dateString = date.format(dateFormatter)

                                val isToday = date.isEqual(today)
                                val isCompleted = completedDates.contains(dateString)
                                val isPast = date.isBefore(today)

                                val backgroundColor = when {
                                    isToday -> MaterialTheme.colorScheme.primary
                                    isCompleted -> Color(0xFF4CAF50) // Verde
                                    isPast -> Color(0xFFF44336) // Rojo
                                    else -> Color.Transparent
                                }

                                val contentColor = when {
                                    isToday || isCompleted || isPast -> Color.White
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(backgroundColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayOfMonth.toString(),
                                        color = contentColor,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper para ajustar el inicio de la semana a Lunes
private fun startOffset(dayOfWeek: Int): Int {
    return (dayOfWeek - 1 + 7) % 7
}
