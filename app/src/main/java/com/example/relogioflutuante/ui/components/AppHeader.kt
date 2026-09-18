package com.example.relogioflutuante.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@Composable
fun AppHeader(
    onOpenSetup: () -> Unit,
    onOpenAbout: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 6.dp, top = 12.dp, bottom = 6.dp),
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
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = "Mais opções",
                tint = AppColors.TextPrimary
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Permissões e configuração") },
                onClick = {
                    expanded = false
                    onOpenSetup()
                }
            )
            DropdownMenuItem(
                text = { Text("Sobre") },
                onClick = {
                    expanded = false
                    onOpenAbout()
                }
            )
        }
    }
}
