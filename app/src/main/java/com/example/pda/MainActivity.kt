package com.example.pda


import androidx.compose.material3.TextFieldDefaults
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

                // Destino inicial basado solo en el ID
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
                                navController.navigate("scanner/$busId") {
                                }
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
                                },onVolver = {
                                    // Esta es la clave: volver sin borrar nada del SessionManager
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

// En lugar de inicializar con null/vacío, leemos del SessionManager
    var selectedBusId by remember {
        mutableStateOf<Int?>(sessionManager.obtenerBusId().takeIf { it > 0 })
    }
    var selectedBusPatente by remember {
        mutableStateOf(sessionManager.obtenerBusNombre() ?: "Seleccione un bus")
    }

// Cargamos la lista de buses desde el cache inmediatamente
    var busList by remember {
        mutableStateOf(sessionManager.obtenerListaBusesCache())
    }

    var expanded by remember { mutableStateOf(false) }
    var estaSincronizando by remember { mutableStateOf(false) }
    var datosListos by remember { mutableStateOf(sessionManager.obtenerBusId() > 0) }

// Usamos una clave que no cambie, para que solo se ejecute al "montar" el componente
    LaunchedEffect(key1 = true) {
        // 1. Verificación instantánea
        val estudiantesLocales = db.estudianteDao().obtenerTodosDirecto()
        if (estudiantesLocales.isNotEmpty()) {
            datosListos = true
        }

        // 2. Control de saturación para el Backend
        if (estaSincronizando) return@LaunchedEffect

        try {
            estaSincronizando = true
            // Primero buses
            val respuestaBuses = RetrofitClient.instance.getBuses().filter { !it.deleted }
            if (respuestaBuses.isNotEmpty()) {
                busList = respuestaBuses
                sessionManager.guardarListaBuses(respuestaBuses)
            }

            // Luego estudiantes
            val alumnosApi = RetrofitClient.instance.getEstudiantesAutorizados()
            if (alumnosApi.isNotEmpty()) {
                // TRANSACCIÓN SEGURA: Solo borramos si tenemos con qué reemplazar
                db.estudianteDao().borrarTodo()
                db.estudianteDao().insertarEstudiantes(alumnosApi.map { it ->
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
            // Si hay error (como el 53300), datosListos sigue siendo true
            // si la verificación inicial (paso 1) fue exitosa.
        } finally {
            estaSincronizando = false
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("PDA - Control de Acceso", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Indicador de estado de sincronización
            if (estaSincronizando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Actualizando base de datos...", style = MaterialTheme.typography.bodySmall)
            } else if (datosListos) {
                Text("Base de datos lista para uso Offline", color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

// Campo de Selección de Bus
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedBusPatente,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Patente del Bus") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.DirectionsBus, null) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF355085),
                        unfocusedIndicatorColor = Color.Gray
                    )
                )

                // Capa de clic
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        if (busList.isNotEmpty()) {
                            expanded = true
                        } else {
                            Toast.makeText(context, "Lista vacía. Conéctese a internet una vez.", Toast.LENGTH_LONG).show()
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
                            text = { Text(bus.bus_patente) },
                            onClick = {
                                selectedBusPatente = bus.bus_patente
                                selectedBusId = bus.bus_id
                                expanded = false
                                // Persistencia física
                                sessionManager.guardarBus(bus.bus_id, bus.bus_patente)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón Principal
            Button(
                onClick = {
                    selectedBusId?.let { onIrAlScanner(it) }
                },
                // El botón se habilita si hay datos descargados Y un bus seleccionado
                enabled = datosListos && selectedBusId != null,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF355085))
            ) {
                Icon(Icons.Default.QrCodeScanner, null)
                Spacer(Modifier.width(8.dp))
                Text("INICIAR ESCÁNER QR")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Historial
            OutlinedButton(
                onClick = onVerHistorial,
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Icon(Icons.Default.History, null)
                Spacer(Modifier.width(8.dp))
                Text("VER PASAJEROS DE HOY")
            }
        }
    }
}