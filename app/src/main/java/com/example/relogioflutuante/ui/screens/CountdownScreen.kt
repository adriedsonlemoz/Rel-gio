package com.example.relogioflutuante.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.relogioflutuante.state.CountdownState
import com.example.relogioflutuante.state.formatDuration
import com.example.relogioflutuante.ui.components.DurationField
import com.example.relogioflutuante.ui.components.TimeCard
import com.example.relogioflutuante.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun CountdownScreen() {
    val context = LocalContext.current
    var hours by remember { mutableStateOf("00") }
    var minutes by remember { mutableStateOf("05") }
    var seconds by remember { mutableStateOf("00") }
    var showFinishedDialog by remember { mutableStateOf(false) }

    val snapshot by produceState(initialValue = CountdownState.snapshot(context)) {
        while (true) {
            value = CountdownState.snapshot(context)
            delay(100L)
        }
    }

    LaunchedEffect(snapshot.isFinished) {
        if (snapshot.isFinished) showFinishedDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(top = 18.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimeCard(
            title = "CONTAGEM REGRESSIVA",
            time = formatDuration(snapshot.remainingMillis),
            subtitle = when {
                snapshot.isFinished -> "Tempo esgotado"
                snapshot.isRunning -> "Em andamento"
                snapshot.remainingMillis == 0L && snapshot.configuredMillis > 0L -> "Pronta para iniciar"
                snapshot.configuredMillis > 0L && snapshot.remainingMillis < snapshot.configuredMillis -> "Pausada"
                else -> "Defina o tempo abaixo"
            }
        )

        Spacer(Modifier.height(18.dp))
        CountdownSetupCard(
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            enabled = !snapshot.isRunning,
            onHoursChange = { hours = it },
            onMinutesChange = { minutes = it },
            onSecondsChange = { seconds = it },
            onApply = {
                CountdownState.setDuration(
                    context,
                    hours.toIntOrNull() ?: 0,
                    minutes.toIntOrNull() ?: 0,
                    seconds.toIntOrNull() ?: 0
                )
            }
        )

        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when {
                snapshot.isRunning -> Button(
                    modifier = Modifier.weight(1f),
                    onClick = { CountdownState.pause(context) }
                ) { Text("Pausar") }

                snapshot.remainingMillis > 0L && snapshot.remainingMillis < snapshot.configuredMillis -> Button(
                    modifier = Modifier.weight(1f),
                    onClick = { CountdownState.resume(context) }
                ) { Text("Continuar") }

                else -> Button(
                    modifier = Modifier.weight(1f),
                    enabled = snapshot.configuredMillis > 0L,
                    onClick = { CountdownState.start(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) { Text("Iniciar") }
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                enabled = snapshot.configuredMillis > 0L,
                onClick = { CountdownState.reset(context) }
            ) { Text("Zerar") }
        }
    }

    if (showFinishedDialog) {
        AlertDialog(
            onDismissRequest = {
                showFinishedDialog = false
                CountdownState.acknowledgeFinished(context)
            },
            title = { Text("Tempo esgotado") },
            text = { Text("A contagem regressiva chegou a 00:00:00.") },
            confirmButton = {
                TextButton(onClick = {
                    showFinishedDialog = false
                    CountdownState.acknowledgeFinished(context)
                }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun CountdownSetupCard(
    hours: String,
    minutes: String,
    seconds: String,
    enabled: Boolean,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onSecondsChange: (String) -> Unit,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Definir tempo", color = AppColors.TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DurationField("Horas", hours, 99, Modifier.weight(1f), onHoursChange)
                DurationField("Min", minutes, 59, Modifier.weight(1f), onMinutesChange)
                DurationField("Seg", seconds, 59, Modifier.weight(1f), onSecondsChange)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                onClick = onApply
            ) {
                Text("Aplicar tempo")
            }
        }
    }
}
