package com.example.barriosmartfront.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.core.content.ContextCompat
import com.example.barriosmartfront.data.dto.auth.NavItem
import com.example.barriosmartfront.ui.authorities.AuthoritiesActivity
import com.example.barriosmartfront.ui.community.CommunityActivity
import com.example.barriosmartfront.ui.report.ReportActivity

import com.example.barriosmartfront.ui.theme.SeguridadTheme
import com.example.barriosmartfront.ui.theme.SurfaceSoft
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.material3.MaterialTheme
import com.example.barriosmartfront.data.auth.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.BroadcastReceiver
import androidx.core.app.NotificationCompat

class HomeActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var sentReceiverRef: BroadcastReceiver? = null
    private var deliveredReceiverRef: BroadcastReceiver? = null

    override fun onDestroy() {
        super.onDestroy()
        sentReceiverRef?.let { unregisterReceiver(it) }
        deliveredReceiverRef?.let { unregisterReceiver(it) }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "panic_channel",
                "Alertas de emergencia",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de envío y entrega de mensajes de emergencia"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(this, "panic_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        setContent {
            SeguridadTheme {
                val context = this

                // Declarar launcher para pedir permiso
                val callPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        Toast.makeText(
                            context,
                            "Contactando a las autoridades...",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(Intent.ACTION_CALL).apply {
                            data = Uri.parse("tel:911") // Cambia por el número real
                        }
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "Permiso para llamadas denegado",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                // Launcher para solicitar permiso de SMS
                val smsPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        sendEmergencySMS("911") // o número de contacto de emergencia
                    } else {
                        Toast.makeText(context, "Permiso para enviar SMS denegado", Toast.LENGTH_SHORT).show()
                    }
                }

                // Pasar la lógica de llamada al botón SOS
                HomeRoute(
                    onButtonClick = {
                        // Primero enviar SMS, luego llamar
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            sendEmergencySMS("911")
                        } else {
                            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                        }

                        /*if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            //makeEmergencyCall()
                        } else {
                            //callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                        }*/
                    }
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendEmergencySMS(phoneNumber: String) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val latitude = location?.latitude ?: 0.0
            val longitude = location?.longitude ?: 0.0
            val locationText = "https://maps.google.com/?q=$latitude,$longitude"
            val nombre = SessionManager.getUserName() ?: "Usuario desconocido"

            val message = """
            🚨 Reporte de pánico 🚨
            Necesito ayuda, mi nombre es $nombre.
            Estoy ubicado en: $locationText
        """.trimIndent()

            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber") // Usa smsto: para SMS
                putExtra("sms_body", message)
            }

            try {
                startActivity(smsIntent)
                Toast.makeText(this, "Abriendo app de mensajes...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la app de SMS", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /*private fun fetchCurrentLocation(onLocationRetrieved: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission de ubicación no otorgado", Toast.LENGTH_SHORT).show()
            onLocationRetrieved(null)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                onLocationRetrieved(location)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error obteniendo ubicación", Toast.LENGTH_SHORT).show()
                onLocationRetrieved(null)
            }
    }


    //  Realiza la llamada
    private fun makeEmergencyCall() {
        Toast.makeText(this, "Contactando a las autoridades...", Toast.LENGTH_SHORT).show()
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:911")
        }
        startActivity(intent)
    }
*/
}

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
            var selectedItem by remember { mutableStateOf(-1) }


            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                menuItems.forEachIndexed { index, item ->
                    val interactionSource = remember { MutableInteractionSource() }

                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = MaterialTheme.colorScheme.primary // color fijo
                            )
                        },
                        label = { Text(item.title) },
                        selected = false, // no queremos que cambie visualmente
                        onClick = {
                            selectedItem = index
                            when (item.title) {
                                "Autoridades" -> context.startActivity(Intent(context, AuthoritiesActivity::class.java))
                                "Comunidades" -> context.startActivity(Intent(context, CommunityActivity::class.java))
                                "Reportes" -> context.startActivity(Intent(context, ReportActivity::class.java))
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = Color.Transparent // sin fondo permanente
                        ),
                        interactionSource = interactionSource,
                        // Ripple gris claro al pulsar
                        // `indication` se aplica al interactionSource
                        // Nota: Solo disponible en Compose >= 1.4
                    )
                }
            }

        }
    }
}