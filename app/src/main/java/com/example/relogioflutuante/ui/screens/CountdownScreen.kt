package com.example.relogioflutuante.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.relogioflutuante.state.CountdownState
import com.example.relogioflutuante.state.formatDuration
import com.example.relogioflutuante.ui.components.AppScreenColumn
import com.example.relogioflutuante.ui.components.CountdownActions
import com.example.relogioflutuante.ui.components.CountdownSetupCard
import com.example.relogioflutuante.ui.components.TimeCard
import com.example.relogioflutuante.ui.dialogs.FinishedCountdownDialog
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

    AppScreenColumn(imeAware = true) {
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
