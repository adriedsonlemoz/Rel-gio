package com.example.relogioflutuante.ui.dialogs.timezones

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.relogioflutuante.timezones.WorldClockCatalog
import com.example.relogioflutuante.timezones.WorldClockTime
import com.example.relogioflutuante.ui.theme.AppColors
import java.time.Instant
import java.time.ZoneId

@Composable
fun WorldClockPickerDialog(
    selectedZoneIds: Set<String>,
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { WorldClockCatalog.search(query) }
    val instant = remember { Instant.now() }
    val localZone = remember { ZoneId.systemDefault() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(color = AppColors.SurfaceStrong, shape = RoundedCornerShape(22.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Adicionar fuso horário", color = AppColors.TextPrimary, fontSize = 18.sp)
                Text(
                    "UTC, diferença e horário já aparecem antes de adicionar.",
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 3.dp, bottom = 10.dp)
                )
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Pesquisar cidade ou país") }
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 430.dp)
                        .padding(top = 8.dp)
                ) {
                    items(results, key = { it.zoneId }) { entry ->
                        WorldClockPickerRow(
                            entry = entry,
                            display = WorldClockTime.display(ZoneId.of(entry.zoneId), instant, localZone),
                            selected = entry.zoneId in selectedZoneIds,
                            onSelect = { onAdd(entry.zoneId) }
                        )
                        HorizontalDivider(color = AppColors.TextSecondary.copy(alpha = 0.08f))
                    }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Concluir")
                }
            }
        }
    }
}
