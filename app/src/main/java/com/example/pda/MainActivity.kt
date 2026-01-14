package com.example.pda

import androidx.compose.material3.TextFieldDefaults
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.pda.api.RetrofitClient
import com.example.pda.database.SessionManager
import com.example.pda.database.AppDatabase
import com.example.pda.database.entities.EstudianteEntity
import com.example.pda.models.BusResponse
import com.example.pda.ui.screens.HistorialScreen
import com.example.pda.ui.screens.ScannerScreen
import com.example.pda.ui.theme.PDATheme
import com.example.pda.workers.SyncWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)
        setupSyncWorker()

        enableEdgeToEdge()
        setContent {
            PDATheme {
                val navController = rememberNavController()

                val startDestination = if (sessionManager.estaBusSeleccionado()) {
                    "scanner/${sessionManager.obtenerBusId()}"
                } else {
                    "seleccion"
                }

                NavHost(navController = navController, startDestination = startDestination) {

                    composable("seleccion") {
                        MainScreen(
                            sessionManager = sessionManager,
                            onIrAlScanner = { busId ->
                                navController.navigate("scanner/$busId")
                            },
                            onVerHistorial = { navController.navigate("historial") }
                        )
                    }

                    composable("scanner/{busId}") { backStackEntry ->
                        val busIdString = backStackEntry.arguments?.getString("busId")
                        val busId = busIdString?.toIntOrNull() ?: -1
                        if (busId <= 0) {
                            LaunchedEffect(Unit) {
                                navController.navigate("seleccion") {
                                    popUpTo(0)
                                }
                            }
                        } else {
                            ScannerScreen(
                                busId = busId,
                                onCerrarSesion = {
                                    sessionManager.cerrarSesionBus()
                                    navController.navigate("seleccion") { popUpTo(0) }
                                },
                                onVolver = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    composable("historial") {
                        HistorialScreen(
                            db = AppDatabase.getDatabase(LocalContext.current),
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    private fun setupSyncWorker() {
        val workerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(workerConstraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SyncAsistencias",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    sessionManager: SessionManager,
    onIrAlScanner: (Int) -> Unit,
    onVerHistorial: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)

    var selectedBusId by remember {
        mutableStateOf<Int?>(sessionManager.obtenerBusId().takeIf { it > 0 })
    }
    var selectedBusPatente by remember {
        mutableStateOf(sessionManager.obtenerBusNombre() ?: "Seleccione un bus")
    }

    var busList by remember {
        mutableStateOf(sessionManager.obtenerListaBusesCache())
    }

    var expanded by remember { mutableStateOf(false) }
    var estaSincronizando by remember { mutableStateOf(false) }
    var datosListos by remember { mutableStateOf(sessionManager.obtenerBusId() > 0) }
    var mostrarMensajeBienvenida by remember { mutableStateOf(selectedBusId == null) }

    LaunchedEffect(key1 = true) {
        val estudiantesLocales = db.estudianteDao().obtenerTodosDirecto()
        if (estudiantesLocales.isNotEmpty()) {
            datosListos = true
        }

        if (estaSincronizando) return@LaunchedEffect

        try {
            estaSincronizando = true
            val respuestaBuses = RetrofitClient.instance.getBuses().filter { !it.deleted }
            if (respuestaBuses.isNotEmpty()) {
                busList = respuestaBuses
                sessionManager.guardarListaBuses(respuestaBuses)
            }

            val alumnosApi = RetrofitClient.instance.getEstudiantesAutorizados()
            if (alumnosApi.isNotEmpty()) {
                db.estudianteDao().borrarTodo()
                db.estudianteDao().insertarEstudiantes(alumnosApi.map {
                    EstudianteEntity(
                        per_id = it.per_id,
                        pna_nom = it.pna_nom,
                        pna_apat = it.pna_apat ?: "",
                        pna_amat = it.pna_amat ?: "",
                        token = it.token,
                        est_sem_id = it.est_sem_id,
                        qr_id = it.qr_id
                    )
                })
                datosListos = true
            }
        } catch (e: Exception) {
            Log.e("SYNC_ERROR", "Error: ${e.message}")
        } finally {
            estaSincronizando = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // HEADER CON LOGO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }

                Text(
                    text = "CONTROL DE ACCESO",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // CONTENIDO PRINCIPAL
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Estado de sincronización
                AnimatedVisibility(
                    visible = estaSincronizando,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "Actualizando base de datos...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Estado de datos listos
                AnimatedVisibility(
                    visible = datosListos && !estaSincronizando,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Base de datos lista para uso offline",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                // Selector de Bus
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = "Seleccionar Bus",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedBusPatente,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Patente del Bus") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable {
                                        if (busList.isNotEmpty()) {
                                            expanded = true
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Lista vacía. Conéctese a internet una vez.",
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                    }
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                busList.forEach { bus ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DirectionsBus,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = bus.bus_patente,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedBusPatente = bus.bus_patente
                                            selectedBusId = bus.bus_id
                                            expanded = false
                                            sessionManager.guardarBus(bus.bus_id, bus.bus_patente)
                                            mostrarMensajeBienvenida = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BOTONES DE ACCIÓN
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        selectedBusId?.let { onIrAlScanner(it) }
                    },
                    enabled = datosListos && selectedBusId != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "INICIAR ESCÁNER QR",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onVerHistorial,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 2.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "VER PASAJEROS DE HOY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}