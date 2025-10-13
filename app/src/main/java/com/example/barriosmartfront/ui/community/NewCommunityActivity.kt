package com.example.barriosmartfront.ui.community

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SmartTopAppBar

class NewCommunityActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SeguridadTheme {
                NewCommunityRoute(
                    onBackClick = { finish() },
                    onCreateSuccess = {
                        // Lógica después de crear (ej: mostrar mensaje, volver a la lista)
                        finish()
                    }
                )
            }
        }
    }
}

// =========================================================================
// RUTA PRINCIPAL
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCommunityRoute(
    onBackClick: () -> Unit,
    onCreateSuccess: (Community) -> Unit
) {
    // ⭐️ Estado del formulario
    var formState by remember { mutableStateOf(Community()) }
    val isFormValid = formState.name.isNotBlank() && formState.description.isNotBlank()

    Scaffold(
        topBar = {
            SmartTopAppBar(
                title = "Crear Nueva Comunidad",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Define un nombre y una descripción para la comunidad que deseas crear. Podrás invitar a tus vecinos más tarde.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // -------------------- Campo Nombre --------------------
            OutlinedTextField(
                value = formState.name,
                onValueChange = { formState = formState.copy(name = it) },
                label = { Text("Nombre de la Comunidad") },
                placeholder = { Text("Ej: Vecinos de Barrio Centro") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // -------------------- Campo Descripción --------------------
            OutlinedTextField(
                value = formState.description,
                onValueChange = { formState = formState.copy(description = it) },
                label = { Text("Descripción (Máx. 200 caracteres)") },
                placeholder = { Text("Propósito de la comunidad, zona cubierta, etc.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(24.dp))


            // -------------------- Botón Crear --------------------
            Button(
                onClick = {
                    // ⭐️ Lógica de creación de comunidad
                    // Aquí iría la llamada al API

                    // Simulación de éxito:
                    val newCommunity = Community(
                        id = (100..999).random(), // ID simulado
                        name = formState.name,
                        description = formState.description,
                        is_active = true,
                        isJoined = true
                    )
                    onCreateSuccess(newCommunity)
                },
                enabled = isFormValid, // Deshabilitado si faltan campos
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Crear Comunidad", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}