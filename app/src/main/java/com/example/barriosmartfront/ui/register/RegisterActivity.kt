// ui/register/RegisterActivity.kt
package com.example.barriosmartfront.ui.register

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.barriosmartfront.data.auth.AuthService
import com.example.barriosmartfront.data.auth.DataStoreTokenStore
import com.example.barriosmartfront.data.auth.UsersService
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.data.repositories.AuthRepository
import com.example.barriosmartfront.ui.theme.SeguridadTheme

class RegisterActivity : ComponentActivity() {

    private val tokenStore by lazy { DataStoreTokenStore(applicationContext) }

    private val retrofit by lazy {
        ApiClient.create(
            baseUrl = "http://10.0.2.2:8000/", tokenStore = tokenStore
        )
    }

    private val authService by lazy { retrofit.create(AuthService::class.java) }
    private val usersService by lazy { retrofit.create(UsersService::class.java) }
    private val repo by lazy { AuthRepository(authService, usersService, tokenStore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeguridadTheme {
                val vm: RegisterViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST") return RegisterViewModel(repo) as T
                    }
                })
                RegisterRoute(vm = vm, onBackToLogin = { finish() } // go back AFTER snackbar
                )
            }
        }
    }
}

@Composable
fun RegisterRoute(
    vm: RegisterViewModel = viewModel(), onBackToLogin: () -> Unit
) {
    val s by vm.state
    val focus = LocalFocusManager.current

    // --- Snackbar setup
    val snackbarHostState = remember { SnackbarHostState() }
    var registered by remember { mutableStateOf(false) }

    // When registration succeeds, show snackbar then navigate back
    LaunchedEffect(registered) {
        if (registered) {
            snackbarHostState.showSnackbar(
                message = "✅ Usuario creado",
                actionLabel = "OK",
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
            onBackToLogin()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data, shape = MaterialTheme.shapes.medium
                )
            }
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.background
                        )
                    )
                )
                .systemBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {

                        OutlinedTextField(
                            value = s.fullName,
                            onValueChange = vm::updateFullName,
                            label = { Text("Nombre completo") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = s.email,
                            onValueChange = vm::updateEmail,
                            label = { Text("Correo electrónico") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = s.phone,
                            onValueChange = vm::updatePhone,
                            label = { Text("Teléfono") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        var show1 by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = s.password,
                            onValueChange = vm::updatePassword,
                            label = { Text("Contraseña") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (show1) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { show1 = !show1 }) {
                                    Icon(
                                        if (show1) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                            )
                        )

                        Spacer(Modifier.height(12.dp))

                        var show2 by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = s.confirm,
                            onValueChange = vm::updateConfirm,
                            label = { Text("Confirmar contraseña") },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (show2) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { show2 = !show2 }) {
                                    Icon(
                                        if (show2) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
                        )

                        AnimatedVisibility(visible = s.error != null) {
                            Text(
                                s.error ?: "",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {
                                focus.clearFocus()
                                // Register and, on success, flip the flag to show the snackbar
                                vm.register {
                                    registered = true
                                }
                            },
                            enabled = !s.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            if (s.isLoading) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp, modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Creando…")
                            } else {
                                Text("Crear cuenta")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    "Al registrarte aceptas nuestras políticas de uso.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
