package com.example.relogioflutuante.ui.components.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.alarms.AlarmDefaults
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun AlarmDefaultsCard(
    alarms: List<Alarm>,
    onRestore: (AlarmDefaults.Definition) -> Unit,
    onRestoreAll: () -> Unit
) {
    val missing = AlarmDefaults.definitions.filter { AlarmDefaults.findExisting(alarms, it) == null }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Surface,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Text(
                "ALARMES PADRÃO",
                color = AppColors.AccentSoft,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Text(
                "Eventos Zyrvorthian no horário de Brasília. Restaurar é sempre manual.",
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
            AlarmDefaults.definitions.forEach { definition ->
                val present = AlarmDefaults.findExisting(alarms, definition) != null
                DefaultAlarmRow(definition, present) { onRestore(definition) }
            }
            if (missing.size > 1) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRestoreAll,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Restaurar ${missing.size} alarmes ausentes") }
            }
        }
    }
}

@Composable
private fun DefaultAlarmRow(
    definition: AlarmDefaults.Definition,
    present: Boolean,
    onRestore: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "%02d:%02d".format(definition.hour, definition.minute),
                color = AppColors.TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                definition.label.removePrefix("Zyrvorthian "),
                color = AppColors.TextSecondary,
                fontSize = 12.sp
            )
        }
        if (present) {
            Surface(
                color = AppColors.Success.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "Presente",
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    color = AppColors.Success,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            OutlinedButton(onClick = onRestore, shape = RoundedCornerShape(12.dp)) {
                Text("Restaurar")
            }
        }
    }
}
