package com.example.barriosmartfront.ui.report

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.dto.report.ReportResponse
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.data.repositories.CommunityRepository
import com.example.barriosmartfront.data.repositories.ReportTypeRepository
import com.example.barriosmartfront.data.repositories.ReportsRepository
import com.example.barriosmartfront.data.services.ReportTypesService
import com.example.barriosmartfront.ui.community.CommunityViewModel
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.types.ReportTypeViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val EXTRA_REPORT = "report_id"

class ReportDetailsActivity : ComponentActivity() {
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

        val reportId = intent.getIntExtra(EXTRA_REPORT, -1)
        if (reportId == -1) finish()

        val reportsRepo = ReportsRepository(tokenStore)
        val communityRepo = CommunityRepository(tokenStore)
        val reportTypeRepo = ReportTypeRepository(typesService)

        reportViewModel = ReportViewModel(reportsRepo)
        communityViewModel = CommunityViewModel(communityRepo)
        reportTypeViewModel = ReportTypeViewModel(reportTypeRepo)

        // Cargar datos
        communityViewModel.loadCommunities()
        reportTypeViewModel.fetchReportTypes()
        reportViewModel.fetchReports() // O un método específico por ID

        setContent {
            SeguridadTheme {
                ReportDetailsScreen(
                    vm = reportViewModel,
                    cvm = communityViewModel,
                    rtvm = reportTypeViewModel,
                    reportId = reportId,
                    onBackClick = { finish() },
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailsScreen(
    vm: ReportViewModel,
    cvm: CommunityViewModel,
    rtvm: ReportTypeViewModel,
    reportId: Int,
    onBackClick: () -> Unit
) {
    val reports by vm.reports.collectAsState()
    val communities by cvm.communities.collectAsState()
    val reportTypes by rtvm.reportTypes.collectAsState()

    val report: ReportResponse? = reports.find { it.id == reportId }

    if (report == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando reporte...", color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    // Estados de edición
    var isEditing by remember { mutableStateOf(false) }
    var uiState by remember { mutableStateOf(report) }
    var selectedLat by remember { mutableStateOf(report.latitude) }
    var selectedLng by remember { mutableStateOf(report.longitude) }
    var dateString by remember { mutableStateOf(report.occurred_at.take(10)) }
    var timeString by remember { mutableStateOf(report.occurred_at.takeLast(5)) }

    var selectedIncidentType by remember { mutableStateOf(
        reportTypes.find { it.id == report.type_id }?.display_name ?: ""
    ) }

    var selectedCommunity by remember { mutableStateOf(
        communities.find { it.id == report.community_id }?.name ?: ""
    ) }

    var isAnonymousCheck by remember { mutableStateOf(report.reported_by_user_id == null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detalles del Reporte",
                        color = Color.White // 🔹 Texto blanco para contraste
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White // 🔹 Ícono blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // 🔹 Color igual al otro Scaffold
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (isEditing) {
                            // Guardar cambios
                            val typeId =
                                reportTypes.find { it.display_name == selectedIncidentType }?.id
                                    ?: report.type_id
                            val communityId = communities.find { it.name == selectedCommunity }?.id
                                ?: report.community_id

                            val occurredAtIso = runCatching {
                                LocalDateTime.parse(
                                    "$dateString $timeString",
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            }.getOrElse {
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            }

                            val updatedReport = report.copy(
                                title = uiState.title,
                                description = uiState.description,
                                type_id = typeId,
                                community_id = communityId,
                                occurred_at = occurredAtIso,
                                reported_by_user_id = if (isAnonymousCheck) null else report.reported_by_user_id
                            )

                            Log.d("ReportDetails", "Tipo seleccionado: $selectedIncidentType")

                            vm.updateReport(updatedReport)
                        }
                        isEditing = !isEditing
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isEditing) "Guardar" else "Editar", color = Color.White)
                }
            }
        }

    ) {  paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reporte Anónimo", modifier = Modifier.weight(1f))
                Switch(
                    checked = isAnonymousCheck,
                    onCheckedChange = {
                        if (isEditing) isAnonymousCheck = it
                    }, // solo cambia si isEditing
                    enabled = isEditing // desactiva visualmente cuando no se puede editar
                )

            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { if (isEditing) uiState = uiState.copy(title = it) },
                label = { Text("Resumen del incidente") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = !isEditing
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.description ?: "",
                onValueChange = { if (isEditing) uiState = uiState.copy(description = it) },
                label = { Text("Descripción del incidente") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                readOnly = !isEditing,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fecha y hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DateTimeInputField(
                    "Fecha",
                    dateString,
                    { if (isEditing) dateString = it },
                    "yyyy-MM-dd",
                    Modifier.weight(1f)
                )
                DateTimeInputField(
                    "Hora",
                    timeString,
                    { if (isEditing) timeString = it },
                    "HH:mm",
                    Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DropdownSelector(
                label = "Tipo de incidente",
                options = reportTypes.map { it.display_name },
                selectedValue = selectedIncidentType,
                onValueSelected = { if (isEditing) selectedIncidentType = it },
                enabled = isEditing
            )

            Spacer(modifier = Modifier.height(16.dp))

            DropdownSelector(
                label = "Comunidad",
                options = communities.map { it.name },
                selectedValue = selectedCommunity,
                onValueSelected = { if (isEditing) selectedCommunity = it },
                enabled = isEditing
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (selectedLat != null && selectedLng != null) {
                val location = LatLng(selectedLat!!, selectedLng!!)
                val cameraPositionState = rememberCameraPositionState {
                    position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                        location,
                        15f
                    )
                }

                Text(
                    text = "Ubicación del incidente",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            if (isEditing) {
                                selectedLat = latLng.latitude
                                selectedLng = latLng.longitude
                            }
                        }
                    ) {
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(
                                position = LatLng(
                                    selectedLat!!,
                                    selectedLng!!
                                )
                            ),
                            title = "Ubicación reportada"
                        )
                    }
                }
            } else {
                Text("No se registró ubicación en este reporte.")
            }
        }

    }
}
