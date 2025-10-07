package com.example.barriosmartfront.ui.community


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barriosmartfront.ui.member.Member
import com.example.barriosmartfront.ui.member.MiembroCard
import com.example.barriosmartfront.ui.report.Report
import com.example.barriosmartfront.ui.report.ReportCard
import com.example.barriosmartfront.ui.theme.SmartTopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.barriosmartfront.ui.report.ReportStatus
import com.example.barriosmartfront.ui.theme.SeguridadTheme


// Define la clave para pasar el ID a través del Intent
const val EXTRA_COMMUNITY_ID = "community_id"

class CommunityDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Obtener el ID de la comunidad del Intent
        val communityId = intent.getIntExtra(EXTRA_COMMUNITY_ID, -1)

        // Si el ID es inválido, podrías cerrar la actividad o mostrar un error.
        if (communityId == -1) {
            finish()
            return
        }


        val sampleMembers = listOf(
            Member("MG", "Maria González", "14/1/2024", "Administrador"),
            Member("JP", "Juan Pérez", "19/1/2024"),
            Member("AL", "Ana López", "31/1/2024"),
            Member("CR", "Carlos Ruiz", "14/2/2024"),
            Member("LM", "Laura Martin", "29/2/2024"),
        )

        val sampleReports = listOf(
            Report("Actividad sospechosa en la plaza", "Actividad Sospechosa", "María González", "15/3/2024 a las 10:30", ReportStatus.PENDIENTE, Community()),
            Report("Robo en tienda local", "Robo", "Juan Pérez", "14/3/2024 a las 15:45", ReportStatus.APROBADO, Community()),
            Report("Ruido excesivo en la noche", "Ruido Excesivo", "Ana López", "13/3/2024 a las 22:15", ReportStatus.APROBADO, Community()),
        )

        val community = Community(1, 5, "Barrio Centro", "Comunidad del centro histórico de la ciudad con gran actividad comercial y residencial", true,  isJoined = false) // Reemplaza con una llamada a tu ViewModel/Repository
        val members = sampleMembers
        val reports = sampleReports

        setContent {
            SeguridadTheme {
                CommunityDetailsRoute(
                    community = community,
                    members = members,
                    reports = reports,
                    onBackClick = { finish() }, // Cierra la Activity al retroceder
                    onLeaveCommunity = { /* Lógica de API + cerrar la Activity */ },
                    onViewAllReports = { /* Lógica para navegar a otra Activity/Composable */ }
                )
            }
        }
    }
}
// -------------------------------------------------------------------------
// PESTAÑAS (TABS)
// -------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailsRoute(
    community: Community,
    members: List<Member>,
    reports: List<Report>,
    onBackClick: () -> Unit,
    onLeaveCommunity: () -> Unit,
    onViewAllReports: () -> Unit
) {
    val tabs = listOf("Resumen", "Miembros", "Reportes Recientes")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = community.name, // El título puede ser dinámico
                onBackClick = onBackClick,
                actions = {
                    // Botón Salir de la Comunidad (diseño de la imagen)
                    Button(
                        onClick = onLeaveCommunity,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .heightIn(min = 36.dp)
                    ) {
                        Text("Salir de la Comunidad", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // -------------------- Cabecera de la Comunidad (Info fija) --------------------
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = community.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                Text(
                    text = community.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))

                // Metadatos (45 miembros, Creada el)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Group, contentDescription = "Miembros", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text("${community.memberCount} miembros", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                    Spacer(Modifier.width(16.dp))

                    // Etiqueta Activa
                    Text(
                        text = "Activa",
                        color = Color(0xFF4CAF50), // Verde
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // -------------------- Tab Bar --------------------
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // -------------------- Contenido de las Pestañas --------------------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> TabResumen(community)
                    1 -> TabMiembros(members)
                    2 -> TabReportesRecientes(reports, onViewAllReports)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// CONTENIDO DE CADA PESTAÑA
// -------------------------------------------------------------------------

@Composable
fun TabResumen(community: Community) {
    // Implementación basada en image_fddb25.png (similar a las tarjetas de Autoridades)
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(16.dp))

        // Tarjeta de Miembros Activos
        SummaryCard(
            title = "Miembros Activos",
            value = "${community.memberCount}",
            subtitle = "+3 este mes",
            icon = Icons.Filled.Group,
            iconColor = MaterialTheme.colorScheme.primary
        )

        // Tarjeta de Reportes del Mes
        SummaryCard(
            title = "Reportes del Mes",
            value = "12",
            subtitle = "-2 vs mes anterior",
            icon = Icons.Filled.Warning,
            iconColor = Color(0xFFFF9800) // Naranja
        )

        // Tarjeta de Emergencias
        SummaryCard(
            title = "Emergencias",
            value = "2",
            subtitle = "Botones de pánico activados",
            icon = Icons.Filled.Phone,
            iconColor = Color.Red
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun TabMiembros(members: List<Member>) {
    // Implementación basada en image_fddb41.png
    Column {
        Spacer(Modifier.height(16.dp))
        Text(
            "Miembros de la Comunidad",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Lista de todos los miembros activos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(members) { member ->
                MiembroCard(member)
                Divider()
            }
        }
    }
}

@Composable
fun TabReportesRecientes(reports: List<Report>, onViewAllReports: () -> Unit) {
    // Implementación basada en image_fddde8.png
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(16.dp))
        Text(
            "Reportes Recientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            "Últimos incidentes reportados en la comunidad",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp).align(Alignment.Start)
        )

        reports.forEach { report ->
            ReportCard(report)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(onClick = onViewAllReports) {
            Text("Ver Todos los Reportes")
        }
        Spacer(Modifier.height(16.dp))
    }
}

// =========================================================================
// COMPONENTE AUXILIAR PARA LAS TARJETAS DE RESUMEN (REUTILIZACIÓN DE ESTILO)
// =========================================================================

@Composable
fun SummaryCard(title: String, value: String, subtitle: String, icon: ImageVector, iconColor: Color) {
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}


