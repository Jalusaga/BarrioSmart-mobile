package com.example.barriosmartfront.ui.report


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.text.font.FontWeight
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.dto.report.ReportStatus
import com.example.barriosmartfront.data.dto.report.ReportType
import com.example.barriosmartfront.data.dto.community.Community
import com.example.barriosmartfront.ui.theme.FilterButton
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SmartTopAppBar
import com.example.barriosmartfront.data.repositories.ReportsRepository
import com.example.barriosmartfront.data.repositories.ReportTypeRepository
import com.example.barriosmartfront.data.repositories.CommunityRepository
import com.example.barriosmartfront.ui.community.CommunityViewModel
import com.example.barriosmartfront.ui.types.ReportTypeViewModel
import com.example.barriosmartfront.data.services.ReportsService
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.data.services.ReportTypesService
import kotlin.getValue


class ReportActivity : ComponentActivity() {
    private val tokenStore by lazy { DataStoreTokenStore(applicationContext) }
    private val retrofit by lazy {
        ApiClient.create(baseUrl = "http://10.0.2.2:8000/", tokenStore = tokenStore)
    }

    private val reportsService by lazy { retrofit.create(ReportsService::class.java) }
    private val reportsRepo by lazy { ReportsRepository(reportsService) }
    private val reportsVm by lazy { ReportViewModel(reportsRepo) }



    private lateinit var communitiesVm: CommunityViewModel

    private val typesService by lazy { retrofit.create(ReportTypesService::class.java) }
    private val typesRepo by lazy { ReportTypeRepository(typesService) }
    private val typesVm by lazy { ReportTypeViewModel(typesRepo) }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        val barrioCentro = Community(id = 1, name = "Barrio Centro", description = "Comunidad central", is_active = true, isJoined = true)
        val barrioLaguinilla = Community(id = 1, name = "Lagunilla", description = "Comunidad central", is_active = true, isJoined = true)

        val tipoRobo = ReportType(1, "Robo")
        val tipoAgresion = ReportType(2, "Agresión")
        val tipoAcoso = ReportType(3, "Acoso")
        val tipoUrto = ReportType(4, "Urto")


        val sampleReports = listOf(
            Report(
                1,
                barrioCentro,
                tipoAgresion,
                "Actividad sospechosa en la plaza",
                "Grupo de personas merodeando cerca del parque infantil durante la madrugada",
                10.123456,
                -84.123456,
                "14/1/2024 22:30",
                ReportStatus.pending,
                false,
                101,
                null
            ),
            Report(2, barrioLaguinilla, tipoRobo, "Robo en tienda local", "Intento de robo con arma blanca", 10.654321, -84.654321, "21/5/2025 18:45", ReportStatus.pending, false, 102, null),
            Report(3, barrioLaguinilla, tipoAcoso, "Ruido excesivo en la noche", "Fiesta con música alta hasta las 4 AM", 10.222222, -84.222222, "4/12/2025 2:00", ReportStatus.pending, false, 103, null),
            Report(4, barrioCentro, tipoUrto, "Fuga de agua", "Gran charco en la calle principal", 10.333333, -84.333333, "1/10/2025 8:15", ReportStatus.approved, false, 104, 201)
        )
*/

        val repository = CommunityRepository(tokenStore)
        communitiesVm = CommunityViewModel(repository)

        setContent {
            SeguridadTheme {
                ReportListRoute(
                    vm = reportsVm,
                    cvm = communitiesVm,
                    rvm = typesVm,
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
    vm: ReportViewModel,
    cvm: CommunityViewModel,
    rvm: ReportTypeViewModel,
    onNavigateBack: () -> Unit,
    onCreateNewReport: () -> Unit,
    onViewReportDetails: (Int) -> Unit
) {


    val reports by vm.reports.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()


    // Communities y Types (obteniendo el valor actual del StateFlow)
    val communities by cvm.communities.collectAsState()
    val reportTypes by rvm.reportTypes.collectAsState()

    // Mapas para lookup por id
    val communitiesMap = communities.associateBy({ it.id }, { it.name })
    val typesMap = reportTypes.associateBy({ it.id }, { it.name })

    // Estados de filtros
    var searchText by remember { mutableStateOf("") }
    var selectedCommunity by remember { mutableStateOf("Todos") }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var selectedType by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) { vm.fetchReports() }

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
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
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

            // Filtros
            ReportFilters(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                selectedCommunity = selectedCommunity,
                selectedStatus = selectedStatus,
                selectedType = selectedType,
                onCommunityChange = { selectedCommunity = it },
                onStatusChange = { selectedStatus = it },
                onTypeChange = { selectedType = it }
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    val filteredReports = reports.filter {
                        it.title.contains(searchText, ignoreCase = true) &&
                                (selectedType == "Todos" || it.type_id.name.equals(
                                    selectedType,
                                    ignoreCase = true
                                )) &&
                                (selectedCommunity == "Todos" || it.community_id.name.equals( //Aqui community_id es un object
                                    selectedCommunity,
                                    ignoreCase = true
                                )) &&
                                (selectedStatus == "Todos" || it.status.name.equals(
                                    selectedStatus,
                                    ignoreCase = true
                                ))
                    }

                    items(filteredReports, key = { report -> report.id }) { report ->
                        val communityName = report.community_id.name ?: "Desconocido"
                        val typeName = report.type_id.name ?: "Desconocido"
                        ReportCard(
                            report = report,
                            communityName = communityName,
                            typeName = typeName)
                    }
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
    selectedStatus: String,
    selectedType: String,
    onCommunityChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onTypeChange: (String) -> Unit
) {
    val communityOptions = listOf("Todos", "Lagunilla", "Barrio Centro", "Santo Domingo")
    val typeOptions = listOf("Todos", "Agresión", "Acoso", "Robo", "Urto", "Amenaza", "Ruido")
    val statusOptions = listOf("Todos", "pending", "approved", "rejected")

    var communityExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Campo de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = { Text("Buscar reportes...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Text("Filtros Rápidos", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Comunidad
                Box(modifier = Modifier.weight(1f)) {
                    FilterButton(label = "Comunidad: $selectedCommunity", onClick = { communityExpanded = true })
                    DropdownMenu(
                        expanded = communityExpanded,
                        onDismissRequest = { communityExpanded = false }
                    ) {
                        communityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onCommunityChange(option)
                                    communityExpanded = false
                                }
                            )
                        }
                    }
                }

                // Tipo
                Box(modifier = Modifier.weight(1f)) {
                    FilterButton(label = "Tipo: $selectedType", onClick = { typeExpanded = true })
                    DropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        typeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onTypeChange(option)
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Estado
                Box(modifier = Modifier.weight(1f)) {
                    FilterButton(label = "Estado: $selectedStatus", onClick = { statusExpanded = true })
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onStatusChange(option)
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ReportCard(report: Report,
               communityName: String,
               typeName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(report.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                val color = when (report.status) {
                    ReportStatus.approved -> Color(0xFF4CAF50)
                    ReportStatus.pending -> Color(0xFFFF9800)
                    ReportStatus.rejected -> Color(0xFFF44336)
                }
                Text(report.status.name.capitalize(), color = Color.White,
                    modifier = Modifier.background(color, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(report.description ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Warning, contentDescription = "Tipo", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(typeName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(Modifier.width(12.dp))
                Icon(Icons.Filled.LocationOn, contentDescription = "Comunidad", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(communityName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(Modifier.width(12.dp))
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Fecha", modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(report.occurred_at, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(Modifier.height(16.dp))
                Icon(Icons.Filled.Person, contentDescription = "Reportero", modifier = Modifier.size(16.dp), tint = Color.Gray)

                Spacer(Modifier.height(16.dp))
                Text("Por ${report.reported_by_user_id}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            OutlinedButton(onClick = { /* Navegar a detalles */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Ver Detalles")
            }
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
