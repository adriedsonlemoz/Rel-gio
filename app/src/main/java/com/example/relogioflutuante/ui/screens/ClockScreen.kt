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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.relogioflutuante.state.ClockState
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.TimeAdjustDialog
import com.example.relogioflutuante.ui.components.TimeCard
import com.example.relogioflutuante.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun ClockScreen() {
    val context = LocalContext.current
    var showAdjust by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }

    val displayedTime by produceState(
        initialValue = ClockState.formattedTime(context),
        key1 = refreshKey
    ) {
        while (true) {
            value = ClockState.formattedTime(context)
            delay(200L)
        }
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
            title = "HORÁRIO",
            time = displayedTime,
            subtitle = if (ClockState.offsetMillis(context) == 0L) {
                "Sincronizado com o horário do aparelho"
            } else {
                "Horário ajustado somente dentro deste aplicativo"
            }
        )

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { showAdjust = true },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
            ) {
                Text("Ajustar horário")
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    ClockState.resetToSystemTime(context)
                    refreshKey++
                }
            ) {
                Text("Usar sistema")
            }
        }

        Spacer(Modifier.height(16.dp))
        InfoCard(
            "O ajuste não altera o relógio do Android. Ele muda apenas a hora exibida por este aplicativo e pelo relógio flutuante."
        )
    }

    if (showAdjust) {
        val shown = ClockState.displayedLocalTime(context)
        TimeAdjustDialog(
            initialHour = shown.hour,
            initialMinute = shown.minute,
            initialSecond = shown.second,
            onDismiss = { showAdjust = false },
            onConfirm = { hour, minute, second ->
                ClockState.setDisplayedTime(context, hour, minute, second)
                refreshKey++
                showAdjust = false
            }
        )
    }
}
