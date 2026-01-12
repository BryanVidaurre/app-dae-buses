package com.example.pda



import com.example.pda.api.RetrofitClient
import com.example.pda.models.BusResponse
import com.example.pda.models.EstudianteAutorizado
import com.example.pda.models.CreateIngresoBusDto
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.pda.database.AppDatabase
import com.example.pda.database.entities.EstudianteEntity
import com.example.pda.ui.screens.HistorialScreen
import com.example.pda.ui.screens.ScannerScreen
import com.example.pda.ui.theme.PDATheme
import com.example.pda.workers.SyncWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el sincronizador automático al abrir la app
        setupSyncWorker()

        enableEdgeToEdge()
        setContent {
            PDATheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "seleccion") {
                    composable("seleccion") {
                        MainScreen(
                            onIrAlScanner = { busId ->
                                navController.navigate("scanner/$busId")
                            },
                            onVerHistorial = {
                                navController.navigate("historial")
                            }
                        )
                    }
                    composable("scanner/{busId}") { backStackEntry ->
                        val busId = backStackEntry.arguments?.getString("busId")?.toInt() ?: 0
                        ScannerScreen(busId = busId)
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
        // Usamos androidx.work.Constraints explícitamente para evitar conflictos
        val workerConstraints = androidx.work.Constraints.Builder()
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
fun MainScreen(onIrAlScanner: (Int) -> Unit, onVerHistorial: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)

    var selectedBusPatente by remember { mutableStateOf("Seleccione un bus") }
    var selectedBusId by remember { mutableStateOf<Int?>(null) }
    var busList by remember { mutableStateOf<List<BusResponse>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var estaSincronizando by remember { mutableStateOf(false) }
    var datosListos by remember { mutableStateOf(false) }

    // Cargar lista de buses al iniciar
    LaunchedEffect(Unit) {
        try {
            busList = RetrofitClient.instance.getBuses().filter { !it.deleted }
            // Verificar si ya hay alumnos en la DB local para habilitar el botón
            scope.launch {
                val conteo = db.estudianteDao().obtenerTodosDirecto().size
                if (conteo > 0) datosListos = true
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error de red: Revise IP del servidor", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("PDA - Control de Acceso", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            if (estaSincronizando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Sincronizando alumnos...", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Selector de Bus (Dropdown)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedBusPatente,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Patente del Bus") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.DirectionsBus, null) }
                )
                // Capa transparente para detectar el click sobre el TextField
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true })

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
                                scope.launch {
                                    estaSincronizando = true
                                    try {
                                        val alumnos = RetrofitClient.instance.getEstudiantesAutorizados()
                                        db.estudianteDao().borrarTodo()
                                        db.estudianteDao().insertarEstudiantes(alumnos.map {
                                            EstudianteEntity(it.per_id, it.pna_nom, it.token, it.est_sem_id, it.qr_id)
                                        })
                                        datosListos = true
                                        Toast.makeText(context, "Alumnos sincronizados offline", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Log.e("PDA_DEBUG", "Error: ${e.message}")
                                        Toast.makeText(context, "Fallo al descargar alumnos", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        estaSincronizando = false
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón para ir al Scanner
            Button(
                onClick = { selectedBusId?.let { onIrAlScanner(it) } },
                enabled = datosListos && selectedBusId != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("INICIAR ESCÁNER QR")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir al Historial (Modo Offline)
            OutlinedButton(
                onClick = onVerHistorial,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("VER PASAJEROS DE HOY")
            }
        }
    }
}