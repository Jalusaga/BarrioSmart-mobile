package com.example.barriosmartfront.ui.community

import android.content.Intent
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
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.repositories.CommunityRepository
import com.example.barriosmartfront.data.dto.member.Member
import com.example.barriosmartfront.data.dto.report.ReportResponse
import com.example.barriosmartfront.ui.report.ReportCard
import com.example.barriosmartfront.ui.report.ReportDetailsActivity
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SmartTopAppBar

const val EXTRA_COMMUNITY_ID = "community_id"

class CommunityDetailsActivity : ComponentActivity() {

    private lateinit var viewModel: CommunityDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val communityId = intent.getIntExtra(EXTRA_COMMUNITY_ID, -1)
        if (communityId == -1) {
            finish()
            return
        }

        val tokenStore = DataStoreTokenStore(this)
        val repository = CommunityRepository(tokenStore)
        viewModel = CommunityDetailsViewModel(repository)

        setContent {
            SeguridadTheme {

                LaunchedEffect(Unit) {
                    viewModel.loadCommunityDetails(communityId)
                    viewModel.loadMembers(communityId)
                    viewModel.loadReports(communityId)
                }

                val community by viewModel.community.collectAsState()
                val members by viewModel.members.collectAsState()
                val reports by viewModel.reports.collectAsState()
                val loading by viewModel.loading.collectAsState()
                val error by viewModel.error.collectAsState()



                when {
                    loading || community ==null -> {
                        // Mientras se cargan los datos, mostramos el indicador
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(12.dp))
                                Text("Cargando comunidad...", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    error != null && community == null -> {
                        // Si hubo error, se muestra el mensaje
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error: $error")
                        }
                    }


                    else -> {
                        // Cuando sí hay comunidad, mostramos la UI principal
                        CommunityDetailsRoute(
                            community = community!!,
                            members = members,
                            reports = reports,
                            onBackClick = { finish() },
                            onLeaveCommunity = { /* TODO: API para salir */ },
                            onViewAllReports = { /* TODO: Navegar a lista completa */ }
                        )
                    }
                }


            }
        }
    }
}


// -------------------------------------------------------------------------
// Composable principal de la UI
// -------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailsRoute(
    community: CommunityResponse,
    members: List<Member>,
    reports: List<ReportResponse>,
    onBackClick: () -> Unit,
    onLeaveCommunity: () -> Unit,
    onViewAllReports: () -> Unit
) {
    val tabs = listOf("Resumen", "Miembros", "Reportes Recientes")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val onViewReportDetails: (Int) -> Unit = { reportId ->
        val intent = Intent(context, ReportDetailsActivity::class.java)
        intent.putExtra("EXTRA_REPORT_ID", reportId)
        context.startActivity(intent)
    }


    Scaffold(
        topBar = {
            SmartTopAppBar(
                title ="",
                onBackClick = onBackClick,
                actions = {
                    Button(
                        onClick = onLeaveCommunity,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
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
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {

            // Cabecera
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(10.dp))
                Text(community.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            }

            // Tabs
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, text = { Text(title) })
                }
            }

            // Contenido de cada pestaña
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                when (selectedTabIndex) {
                    0 -> TabResumen(community)
                    1 -> TabMiembros(members)
                    2 -> TabReportesRecientes(reports, onViewAllReports, onViewReportDetails)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Pestañas
// -------------------------------------------------------------------------

@Composable
fun TabResumen(community: CommunityResponse) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        if (!community.description.isNullOrBlank()) {
            SummaryCard(
                title = "Descripción",
                value = community.description,
                subtitle = "",
                icon = Icons.Filled.AddComment,
                iconColor = MaterialTheme.colorScheme.primary
            )
        }


        // Estado
        SummaryCard(
            title = "Estado",
            value = if (community.is_active) "Activa" else "Inactiva",
            subtitle = if (community.is_active)
                "Esta comunidad está actualmente en funcionamiento."
            else
                "Esta comunidad está inactiva temporalmente.",
            icon = Icons.Filled.NotificationsActive,
            iconColor = if (community.is_active) Color(0xFF4CAF50) else Color.Gray
        )

        // Cantidad de miembros
        SummaryCard(
            title = "Miembros",
            value = "${community.members.size}",
            subtitle = "Miembros registrados en la comunidad",
            icon = Icons.Filled.Group,
            iconColor = Color(0xFF2196F3)
        )

        // Cantidad de reportes
        SummaryCard(
            title = "Reportes",
            value = "${community.reports.size}",
            subtitle = "Reportes totales registrados",
            icon = Icons.Filled.Receipt,
            iconColor = Color(0xFFFF9800)
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun TabMiembros(members: List<Member>) {
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Spacer(Modifier.height(8.dp))
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

        if (members.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay miembros en esta comunidad")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(members, key = { it.id?:0 }) { member ->
                    MiembroCard(member)
                }
            }
        }
    }
}


@Composable
fun TabReportesRecientes(reports: List<ReportResponse>, onViewAllReports: () -> Unit, onViewReportDetails: (Int) -> Unit ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(16.dp))
        Text("Reportes Recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Text("Últimos incidentes reportados en la comunidad", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 8.dp).align(Alignment.Start))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Mostramos los 5 reportes más recientes
            items(reports.take(5), key = { it.id }) { report ->
                ReportCard(
                    report = report,
                    communityName = "",
                    typeName =  "Desconocido", // si tienes un campo tipo nombre
                    onViewReportDetails = onViewReportDetails
                )
                Spacer(Modifier.height(8.dp))
            }
        }


        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onViewAllReports) { Text("Ver Todos los Reportes") }
        Spacer(Modifier.height(16.dp))
    }
}

// -------------------------------------------------------------------------
// Componente reutilizable de resumen
// -------------------------------------------------------------------------

@Composable
fun SummaryCard(title: String, value: String, subtitle: String, icon: ImageVector, iconColor: Color) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor =Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}
@Composable
fun MiembroCard(member: Member) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor =Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del miembro
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(12.dp))

            // Información del miembro
            Column {
                Text(
                    text = member.full_name ?: "Sin nombre",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Rol: ${if (member.is_admin) "Administrador" else "Miembro"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}