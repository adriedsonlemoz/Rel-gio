package com.example.relogioflutuante.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import com.example.relogioflutuante.state.AppDiagnostics
import com.example.relogioflutuante.ui.components.DONATION_PIX_KEY
import com.example.relogioflutuante.ui.theme.AppColors

internal data class ChangeLogItem(
    val version: String,
    val title: String,
    val details: String
)

internal val recentChanges = listOf(
    ChangeLogItem("1.8.0", "Interface mais enxuta", "Alarmes e Sobre recolhíveis, contagem sem teclado e novo polimento visual."),
    ChangeLogItem("1.7.x", "Alarmes organizados", "Ordenação por próximo disparo, restauração manual dos presets e correções de build."),
    ChangeLogItem("1.6.0", "Sobre e diagnóstico", "Histórico visual, diagnóstico copiável e correção do preset Corvith."),
    ChangeLogItem("1.5.0", "Alarmes mais completos", "Soneca, som e vibração configuráveis individualmente por alarme."),
    ChangeLogItem("1.4.0", "Presets e acesso rápido", "Presets do overlay, bloco nas Configurações Rápidas e alarmes Zyrvorthian.")
)

@Composable
internal fun DiagnosticCard(
    diagnostics: AppDiagnostics,
    onCopy: () -> Unit,
    onFixOverlay: () -> Unit,
    onFixAccessibility: () -> Unit,
    onFixExactAlarms: () -> Unit,
    onFixNotifications: () -> Unit
) {
    ExpandableAboutCard(title = "Diagnóstico", summary = diagnosticSummary(diagnostics)) {
        DiagnosticRow("Overlay", if (diagnostics.overlayEnabled) "Ativo" else "Desativado", diagnostics.overlayEnabled)
        DiagnosticRow("Método", diagnostics.overlayMethod, diagnostics.overlayEnabled)
        DiagnosticRow(
            "Sobrepor apps",
            yesNo(diagnostics.drawOverlays),
            diagnostics.drawOverlays,
            if (!diagnostics.drawOverlays) onFixOverlay else null
        )
        DiagnosticRow(
            "Acessibilidade",
            yesNo(diagnostics.accessibility),
            diagnostics.accessibility,
            if (!diagnostics.accessibility) onFixAccessibility else null
        )
        DiagnosticRow(
            "Alarmes exatos",
            yesNo(diagnostics.exactAlarms),
            diagnostics.exactAlarms,
            if (!diagnostics.exactAlarms) onFixExactAlarms else null
        )
        DiagnosticRow(
            "Notificações",
            yesNo(diagnostics.notifications),
            diagnostics.notifications,
            if (!diagnostics.notifications) onFixNotifications else null
        )
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
    ExpandableAboutCard(
        title = "Últimas alterações",
        summary = "${recentChanges.first().version} • ${recentChanges.first().title}",
        initiallyExpanded = true
    ) {
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
    ExpandableAboutCard(title = "Apoiar o projeto", summary = "PIX disponível") {
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
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(DONATION_PIX_KEY, modifier = Modifier.weight(1f), color = AppColors.TextPrimary, fontSize = 13.sp)
                Text("Copiar", color = AppColors.AccentSoft, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
internal fun PrivacyCard() {
    ExpandableAboutCard(title = "Privacidade", summary = "Como a Acessibilidade é usada") {
        Text(
            "O modo por Acessibilidade apenas desenha a janela flutuante. O aplicativo não lê a tela e não executa cliques ou gestos.",
            color = AppColors.TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ExpandableAboutCard(
    title: String,
    summary: String,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    Surface(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        color = AppColors.Surface.copy(alpha = 0.92f)
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(title, color = AppColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(summary, color = AppColors.TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                }
                Text(if (expanded) "▲" else "▼", color = AppColors.AccentSoft, fontSize = 11.sp)
            }
            AnimatedVisibility(expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) { content() }
            }
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String, good: Boolean, onFix: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onFix != null) Modifier.clickable(onClick = onFix) else Modifier)
            .padding(vertical = if (onFix != null) 4.dp else 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), color = AppColors.TextSecondary, fontSize = 12.sp)
        Text(
            if (onFix != null) "$value  • Corrigir" else value,
            color = if (good) AppColors.Success else AppColors.Warning,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}

private fun diagnosticSummary(diagnostics: AppDiagnostics): String {
    val issues = listOf(
        diagnostics.drawOverlays || diagnostics.accessibility,
        diagnostics.exactAlarms,
        diagnostics.notifications
    ).count { !it }
    return if (issues == 0) "Tudo pronto" else "$issues item(ns) para revisar"
}

private fun yesNo(value: Boolean) = if (value) "Permitido" else "Não permitido"
