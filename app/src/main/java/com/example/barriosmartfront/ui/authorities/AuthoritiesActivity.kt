package com.example.barriosmartfront.ui.authorities


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.ui.theme.ScreenHeader
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SmartTopAppBar


class AuthoritiesActivity : ComponentActivity() {
    private val tokenStore by lazy { DataStoreTokenStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authorities = listOf(
            Authority(
                title = "Policía Nacional",
                phone = "2586-4000",
                description = "Reportes de crímenes y situaciones que requieren intervención policial",
                availability = "Disponible 24/7",
                backgroundColor = Color(0xFFE3F2FD), // Azul claro
                icon = Icons.Filled.Security,
                iconColor = Color(0xFF1E88E5) // Azul
            ),
            Authority(
                title = "Bomberos",
                phone = "2528-0000",
                description = "Incendios, rescates y emergencias con materiales peligrosos",
                availability = "Disponible 24/7",
                backgroundColor = Color(0xFFFBE9E7), // Rosa pálido
                icon = Icons.Filled.Call,
                iconColor = Color(0xFFE53935) // Rojo
            ),
            Authority(
                title = "Cruz Roja",
                phone = "2528-0000",
                description = "Servicios médicos de emergencia y primeros auxilios",
                availability = "Disponible 24/7",
                backgroundColor = Color(0xFFE8F5E9), // Verde claro
                icon = Icons.Filled.LocalHospital,
                iconColor = Color(0xFF43A047) // Verde
            )
        )

        setContent {
            SeguridadTheme {
                // Llamamos al composable que está definido aparte
                AuthoritiesRoute(  onBackClick = { finish() }, authorities )
            }
        }
    }
}


@Composable
fun AuthorityCard(info: Authority) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = info.backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono de Check / Escudo
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = info.iconColor,
                modifier = Modifier.size(24.dp).padding(end = 4.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = info.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = info.phone,
                    style = MaterialTheme.typography.headlineSmall,
                    color = info.iconColor,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = info.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = info.availability,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Botón "Llamar"
            Button(
                onClick = { /* Lógica de llamada: Ejemplo: dialer.dial(info.phone) */ },
                colors = ButtonDefaults.buttonColors(containerColor = info.iconColor)
            ) {
                Text("Llamar")
            }
        }
    }
}

// =========================================================================
// 2. FUNCIÓN PRINCIPAL DE LA RUTA
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthoritiesRoute(onBackClick: () -> Unit = {}, authorities: List<Authority>) {

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "Contacto con Autoridades",
                onBackClick = onBackClick,
            )
        }
    ) { paddingValues ->

        // Contenedor principal que permite el scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // -------------------- Título y Subtítulo -------------------
            ScreenHeader(
                subtitle = "Encuentra información de contacto de emergencia, autoridades locales y recursos útiles para situaciones de seguridad"
            )

            // -------------------- Bloque de Emergencia Inmediata (Llamar 911) --------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), // Fondo muy pálido
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ReportProblem, contentDescription = "Emergencia", tint = Color.Red)
                        Spacer(Modifier.width(8.dp))
                        Text("En caso de emergencia inmediata", fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                    Text(
                        text = "Si te encuentras en peligro inmediato, no dudes en contactar directamente a los servicios de emergencia",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                    Button(
                        onClick = { /* Lógica de llamada al 911 */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Llamar 911")
                    }
                }
            }

            // -------------------- Bloques de Contacto Generales --------------------
            AuthorityCard(
                info = Authority(
                    title = "Emergencia Generales",
                    phone = "911",
                    description = "Policía, bomberos y servicios médicos de emergencia",
                    availability = "Disponible 24/7",
                    backgroundColor = Color(0xFFFBE9E7),
                    icon = Icons.Filled.Call,
                    iconColor = Color(0xFFE53935)
                )
            )

            authorities.forEach { info ->
                AuthorityCard(info = info)
            }

            // -------------------- Información Importante --------------------
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Información Importante",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Consejos para contactar servicios de emergencia:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    // Puedes reemplazar esto con un bucle de Bullet Points real si es necesario
                    Text(text = "• Tu ubicación exacta (dirección, referencias)")
                    Text(text = "• Naturaleza de la emergencia")
                    Text(text = "• Número de personas involucradas")
                    Text(text = "• Tu nombre y número de contacto")
                    Text(text = "• Mantén la calma y sigue las instrucciones del operador")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}