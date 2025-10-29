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
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.repositories.CommunityRepository
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SmartTopAppBar

class NewCommunityActivity : ComponentActivity() {
    private lateinit var viewModel: CommunityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStore = DataStoreTokenStore(this)
        val repository = CommunityRepository(tokenStore)
        viewModel = CommunityViewModel(repository)

        setContent {
            SeguridadTheme {
                NewCommunityRoute(
                    onBackClick = { finish() },
                    viewModel = viewModel,
                    onCreateSuccess = { finish() }
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
    viewModel: CommunityViewModel,
    onCreateSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isFormValid = name.isNotBlank() && description.isNotBlank()

    Scaffold(
        topBar = {
            SmartTopAppBar(title = "Crear Nueva Comunidad", onBackClick = onBackClick)
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la Comunidad") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.createCommunity(name, description)
                    onCreateSuccess()
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Comunidad")
            }
        }
    }
}
