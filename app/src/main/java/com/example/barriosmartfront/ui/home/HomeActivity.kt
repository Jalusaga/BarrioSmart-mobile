package com.example.barriosmartfront.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.dto.auth.NavItem
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.ui.authorities.ContactActivity

import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SurfaceSoft

class HomeActivity : ComponentActivity() {
    private val tokenStore by lazy { DataStoreTokenStore(applicationContext) }

    private val retrofit by lazy {
        ApiClient.create(
            baseUrl = "http://10.0.2.2:8000/", tokenStore = tokenStore
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeguridadTheme {
                // Llamamos al composable que está definido aparte
                HomeRoute(
                    onButtonClick = {
                        // Aquí defines la acción del botón
                        // Por ejemplo: mostrar un log o navegar
                    }
                )
            }
        }
    }
}



// ---- Navigation-less route ----
@Composable
fun HomeRoute(
    onButtonClick: () -> Unit
) {
    val context = LocalContext.current

    val menuItems = listOf(
        NavItem("Comunidades", Icons.Filled.Group),
        NavItem("Reportes", Icons.Filled.ReportProblem),
        NavItem("Autoridades", Icons.Filled.Security)
    )

    // Estado para saber qué ítem está seleccionado (simulación de navegación)
    var selectedItem by remember { mutableStateOf(0) }

    // Contenedor principal: Ocupa toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        SurfaceSoft, Color.White
                    )
                )
            )
            .systemBarsPadding()
    ) {

        // 1. Encabezado Fijo en la Parte Superior
        Column(
            // El modifier garantiza que esta columna se pegue a la parte superior (default)
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Arrangement.Top por defecto, no es necesario especificarlo aquí
        ) {
            // Header Seguridad Comunitaria"
            Text(
                text = "BarrioSmart",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Seguridad Comunitaria",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Protege tu comunidad y mantente informado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }

        // 2. Botón SOS Centrado en la Pantalla
        Column(
            // Usa fillMaxSize() y Arrangement.Center para tomar todo el espacio y centrar
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Esto es solo para separar el botón del encabezado si el encabezado
            // llega muy abajo en la pantalla (opcional, puedes probar sin él)
            Spacer(Modifier.height(100.dp))

            // ------------------ BOTÓN SOS ------------------
            Button(
                onClick = onButtonClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .size(300.dp)
                    .padding(30.dp)
            ) {
                Text(
                    "SOS",
                    fontSize = 50.sp
                )
            }
            // -----------------------------------------------

            Spacer(Modifier.height(4.dp))

            // Texto "Botón de pánico" debajo del botón
            Text(
                text = "Botón de pánico",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                fontSize = 20.sp
            )
        }

        // 3. BARRA DE NAVEGACIÓN INFERIOR (Menú)
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter // Alineación clave: Fija el menú abajo
        ) {
            NavigationBar(
                // Puedes personalizar el color de fondo aquí
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                menuItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (item.title == "Autoridades") {
                                val intent = Intent(context, ContactActivity::class.java)
                                context.startActivity(intent)
                            }
                        }

                    )
                }
            }
        }
    }

}