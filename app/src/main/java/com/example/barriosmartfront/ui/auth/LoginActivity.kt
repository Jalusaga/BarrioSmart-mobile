// LoginActivity.kt
package com.example.barriosmartfront.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.barriosmartfront.data.repositories.AuthRepository
import com.example.barriosmartfront.data.auth.AuthService
import com.example.barriosmartfront.data.auth.DataStoreTokenStore

import com.example.barriosmartfront.data.auth.UsersService
import com.example.barriosmartfront.data.remote.ApiClient
import com.example.barriosmartfront.ui.home.HomeActivity
import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SurfaceSoft
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.jvm.java

class LoginActivity : ComponentActivity() {
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
                val vm: LoginViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST") return LoginViewModel(repo) as T
                    }
                })
                LoginRoute(
                    vm = vm, onLogin = { _, _ ->
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    })
            }
        }
    }
}

// ---- Navigation-less route ----
@Composable
fun LoginRoute(
    vm: LoginViewModel = viewModel(), onLogin: (String, String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val focus = LocalFocusManager.current
    val scope = rememberCoroutineScope()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
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

            Spacer(Modifier.height(24.dp))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Tarjeta de Login (bordes redondeados grandes)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Iniciar Sesión",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = vm.email,
                        onValueChange = vm::updateEmail,
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        ),
                        leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    var show by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = vm.password,
                        onValueChange = vm::updatePassword,
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focus.clearFocus()
                        }),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { show = !show }) {
                                Icon(
                                    if (show) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    AnimatedVisibility(visible = vm.error != null) {
                        Text(
                            vm.error ?: "",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            focus.clearFocus()
                            scope.launch {
                                vm.run {
                                    doLogin(
                                        onSuccess = {
                                            onLogin(
                                                email, password
                                            )
                                        })
                                }
                            }
                        },
                        enabled = !vm.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (vm.isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp, modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Ingresando…")
                        } else {
                            Text("Ingresar")
                        }
                    }

                    TextButton(
                        onClick = {
                            context.startActivity(
                                android.content.Intent(
                                    context,
                                    com.example.barriosmartfront.ui.register.RegisterActivity::class.java
                                )
                            )
                        }, modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("¿No tienes cuenta? Regístrate") }

                    TextButton(
                        onClick = { /* TODO: recuperación */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("¿Olvidaste tu contraseña?") }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Pie con tono informativo, similar a tus pantallas
            AssistChip(
                onClick = { /* tips de seguridad */ },
                label = { Text("Consejos de seguridad y contacto 911") },
                leadingIcon = {
                    Icon(Icons.Default.Info, contentDescription = null)
                },
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}
