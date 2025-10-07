package com.example.barriosmartfront.ui.theme


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {} // Permite añadir botones extra (como "+ Nueva Comunidad")
) {
    TopAppBar(
        title = {
            // Centra el texto del título en la barra
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    // Usamos Color.White para asegurar contraste si containerColor es MaterialTheme.colorScheme.primary
                    color = Color.White
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.White // Color de la flecha
                )
            }
        },
        actions = actions, // Pasamos las acciones (ej: Botón + Nueva Comunidad)
        colors = TopAppBarDefaults.topAppBarColors(
            // Establece un color de fondo fijo para la barra superior (ej: el color primario)
            containerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = Color.White // Asegura que los iconos de acción sean blancos
        )
    )
}

@Composable
fun ScreenHeader(
    subtitle: String,
) {
    // Subtítulo estandarizado
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
    )
}
