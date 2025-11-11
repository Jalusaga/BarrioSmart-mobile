package com.example.barriosmartfront.ui.community

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barriosmartfront.ui.theme.ScreenHeader
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SmartTopAppBar
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.dto.community.CommunityResponse
import com.example.barriosmartfront.data.repositories.CommunityRepository

class CommunityActivity : ComponentActivity() {

    private lateinit var viewModel: CommunityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instancias necesarias para el ViewModel
        val tokenStore = DataStoreTokenStore(this)
        val repository = CommunityRepository(tokenStore)
        viewModel = CommunityViewModel(repository)

        setContent {
            SeguridadTheme {
                CommunityRoute(
                    onNavigateBack = { finish() },
                    onNewCommunity = { navigateToNewCommunity() },
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCommunities()
    }
    private fun navigateToNewCommunity() {
        val intent = Intent(this, NewCommunityActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun CommunityCard(
    community: CommunityResponse,
    onViewDetails: (Int) -> Unit,
    onJoin: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Nombre de la comunidad
            Text(
                text = community.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            // Descripción (placeholder por ahora porque backend no la manda aún)
            Text(
                text = "Comunidad cercana a tu zona",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(8.dp))

            // Info secundaria / metadata mock
            Text(
                text = "Zona activa",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(16.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ver Detalles
                OutlinedButton(
                    onClick = { onViewDetails(community.id) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver Detalles")
                }

                Spacer(Modifier.width(8.dp))

                // Unirse (siempre unirse por ahora)
                Button(
                    onClick = { onJoin(community.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Unirse")
                }
            }
        }
    }
}


// =========================================================================
// FUNCIÓN PRINCIPAL DE LA RUTA
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityRoute(
    onNavigateBack: () -> Unit,
    onNewCommunity: () -> Unit,
    viewModel: CommunityViewModel
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val communities by viewModel.communities.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Cargar comunidades al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadCommunities()
    }

    val navigateToDetails: (Int) -> Unit = { communityId ->
        val intent = Intent(context, CommunityDetailsActivity::class.java).apply {
            putExtra(EXTRA_COMMUNITY_ID, communityId)
        }
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "Comunidades",
                onBackClick = onNavigateBack,
                actions = {
                    Button(
                        onClick = onNewCommunity,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Nueva Comunidad", modifier = Modifier.size(16.dp))
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenHeader(
                subtitle = "Únete a comunidades cercanas para mantenerte informado sobre la seguridad en tu área"
            )

            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(communities) { community ->
                            CommunityCard(
                                community = community,
                                onViewDetails = navigateToDetails,
                                onJoin = { id ->
                                    // llamar al ViewModel para unirse a la comunidad
                                    // viewModel.joinCommunity(id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) { // Mientras sea una capa envolvente...
        if (context is Activity) return context // Si es la Activity, la retornamos
        context = context.baseContext // Si no, vamos a la capa base (el contexto envuelto)
    }
    throw IllegalStateException("Composable no se ejecutó en el contexto de una Activity")
}