package com.example.barriosmartfront.ui.report


import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.barriosmartfront.ui.theme.FilterButton
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SmartTopAppBar
import com.example.barriosmartfront.data.repositories.ReportsRepository
import com.example.barriosmartfront.data.repositories.ReportTypeRepository
import com.example.barriosmartfront.data.repositories.CommunityRepository
import com.example.barriosmartfront.ui.community.CommunityViewModel
import com.example.barriosmartfront.ui.types.ReportTypeViewModel
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.auth.UsersService
import com.example.barriosmartfront.data.dto.report.ReportResponse
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.data.services.ReportTypesService
import com.example.barriosmartfront.ui.community.NewCommunityActivity
import kotlin.getValue

const val EXTRA_REPORT_ID = "report_id"
class ReportActivity : ComponentActivity() {
    private val tokenStore by lazy { DataStoreTokenStore(applicationContext) }
    private val retrofit by lazy { ApiClient.create(baseUrl = "http://10.0.2.2:8000/", tokenStore = tokenStore) }

    private val reportsRepo by lazy { ReportsRepository(tokenStore) }
    private val reportsVm by lazy { ReportViewModel(reportsRepo) }
    private lateinit var communitiesVm: CommunityViewModel

    private lateinit var usersVm: UsersService


    private val typesService by lazy { retrofit.create(ReportTypesService::class.java) }
    private val typesRepo by lazy { ReportTypeRepository(typesService) }
    private val typesVm by lazy { ReportTypeViewModel(typesRepo) }

    private val refreshReportsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            reportsVm.fetchReports()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportsVm.fetchReports()
        val repository = CommunityRepository(tokenStore)
        communitiesVm = CommunityViewModel(repository)

        setContent {
            SeguridadTheme {
                ReportListRoute(
                    vm = reportsVm,
                    cvm = communitiesVm,
                    rvm = typesVm,
                    onNavigateBack = { finish() },
                    onCreateNewReport = { navigateToNewReport() },
                    onViewReportDetails = { reportId -> navigateToReportDetails(reportId) }
                )
            }
        }
    }

    private fun navigateToNewReport() {
        val intent = Intent(this, NewReportActivity::class.java)
        refreshReportsLauncher.launch(intent)
    }

    private fun navigateToReportDetails(reportId: Int) {
        val intent = Intent(this, ReportDetailsActivity::class.java)
        intent.putExtra(EXTRA_REPORT_ID, reportId)
        startActivity(intent)
    }
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
    LaunchedEffect(Unit) {
        cvm.loadCommunities()
        rvm.fetchReportTypes()
        vm.fetchReports()
    }

    val communities by cvm.communities.collectAsState()
    val reportTypes by rvm.reportTypes.collectAsState()
    val reports by vm.reports.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    val communitiesMap = remember(communities) { communities.associate { it.id to it.name } }
    val typesMap = remember(reportTypes) { reportTypes.associate { it.id to it.display_name } }

    // <-- Generamos las opciones usando map pero con lambda nombrada (evita posible inferencia fallida)
    val communityOptions: List<String> = remember(communities) {
        listOf("Todos") + communities.map { c -> c.name }
    }
    val typeOptions: List<String> = remember(reportTypes) {
        listOf("Todos") + reportTypes.map { t -> t.display_name }
    }

    var searchText by remember { mutableStateOf("") }
    var selectedCommunity by remember { mutableStateOf("Todos") }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var selectedType by remember { mutableStateOf("Todos") }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "Reportes",
                onBackClick = onNavigateBack,
                actions = {
                    Button(
                        onClick = onCreateNewReport,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Filled.Add,
                            contentDescription = "Crear",
                            modifier = Modifier.size(16.dp))
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
                onTypeChange = { selectedType = it },
                communityOptions = communityOptions,
                typeOptions = typeOptions
            )

            val filteredReports = remember(reports, searchText, selectedType, selectedCommunity, selectedStatus) {
                reports.filter {
                    it.title.contains(searchText, ignoreCase = true) &&
                            (selectedType == "Todos" || typesMap[it.type_id]?.equals(selectedType, ignoreCase = true) == true) &&
                            (selectedCommunity == "Todos" || communitiesMap[it.community_id]?.equals(selectedCommunity, ignoreCase = true) == true) &&
                            (selectedStatus == "Todos" || it.status.equals(selectedStatus, ignoreCase = true))
                }
            }

            when {
                isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {


                    items(filteredReports, key = { it.id }) { report ->
                        val communityName = communitiesMap[report.community_id] ?: "Desconocido"
                        val typeName = typesMap[report.type_id] ?: "Desconocido"
                        ReportCard(
                            report = report,
                            communityName = communityName,
                            typeName = typeName,
                            onViewReportDetails = onViewReportDetails
                        )
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
    onTypeChange: (String) -> Unit,
    communityOptions: List<String>,
    typeOptions: List<String>
) {
    val statusOptions = listOf("Todos", "pending", "approved", "rejected")

    var communityExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Comunidad
                Box(modifier = Modifier.weight(1f)) {
                    FilterButton(label = "Comunidad: $selectedCommunity", onClick = { communityExpanded = true })
                    DropdownMenu(expanded = communityExpanded, onDismissRequest = { communityExpanded = false }) {
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
                    DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
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
                    DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
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

// =========================================================================
// COMPOSABLE DE CADA TARJETA DE REPORTE
// =========================================================================
@Composable
fun ReportCard(
    report: ReportResponse,
    communityName: String,
    typeName: String,
    onViewReportDetails: (Int) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(report.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                val color = when (report.status) {
                    "approved" -> Color(0xFF4CAF50)
                    "pending" -> Color(0xFFFF9800)
                    "rejected" -> Color(0xFFF44336)
                    else -> Color.Gray
                }
                Text(
                    report.status,
                    color = Color.White,
                    modifier = Modifier.background(color, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(report.description ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

            Spacer(Modifier.height(8.dp))

            // Fila 1: Tipo y Comunidad
            Row(
                modifier = Modifier.padding(0.dp, 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = "Tipo", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(typeName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Comunidad", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(communityName, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(1.dp))

            // Fila 2: Fecha y Reportero
            Row(
                modifier = Modifier.padding(0.dp, 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = "Fecha", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(report.occurred_at, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = "Reportero", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text("Por ${report.id}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            OutlinedButton(
                onClick = { onViewReportDetails(report.id) },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Ver Detalles")
            }
        }
    }
}

