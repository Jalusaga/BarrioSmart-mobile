package com.example.barriosmartfront.ui.report

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.dto.report.Report
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.data.repositories.CommunityRepository
import com.example.barriosmartfront.data.repositories.ReportTypeRepository
import com.example.barriosmartfront.data.repositories.ReportsRepository
import com.example.barriosmartfront.data.services.ReportTypesService
import com.example.barriosmartfront.ui.community.CommunityViewModel
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.types.ReportTypeViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NewReportActivity: ComponentActivity() {
    private val tokenStore by lazy { DataStoreTokenStore(applicationContext) }
    private val retrofit by lazy {
        ApiClient.create(baseUrl = "http://10.0.2.2:8000/", tokenStore = tokenStore)
    }
    private lateinit var reportViewModel: ReportViewModel
    private lateinit var communityViewModel: CommunityViewModel
    private lateinit var reportTypeViewModel: ReportTypeViewModel
    private val typesService by lazy { retrofit.create(ReportTypesService::class.java) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val reportsRepo = ReportsRepository(tokenStore)
        val communityRepo = CommunityRepository(tokenStore)
        val reportTypeRepo = ReportTypeRepository(typesService)

        reportViewModel = ReportViewModel(reportsRepo)
        communityViewModel = CommunityViewModel(communityRepo)
        reportTypeViewModel = ReportTypeViewModel(reportTypeRepo)

        // Cargar datos iniciales
        communityViewModel.loadCommunities()
        reportTypeViewModel.fetchReportTypes()

        setContent {
            SeguridadTheme {
                NewReportRoute(
                    reportViewModel = reportViewModel,
                    communityViewModel = communityViewModel,
                    reportTypeViewModel = reportTypeViewModel,
                    onBackClick = { finish() },
                    onCreateSuccess = { finish() }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewReportRoute(
    reportViewModel: ReportViewModel,
    communityViewModel: CommunityViewModel,
    reportTypeViewModel: ReportTypeViewModel,
    onBackClick: () -> Unit,
    onCreateSuccess: () -> Unit
) {
    // Obtener la fecha y hora actual para inicializar los estados separados
    val (currentDate, currentTime) = remember { getCurrentDateTimeStrings() }

    // 1. ESTADO PRINCIPAL: Report (ahora es una data class, permite .copy())
    // Se inicializa con valores por defecto.
    var uiState by remember { mutableStateOf(Report()) }

    // 2. ESTADOS ADICIONALES
    var dateString by remember { mutableStateOf(currentDate) }
    var timeString by remember { mutableStateOf(currentTime) }
    var selectedIncidentType by remember { mutableStateOf("") }
    var selectedCommunity by remember { mutableStateOf("") }
    var locationString by remember { mutableStateOf("") }

    // 💥 NUEVO ESTADO: Manejar el check de Anónimo
    var isAnonymousCheck by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        communityViewModel.loadCommunities()
        reportTypeViewModel.fetchReportTypes()
    }

    // Collectar Flows
    val communities by communityViewModel.communities.collectAsState()
    val reportTypes by reportTypeViewModel.reportTypes.collectAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar( // Componente Material 3 (soluciona error de API experimental)
                title = { Text("Nuevo Reporte") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            //val communityId = communities.find { it.name == selectedCommunity }?.id ?: 0
                            val typeId = reportTypes.find { it.display_name == selectedIncidentType }?.id ?: 0
                            val communityId = 1

                            val occurredAtIso = runCatching {
                                LocalDateTime.parse(
                                    "$dateString $timeString",
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                                ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            }.getOrElse {
                                // Por si hay un error de parse, usar la fecha/hora actual
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            }


                            // 3. Mapeo: Determinar el ID de usuario reportador
                            // Si es anónimo (true), el ID es null. Si no lo es, se asume un ID.
                            val reportedById: Int? = if (isAnonymousCheck) null else 1 // Ejemplo: ID 10 para el usuario logeado

                            // 4. Crear el objeto Report final usando .copy() (ahora funciona)
                            val newReport = uiState.copy(
                                community_id = communityId,
                                type_id = typeId,
                                status = "pending",
                                occurred_at = occurredAtIso,
                                description = uiState.description?.ifEmpty { null },
                                reported_by_user_id = reportedById,
                                approved_by_user_id = null
                            )

                            reportViewModel.createReport(newReport)
                            onCreateSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28a745))
                    ) {
                        Text("Crear", color = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nuevo Reporte de Incidente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reporte Anónimo", modifier = Modifier.weight(1f))
                Switch(
                    checked = isAnonymousCheck,
                    onCheckedChange = { isAnonymousCheck = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { uiState = uiState.copy(title = it) },
                label = { Text("Resumen del incidente") },
                modifier = Modifier.fillMaxWidth()
            )

            // Descripción
            OutlinedTextField(
                value = uiState.description ?: "",
                onValueChange = { uiState = uiState.copy(description = it) },
                label = { Text("Descripción del incidente") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha y Hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DateTimeInputField(
                    label = "Fecha",
                    value = dateString,
                    onValueChange = { dateString = it },
                    hint = "dd/mm/yyyy",
                    modifier = Modifier.weight(1f)
                )
                DateTimeInputField(
                    label = "Hora",
                    value = timeString,
                    onValueChange = { timeString = it },
                    hint = "hh:mm",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tipo de incidente
            DropdownSelector(
                label = "Tipo de incidente",
                options = reportTypes.map { it.display_name },
                selectedValue = selectedIncidentType,
                onValueSelected = { selectedIncidentType = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Comunidad
            DropdownSelector(
                label = "Comunidad",
                options = communities.map { it.name },
                selectedValue = selectedCommunity,
                onValueSelected = { selectedCommunity = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ubicación
            OutlinedTextField(
                value = locationString,
                onValueChange = { locationString = it },
                label = { Text("Ubicación del incidente") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}



@Composable
fun DateTimeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "$label:", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(hint) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (selectedValue.isNotEmpty()) selectedValue else "Seleccione"

    Column {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = displayText,
            onValueChange = { },
            readOnly = true,
            enabled = enabled,
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable(enabled = enabled) {
                        expanded = !expanded
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { expanded = !expanded }
        )
        if (enabled) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
@RequiresApi(Build.VERSION_CODES.O)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDateTimeStrings(): Pair<String, String> {
    val now = LocalDateTime.now()
    val date = now.format(dateFormatter)
    val time = now.format(timeFormatter)
    return Pair(date, time)
}