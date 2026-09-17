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
            } else 1_000L
            delay(delayMs)
        }
    }

    LaunchedEffect(snapshot.isFinished) {
        if (snapshot.isFinished) showFinishedDialog = true
    }

    val isPaused = !snapshot.isRunning &&
        snapshot.remainingMillis > 0L &&
        snapshot.remainingMillis < snapshot.configuredMillis
    val showSetup = !snapshot.isRunning && !isPaused

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(top = 12.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimeCard(
            title = "CONTAGEM REGRESSIVA",
            time = formatDuration(snapshot.remainingMillis),
            subtitle = when {
                snapshot.isFinished -> "Tempo esgotado"
                snapshot.isRunning -> "Em andamento"
                isPaused -> "Pausada"
                snapshot.configuredMillis > 0L -> "Pronta para iniciar"
                else -> "Defina o tempo abaixo"
            }
        )

        if (showSetup) {
            Spacer(Modifier.height(14.dp))
            CountdownSetupCard(
                hours = hours,
                minutes = minutes,
                seconds = seconds,
                enabled = true,
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
        }

        Spacer(Modifier.height(12.dp))
        CountdownActions(
            isRunning = snapshot.isRunning,
            isPaused = isPaused,
            canStart = snapshot.configuredMillis > 0L,
            onPause = {
                CountdownState.pause(context)
                refreshKey++
            },
            onResume = {
                CountdownState.resume(context)
                refreshKey++
            },
            onStart = {
                CountdownState.start(context)
                refreshKey++
            },
            onReset = {
                CountdownState.reset(context)
                refreshKey++
            }
        )
    }

    if (showFinishedDialog) {
        FinishedCountdownDialog {
            showFinishedDialog = false
            CountdownState.acknowledgeFinished(context)
        }
    }
}

@Composable
private fun CountdownActions(
    isRunning: Boolean,
    isPaused: Boolean,
    canStart: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStart: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when {
            isRunning -> Button(modifier = Modifier.weight(1f), onClick = onPause) { Text("Pausar") }
            isPaused -> Button(modifier = Modifier.weight(1f), onClick = onResume) { Text("Continuar") }
            else -> Button(
                modifier = Modifier.weight(1f),
                enabled = canStart,
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
            ) { Text("Iniciar") }
        }
        OutlinedButton(
            modifier = Modifier.weight(1f),
            enabled = canStart,
            onClick = onReset
        ) { Text("Zerar") }
    }
}

@Composable
private fun FinishedCountdownDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tempo esgotado") },
        text = { Text("A contagem regressiva chegou a 00:00:00.") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } }
    )
}
