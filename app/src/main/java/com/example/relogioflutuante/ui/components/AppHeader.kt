package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relogioflutuante.R
import com.example.relogioflutuante.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    onOpenSetup: () -> Unit,
    onOpenAbout: () -> Unit
) {
    var showMenuSheet by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 12.dp, top = 12.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Relógio Flutuante",
                color = AppColors.TextPrimary,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Hora, contagem e modo sobreposto",
                color = AppColors.TextSecondary,
                fontSize = 11.sp
            )
        }
        Surface(
            shape = CircleShape,
            color = AppColors.SurfaceStrong.copy(alpha = 0.96f),
            tonalElevation = 3.dp,
            shadowElevation = 6.dp
        ) {
            IconButton(onClick = { showMenuSheet = true }, modifier = Modifier.size(42.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_vert),
                    contentDescription = "Mais opções",
                    tint = AppColors.TextPrimary
                )
            }
        }
    }

    if (showMenuSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            containerColor = AppColors.SurfaceStrong,
            contentColor = AppColors.TextPrimary,
            shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(top = 10.dp),
                    color = AppColors.TextSecondary.copy(alpha = 0.28f),
                    shape = CircleShape
                ) {
                    androidx.compose.foundation.layout.Box(
                        Modifier
                            .size(width = 44.dp, height = 4.dp)
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Menu rápido",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Acesse configurações e informações do aplicativo.",
                    color = AppColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                HeaderMenuTile(
                    title = "Permissões e configuração",
                    subtitle = "Overlay, Acessibilidade e ativação guiada",
                    onClick = {
                        showMenuSheet = false
                        onOpenSetup()
                    }
                )
                HeaderMenuTile(
                    title = "Sobre",
                    subtitle = "Versão, funcionamento e privacidade",
                    onClick = {
                        showMenuSheet = false
                        onOpenAbout()
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderMenuTile(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = AppColors.Surface.copy(alpha = 0.92f),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
