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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.relogioflutuante.state.CountdownState
import com.example.relogioflutuante.state.formatDuration
import com.example.relogioflutuante.ui.components.CountdownSetupCard
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
    var refreshKey by remember { mutableIntStateOf(0) }

    val snapshot by produceState(
        initialValue = CountdownState.snapshot(context),
        key1 = refreshKey
    ) {
        while (true) {
            val current = CountdownState.snapshot(context)
            value = current
            val delayMs = if (current.isRunning) {
                (current.remainingMillis % 1_000L).coerceIn(100L, 1_000L)
            } else {
                1_000L
            }
            delay(delayMs)
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
                refreshKey++
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
                    onClick = {
                        CountdownState.pause(context)
                        refreshKey++
                    }
                ) { Text("Pausar") }

                snapshot.remainingMillis > 0L && snapshot.remainingMillis < snapshot.configuredMillis -> Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        CountdownState.resume(context)
                        refreshKey++
                    }
                ) { Text("Continuar") }

                else -> Button(
                    modifier = Modifier.weight(1f),
                    enabled = snapshot.configuredMillis > 0L,
                    onClick = {
                        CountdownState.start(context)
                        refreshKey++
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                ) { Text("Iniciar") }
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                enabled = snapshot.configuredMillis > 0L,
                onClick = {
                    CountdownState.reset(context)
                    refreshKey++
                }
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
