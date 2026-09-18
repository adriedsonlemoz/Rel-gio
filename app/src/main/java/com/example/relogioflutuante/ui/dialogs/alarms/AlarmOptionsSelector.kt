package com.example.relogioflutuante.ui.dialogs.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.alarms.AlarmSound
import com.example.relogioflutuante.ui.theme.AppColors

@Composable
fun AlarmOptionsSelector(
    sound: AlarmSound,
    vibrate: Boolean,
    snoozeMinutes: Int,
    onSoundChange: (AlarmSound) -> Unit,
    onVibrateChange: (Boolean) -> Unit,
    onSnoozeChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Som e soneca", color = AppColors.TextPrimary, fontSize = 13.sp)
        SoundSelector(sound, onSoundChange)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Vibrar", color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
                Text("Usar vibração junto com o alarme", color = AppColors.TextSecondary, fontSize = 11.sp)
            }
            Switch(
                checked = vibrate,
                onCheckedChange = onVibrateChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = AppColors.Accent,
                    checkedThumbColor = AppColors.TextPrimary,
                    uncheckedTrackColor = AppColors.SurfaceStrong
                )
            )
        }
        Text("Soneca", color = AppColors.TextSecondary, fontSize = 11.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(0, 5, 10, 15).forEach { minutes ->
                SnoozeChip(
                    modifier = Modifier.weight(1f),
                    minutes = minutes,
                    selected = snoozeMinutes == minutes,
                    onClick = { onSnoozeChange(minutes) }
                )
            }
        }
    }
}

@Composable
private fun SoundSelector(sound: AlarmSound, onSoundChange: (AlarmSound) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { open = true },
            color = AppColors.SurfaceStrong,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Som", color = AppColors.TextSecondary, fontSize = 10.sp)
                    Text(sound.label, color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
                }
                Text("Alterar", color = AppColors.AccentSoft, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            AlarmSound.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        open = false
                        onSoundChange(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun SnoozeChip(
    modifier: Modifier,
    minutes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (selected) AppColors.Accent.copy(alpha = 0.2f) else AppColors.SurfaceStrong,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = if (minutes == 0) "Off" else "$minutes min",
            modifier = Modifier.padding(vertical = 9.dp),
            color = if (selected) AppColors.AccentSoft else AppColors.TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
