package com.example.relogioflutuante.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.relogioflutuante.alarms.Alarm
import com.example.relogioflutuante.alarms.AlarmFormatting
import com.example.relogioflutuante.alarms.AlarmRepository
import com.example.relogioflutuante.alarms.AlarmScheduler
import com.example.relogioflutuante.ui.components.AppScreenColumn
import com.example.relogioflutuante.ui.components.InfoCard
import com.example.relogioflutuante.ui.components.alarms.AlarmListCard
import com.example.relogioflutuante.ui.components.alarms.AlarmPermissionCard
import com.example.relogioflutuante.ui.dialogs.alarms.AlarmEditorDialog
import com.example.relogioflutuante.ui.theme.AppColors
import java.time.DayOfWeek
import kotlinx.coroutines.delay

@Composable
fun AlarmScreen(
    permissionRefresh: Int,
    onMessage: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember(context) { AlarmRepository(context) }
    val scheduler = remember(context) { AlarmScheduler(context) }
    var alarms by remember { mutableStateOf(repository.all()) }
    var editing by remember { mutableStateOf<Alarm?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var exactAllowed by remember { mutableStateOf(scheduler.canScheduleExact()) }
    var notificationsAllowed by remember { mutableStateOf(notificationsAllowed(context)) }
    val nowMillis by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(30_000L)
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> notificationsAllowed = granted || notificationsAllowed(context) }

    LaunchedEffect(permissionRefresh) {
        exactAllowed = scheduler.canScheduleExact()
        notificationsAllowed = notificationsAllowed(context)
        scheduler.rescheduleAll()
        alarms = repository.all()
    }

    AppScreenColumn {
        AlarmPermissionCard(
            exactAllowed = exactAllowed,
            notificationsAllowed = notificationsAllowed,
            onRequestExact = {
                runCatching { context.startActivity(scheduler.exactAlarmSettingsIntent()) }
                    .onFailure { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
            },
            onRequestNotifications = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    context.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    )
                }
            }
        )

        AlarmListCard(
            alarms = alarms,
            nowMillis = nowMillis,
            onToggle = { alarm, enabled ->
                repository.setEnabled(alarm.id, enabled)?.let { updated ->
                    if (enabled) scheduler.schedule(updated) else scheduler.cancel(updated.id)
                    onMessage(if (enabled) "Alarme ativado · ${AlarmFormatting.nextTriggerSummary(updated, System.currentTimeMillis())}" else "Alarme desativado")
                }
                alarms = repository.all()
            },
            onEdit = { alarm -> editing = alarm; showEditor = true },
            onDelete = { alarm ->
                scheduler.cancel(alarm.id)
                repository.delete(alarm.id)
                alarms = repository.all()
                onMessage("Alarme excluído")
            }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { editing = null; showEditor = true },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
        ) { Text("+ Adicionar alarme") }

        InfoCard("Toque em um alarme para editar. Repetições são reagendadas automaticamente.")
    }

    if (showEditor) {
        AlarmEditorDialog(
            alarm = editing,
            onDismiss = { showEditor = false },
            onConfirm = { hour, minute, label, days ->
                val saved = saveAlarm(repository, scheduler, editing, hour, minute, label, days)
                alarms = repository.all()
                showEditor = false
                editing = null
                onMessage("Alarme salvo · ${AlarmFormatting.nextTriggerSummary(saved, System.currentTimeMillis())}")
            }
        )
    }
}

private fun saveAlarm(
    repository: AlarmRepository,
    scheduler: AlarmScheduler,
    existing: Alarm?,
    hour: Int,
    minute: Int,
    label: String,
    days: Set<DayOfWeek>
): Alarm {
    val alarm = Alarm(
        id = existing?.id ?: repository.nextId(),
        hour = hour,
        minute = minute,
        label = label,
        repeatDays = days,
        enabled = true
    )
    repository.save(alarm)
    scheduler.schedule(alarm)
    return alarm
}

private fun notificationsAllowed(context: android.content.Context): Boolean {
    val runtimeGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    return runtimeGranted && NotificationManagerCompat.from(context).areNotificationsEnabled()
}
