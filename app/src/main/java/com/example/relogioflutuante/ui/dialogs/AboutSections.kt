package com.example.relogioflutuante.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.state.AppDiagnostics
import com.example.relogioflutuante.ui.components.DONATION_PIX_KEY
import com.example.relogioflutuante.ui.theme.AppColors

internal data class ChangeLogItem(
    val version: String,
    val title: String,
    val details: String
)

internal val recentChanges = listOf(
    ChangeLogItem("1.6.0", "Sobre e diagnóstico", "Histórico visual, diagnóstico copiável e correção do preset Corvith."),
    ChangeLogItem("1.5.0", "Alarmes mais completos", "Soneca, som e vibração configuráveis individualmente por alarme."),
    ChangeLogItem("1.4.0", "Presets e acesso rápido", "Presets do overlay, bloco nas Configurações Rápidas e alarmes Zyrvorthian."),
    ChangeLogItem("1.3.x", "Polimento visual", "Refino de navegação, alarmes, fusos, menu e desempenho geral.")
)

@Composable
internal fun DiagnosticCard(diagnostics: AppDiagnostics, onCopy: () -> Unit) {
    AboutCard(title = "Diagnóstico rápido") {
        DiagnosticRow("Overlay", if (diagnostics.overlayEnabled) "Ativo" else "Desativado", diagnostics.overlayEnabled)
        DiagnosticRow("Método", diagnostics.overlayMethod, diagnostics.overlayEnabled)
        DiagnosticRow("Sobrepor apps", yesNo(diagnostics.drawOverlays), diagnostics.drawOverlays)
        DiagnosticRow("Acessibilidade", yesNo(diagnostics.accessibility), diagnostics.accessibility)
        DiagnosticRow("Alarmes exatos", yesNo(diagnostics.exactAlarms), diagnostics.exactAlarms)
        DiagnosticRow("Notificações", yesNo(diagnostics.notifications), diagnostics.notifications)
        Surface(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onCopy),
            shape = RoundedCornerShape(12.dp),
            color = AppColors.Accent.copy(alpha = 0.14f)
        ) {
            Text(
                "Copiar diagnóstico",
                modifier = Modifier.padding(12.dp),
                color = AppColors.AccentSoft,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
internal fun ChangesCard() {
    AboutCard(title = "Últimas alterações") {
        recentChanges.forEach { item ->
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.version, color = AppColors.AccentSoft, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("  •  ${item.title}", color = AppColors.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Text(item.details, color = AppColors.TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}

@Composable
internal fun PixCard(onCopyPix: () -> Unit) {
    AboutCard(title = "Apoiar o projeto") {
        Text(
            "Se quiser contribuir com o desenvolvimento, toque na chave PIX para copiar.",
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
        Surface(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onCopyPix),
            shape = RoundedCornerShape(12.dp),
            color = AppColors.SurfaceStrong
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(DONATION_PIX_KEY, modifier = Modifier.weight(1f), color = AppColors.TextPrimary, fontSize = 13.sp)
                Text("Copiar", color = AppColors.AccentSoft, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
internal fun AboutCard(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = AppColors.Surface.copy(alpha = 0.92f)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, color = AppColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            content()
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String, good: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), color = AppColors.TextSecondary, fontSize = 12.sp)
        Text(value, color = if (good) AppColors.Success else AppColors.Warning, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

private fun yesNo(value: Boolean) = if (value) "Permitido" else "Não permitido"
