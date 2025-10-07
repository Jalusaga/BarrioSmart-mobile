package com.example.barriosmartfront.ui.report


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun ReporteCard(report: Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${report.description} • Reportado por ${report.reporter}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = report.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Etiqueta de Estado (similar a la imagen fddde8.png)
            val color = when (report.status) {
                ReportStatus.APROBADO -> Color(0xFF4CAF50) // Verde
                ReportStatus.PENDIENTE -> Color(0xFFFF9800) // Naranja
                ReportStatus.RECHAZADO -> Color(0xFFF44336) // Rojo
            }
            Text(
                text = report.status.name.capitalize(),
                color = Color.White,
                modifier = Modifier
                    .background(color, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}