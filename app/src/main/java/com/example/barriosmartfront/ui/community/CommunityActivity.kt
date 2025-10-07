package com.example.barriosmartfront.ui.community

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
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

class CommunityActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sampleCommunities = listOf(
            Community(1, 6, "Barrio Centro", "Comunidad del centro histórico de la ciudad con gran actividad comercial y residencial", true,  isJoined = false),
            Community(2, 20, "Zona Residencial Sur", "Comunidad del centro histórico de la ciudad con gran actividad comercial y residencial", true, isJoined = true),
            Community(3, 40, "Parque Industrial","Comunidad del centro histórico de la ciudad con gran actividad comercial y residencial",  true,  isJoined = false),
        )
        setContent {
            SeguridadTheme {
                CommunityRoute(  onNavigateBack = { finish() }, onNewCommunity = { finish() }, sampleCommunities )
            }
        }
    }
}

@Composable
fun CommunityCard(
    community: Community,
    onViewDetails: (Int) -> Unit,
    onJoinOrLeave: (Int, Boolean) -> Unit
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
            Text(
                text = community.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))

            // Descripción/Detalles
            Text(
                text = "Comunidad del centro histórico de la ciudad", // Descripción fija por ahora
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "${community.memberCount} miembros",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Zona activa",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(16.dp))

            // Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Ver Detalles
                OutlinedButton(
                    onClick = { onViewDetails(community.id) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver Detalles")
                }

                Spacer(Modifier.width(8.dp))

                // Botón Unirse / Salir
                val buttonText = if (community.isJoined) "Salir" else "Unirse"
                val buttonColor = if (community.isJoined) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                val contentColor = if (community.isJoined) Color.White else MaterialTheme.colorScheme.onPrimary

                Button(
                    onClick = { onJoinOrLeave(community.id, !community.isJoined) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = contentColor
                    )
                ) {
                    Text(buttonText)
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
    communities: List<Community>
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val navigateToDetails: (Int) -> Unit = { communityId ->
        val intent = Intent(context, CommunityDetailsActivity::class.java).apply {
            putExtra(EXTRA_COMMUNITY_ID, communityId)
        }
        context.startActivity(intent)
    }

    val navigateToNewCommunity: () -> Unit = {
        val intent = Intent(activity, NewCommunityActivity::class.java)
        activity.startActivity(intent)
    }

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "Comunidades Disponibles",
                onBackClick = onNavigateBack,
                actions = {
                    Button(
                        onClick = navigateToNewCommunity,
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(communities) { community ->
                    CommunityCard(
                        community = community,
                        onViewDetails = navigateToDetails,
                        onJoinOrLeave = { communityId, isJoining -> /* Lógica para unirse/salir */ }
                    )
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