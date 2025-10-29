package com.example.barriosmartfront.ui.report


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.ui.theme.FilterButton
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SmartTopAppBar


class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val barrioCentro = CommunityResponse(
            id = 1,
            name = "Barrio Centro",
            description = "Comunidad central",
            isActive = true,
            isJoined = true
        )

        val sampleReports = listOf(
            Report("Actividad sospechosa en la plaza", "Grupo de personas merodeando cerca del parque infantil durante la madrugada", "María González", "14/1/2024", ReportStatus.PENDIENTE, barrioCentro),
            Report("Robo en tienda local", "Intento de robo con arma blanca", "Juan Pérez", "21/5/2025", ReportStatus.PENDIENTE, barrioCentro),
            Report("Ruido excesivo en la noche", "Fiesta con música alta hasta las 4 AM", "Ana López", "4/12/2025", ReportStatus.PENDIENTE, barrioCentro),
            Report("Fuga de agua", "Gran charco en la calle principal", "Carlos Ruiz", "1/10/2025", ReportStatus.APROBADO, barrioCentro),
        )

        setContent {
            SeguridadTheme {
                ReportListRoute(
                    reports = sampleReports,
                    onNavigateBack = { finish() },
                    onCreateNewReport = { /* Iniciar NewReportActivity */ },
                    onViewReportDetails = { reportId -> /* Iniciar ReportDetailsActivity */ }
                )
            }
        }
    }
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Composable no se ejecutó en el contexto de una Activity")
}

// =========================================================================
// RUTA PRINCIPAL DE REPORTES
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListRoute(
    reports: List<Report>,
    onNavigateBack: () -> Unit,
    onCreateNewReport: () -> Unit,
    onViewReportDetails: (Int) -> Unit
) {
    // ⭐️ Estados para los filtros de búsqueda
    var searchText by remember { mutableStateOf("") }
    var selectedCommunity by remember { mutableStateOf("Todos") }
    var selectedStatus by remember { mutableStateOf("Pendiente") }
    // En una app real, usarías un objeto State para manejar los filtros.

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "Reportes de Incidentes",
                onBackClick = onNavigateBack,
                actions = {
                    Button(
                        onClick = onCreateNewReport,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor =  Color.White
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                            .border(
                                width = 2.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(20.dp)
                            ),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Crear Reporte", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Crear")
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

            // -------------------- Sección de Filtros de Búsqueda --------------------
            ReportFilters(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                selectedCommunity = selectedCommunity,
                selectedStatus = selectedStatus
            )

            // -------------------- Lista de Reportes --------------------
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Aquí aplicas los filtros a la lista de 'reports'
                val filteredReports = reports.filter {
                    // Simulación de filtros
                    it.title.contains(searchText, ignoreCase = true)
                }

                items(filteredReports) { report ->
                    ReportCard(
                        report = report
                    )
                }
            }
        }
    }
}

// =========================================================================
// COMPOSABLE DE FILTROS
// =========================================================================
@Composable
fun ReportFilters(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    selectedCommunity: String,
    selectedStatus: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ... (Textos informativos se mantienen) ...

            // Campo de búsqueda principal (OutlinedTextField)
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = { Text("Buscar reportes...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // ⭐️ Row de filtros con el nuevo estilo de botón
            Text(
                "Filtros Rápidos",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Espaciado horizontal
            ) {
                // Filtro Comunidad
                FilterButton(
                    label = "Comunidad: $selectedCommunity",
                    onClick = { /* Abrir diálogo o menú de comunidad */ },
                    modifier = Modifier.weight(1f)
                )

                // Filtro Tipo
                FilterButton(
                    label = "Tipo: Todos",
                    onClick = { /* Abrir diálogo o menú de tipo */ },
                    modifier = Modifier.weight(1f)
                )

                // Filtro Estado
                FilterButton(
                    label = "Estado: $selectedStatus",
                    onClick = { /* Abrir diálogo o menú de estado */ },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))

            // ⭐️ Campo de Fecha (Usando el estilo OutlinedField con label separado)
            Text("Fecha:", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = "mm / dd / yyyy",
                onValueChange = { /* No hay cambio real aquí */ },
                trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.clickable { /* Mostrar selector de fecha */ }) },
                singleLine = true,
                readOnly = true,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
        }
    }
}


// Componente para simular los Dropdowns de los filtros
@Composable
fun FilterDropdown(label: String, value: String, options: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.width(4.dp))
        }
        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(4.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                .height(40.dp)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .clickable { /* Abrir menú desplegable */ },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(value, style = MaterialTheme.typography.bodyMedium)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar")
        }
    }
}

const val EXTRA_REPORT_ID = "report_id"
@Composable
fun ReportCard(report: Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val context = LocalContext.current
        val activity = context.findActivity()

        val navigateToDetails: () -> Unit = {
//            val intent = Intent(activity, ReportDetailsActivity::class.java).apply {
//                // Usamos el hashCode como ID de simulación
//                putExtra(EXTRA_REPORT_ID, report.hashCode())
//            }
//            activity.startActivity(intent)
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Fila Superior (Título y Estado)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Etiqueta de Estado
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
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(4.dp))

            // Descripción
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tipo de Actividad
                Icon(Icons.Filled.Warning, contentDescription = "Tipo", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(report.title.split(" ").first(), style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(Modifier.width(12.dp))

                // Comunidad
                Icon(Icons.Filled.LocationOn, contentDescription = "Comunidad", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                //Text(report.community, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(Modifier.width(12.dp))

                // Fecha
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Fecha", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(report.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(Modifier.width(12.dp))

                // Reportero
                Icon(Icons.Filled.Person, contentDescription = "Reportero", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text("Por ${report.reporter.split(" ").first()}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))

            // ⭐️ Botón Ver Detalles (Necesario según la imagen)
            OutlinedButton(
                onClick = navigateToDetails,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Detalles")
            }
        }
    }
}