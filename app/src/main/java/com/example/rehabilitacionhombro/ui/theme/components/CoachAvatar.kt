package com.example.rehabilitacionhombro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.rehabilitacionhombro.R

@Composable
fun CoachAvatar(
    streakCount: Int,
    userName: String,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coach_motivacional))
    var currentMessage by remember { mutableStateOf("") }

    LaunchedEffect(streakCount, userName) {
        currentMessage = getMotivationalMessage(streakCount, userName)
    }

    Box(modifier = modifier) {
        if (currentMessage.isNotEmpty()) {
            Text(
                text = currentMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(180.dp) // Tamaño aumentado
                .align(Alignment.BottomCenter)
        )
    }
}

private fun getMotivationalMessage(streakCount: Int, userName: String): String {
    val nameToShow = if (userName.isBlank()) "tú" else userName
    val monthlyMessages = listOf(
        "¡Un mes entero, $nameToShow! Esto es dedicación.", "¡Dos meses! Tu disciplina es increíble.",
        "¡Tres meses, $nameToShow! Has convertido esto en un estilo de vida.", "¡Cuatro meses de constancia! Sigue brillando.",
        "¡Cinco meses! Eres una inspiración, $nameToShow.", "¡Medio año! Estoy muy orgulloso de tu progreso.",
        "¡Siete meses! Nada puede detenerte.", "¡Ocho meses, $nameToShow! La fuerza de la costumbre es poderosa.",
        "¡Nueve meses! Un ejemplo de superación.", "¡Diez meses! Sigue sumando días y logros.",
        "¡Once meses, $nameToShow! A un paso del año completo.", "¡UN AÑO ENTERO, $nameToShow! ¡Felicidades, has alcanzado un hito legendario!"
    )
    if (streakCount == 365) return monthlyMessages[11]
    if (streakCount > 0 && streakCount % 30 == 0) {
        val monthIndex = (streakCount / 30) - 1
        if (monthIndex < monthlyMessages.size) return monthlyMessages[monthIndex]
    }
    if (streakCount == 6) return "¡Vamos, $nameToShow! ¡Mañana cumples una semana!"
    if (streakCount > 0 && streakCount % 7 == 0) return "¡Otra semana completa! ¡Fantástico, $nameToShow!"
    val messages = when {
        streakCount == 0 -> listOf("¡El primer paso es el más importante!", "¡Vamos a empezar con buen pie!", "¡Tú puedes con esto!")
        streakCount in 1..5 -> listOf("¡Un día más! Sigue así.", "La constancia empieza a dar frutos.", "¡Genial, no has fallado!")
        streakCount in 7..29 -> listOf("¡Ya es una costumbre! ¡Qué bien!", "Estás creando un hábito saludable.", "¡$streakCount días seguidos! Impresionante.")
        else -> listOf("¡Vaya racha, $nameToShow! Eres un ejemplo.", "¡Increíble constancia!", "Sigue sumando días, ¡eres imparable!")
    }
    return messages.random()
}

