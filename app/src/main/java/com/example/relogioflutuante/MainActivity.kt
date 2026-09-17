package com.example.relogioflutuante

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FloatingClockTheme {
                FloatingClockApp()
            }
        }
    }
}

private enum class MainSection(val label: String) {
    CLOCK("Relógio"),
    COUNTDOWN("Contagem"),
    OVERLAY("Sobreposição")
}

@Composable
private fun FloatingClockApp() {
    val context = LocalContext.current
    var section by remember { mutableStateOf(MainSection.CLOCK) }
    var pendingOverlayEnable by remember { mutableStateOf(false) }
    var permissionRefresh by remember { mutableIntStateOf(0) }

    fun startOverlay() {
        ContextCompat.startForegroundService(
            context,
            Intent(context, OverlayService::class.java)
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        startOverlay()
    }

    val overlayPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        permissionRefresh++
        if (pendingOverlayEnable && Settings.canDrawOverlays(context)) {
            pendingOverlayEnable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                startOverlay()
            }
        }
    }

    fun requestEnableOverlay() {
        if (!Settings.canDrawOverlays(context)) {
            pendingOverlayEnable = true
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            overlayPermissionLauncher.launch(intent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startOverlay()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Relógio Flutuante",
                    color = AppColors.TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hora e contagem sempre à vista",
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = AppColors.Surface) {
                MainSection.entries.forEach { item ->
                    NavigationBarItem(
                        selected = section == item,
                        onClick = { section = item },
                        icon = { Text(if (section == item) "●" else "○", fontSize = 16.sp) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ResponsiveContent {
                when (section) {
                    MainSection.CLOCK -> ClockScreen()
                    MainSection.COUNTDOWN -> CountdownScreen()
                    MainSection.OVERLAY -> OverlayScreen(
                        permissionRefresh = permissionRefresh,
                        onEnableOverlay = ::requestEnableOverlay
                    )
                }
            }
        }
    }
}

@Composable
private fun ResponsiveContent(content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val horizontal = if (maxWidth >= 700.dp) 48.dp else 18.dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontal),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = if (maxWidth >= 700.dp) {
                    Modifier.width(680.dp)
                } else {
                    Modifier.fillMaxWidth()
                }
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ClockScreen() {
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
            onConfirm = { h, m, s ->
                ClockState.setDisplayedTime(context, h, m, s)
                refreshKey++
                showAdjust = false
            }
        )
    }
}

@Composable
private fun CountdownScreen() {
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Definir tempo", color = AppColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DurationField("Horas", hours, 99, Modifier.weight(1f)) { hours = it }
                    DurationField("Min", minutes, 59, Modifier.weight(1f)) { minutes = it }
                    DurationField("Seg", seconds, 59, Modifier.weight(1f)) { seconds = it }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !snapshot.isRunning,
                    onClick = {
                        CountdownState.setDuration(
                            context,
                            hours.toIntOrNull() ?: 0,
                            minutes.toIntOrNull() ?: 0,
                            seconds.toIntOrNull() ?: 0
                        )
                    }
                ) {
                    Text("Aplicar tempo")
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when {
                snapshot.isRunning -> {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { CountdownState.pause(context) }
                    ) { Text("Pausar") }
                }
                snapshot.remainingMillis > 0L && snapshot.remainingMillis < snapshot.configuredMillis -> {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { CountdownState.resume(context) }
                    ) { Text("Continuar") }
                }
                else -> {
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = snapshot.configuredMillis > 0L,
                        onClick = { CountdownState.start(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                    ) { Text("Iniciar") }
                }
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
private fun OverlayScreen(
    permissionRefresh: Int,
    onEnableOverlay: () -> Unit
) {
    val context = LocalContext.current
    val canOverlay = remember(permissionRefresh) { Settings.canDrawOverlays(context) }
    var mode by remember { mutableStateOf(OverlayState.mode(context)) }

    val overlayEnabled by produceState(initialValue = OverlayState.isEnabled(context)) {
        while (true) {
            value = OverlayState.isEnabled(context)
            delay(500L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 18.dp, bottom = 24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Relógio sobre outros apps", color = AppColors.TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(
                    "A janela é compacta, pode ser arrastada e não bloqueia os toques fora dela.",
                    color = AppColors.TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(18.dp))
                Text("Mostrar no overlay", color = AppColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = mode == OverlayMode.CLOCK,
                        onClick = {
                            mode = OverlayMode.CLOCK
                            OverlayState.setMode(context, mode)
                        },
                        label = { Text("Relógio") }
                    )
                    FilterChip(
                        selected = mode == OverlayMode.COUNTDOWN,
                        onClick = {
                            mode = OverlayMode.COUNTDOWN
                            OverlayState.setMode(context, mode)
                        },
                        label = { Text("Contagem") }
                    )
                }

                Spacer(Modifier.height(18.dp))
                StatusLine(
                    label = "Permissão sobre outros apps",
                    value = if (canOverlay) "Permitida" else "Necessária",
                    good = canOverlay
                )
                Spacer(Modifier.height(8.dp))
                StatusLine(
                    label = "Overlay",
                    value = if (overlayEnabled) "Ativo" else "Desativado",
                    good = overlayEnabled
                )

                Spacer(Modifier.height(18.dp))
                if (!overlayEnabled) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onEnableOverlay,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
                    ) {
                        Text(if (canOverlay) "Ativar sobreposição" else "Conceder permissão e ativar")
                    }
                } else {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            context.startService(
                                Intent(context, OverlayService::class.java)
                                    .setAction(OverlayService.ACTION_STOP)
                            )
                        }
                    ) {
                        Text("Desativar sobreposição")
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        InfoCard(
            "Enquanto o overlay estiver ativo, um serviço em primeiro plano mantém a janela funcionando mesmo depois que você sair do aplicativo."
        )
    }
}

@Composable
private fun TimeCard(title: String, time: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceStrong),
        shape = RoundedCornerShape(26.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            val timeSize = when {
                maxWidth < 330.dp -> 38.sp
                maxWidth < 450.dp -> 48.sp
                else -> 58.sp
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    color = AppColors.AccentSoft,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = time,
                    color = AppColors.TextPrimary,
                    fontSize = timeSize,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun InfoCard(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(15.dp),
            color = AppColors.TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
}

@Composable
private fun StatusLine(label: String, value: String, good: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), color = AppColors.TextSecondary, fontSize = 14.sp)
        Text(
            value,
            color = if (good) AppColors.Success else AppColors.Warning,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DurationField(
    label: String,
    value: String,
    maxValue: Int,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { raw ->
            val digits = raw.filter(Char::isDigit).take(2)
            if (digits.isEmpty()) onValueChange("")
            else onValueChange((digits.toIntOrNull() ?: 0).coerceAtMost(maxValue).toString())
        },
        label = { Text(label) },
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
private fun TimeAdjustDialog(
    initialHour: Int,
    initialMinute: Int,
    initialSecond: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Int) -> Unit
) {
    var hour by remember { mutableStateOf(initialHour.toString().padStart(2, '0')) }
    var minute by remember { mutableStateOf(initialMinute.toString().padStart(2, '0')) }
    var second by remember { mutableStateOf(initialSecond.toString().padStart(2, '0')) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajustar horário exibido") },
        text = {
            Column {
                Text(
                    "Escolha a hora que o aplicativo deve mostrar agora.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DurationField("Hora", hour, 23, Modifier.weight(1f)) { hour = it }
                    DurationField("Min", minute, 59, Modifier.weight(1f)) { minute = it }
                    DurationField("Seg", second, 59, Modifier.weight(1f)) { second = it }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        (hour.toIntOrNull() ?: 0).coerceIn(0, 23),
                        (minute.toIntOrNull() ?: 0).coerceIn(0, 59),
                        (second.toIntOrNull() ?: 0).coerceIn(0, 59)
                    )
                }
            ) { Text("Aplicar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private object AppColors {
    val Background = Color(0xFF0B1220)
    val Surface = Color(0xFF111827)
    val SurfaceStrong = Color(0xFF172033)
    val Accent = Color(0xFF2563EB)
    val AccentSoft = Color(0xFF7DD3FC)
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFF94A3B8)
    val Success = Color(0xFF4ADE80)
    val Warning = Color(0xFFFBBF24)
}

@Composable
private fun FloatingClockTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            primary = AppColors.Accent,
            background = AppColors.Background,
            surface = AppColors.Surface,
            onPrimary = Color.White,
            onBackground = AppColors.TextPrimary,
            onSurface = AppColors.TextPrimary
        ),
        content = content
    )
}
