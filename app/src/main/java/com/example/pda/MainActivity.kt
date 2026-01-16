package com.example.pda

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.pda.api.RetrofitClient
import com.example.pda.database.SessionManager
import com.example.pda.database.AppDatabase
import com.example.pda.database.entities.EstudianteEntity
import com.example.pda.ui.screens.HistorialScreen
import com.example.pda.ui.screens.ScannerScreen
import com.example.pda.ui.theme.PDATheme
import com.example.pda.workers.SyncWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)
        setupSyncWorker()
        verificarYSolicitarPermisos()

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
                            onIrAlScanner = { busId -> navController.navigate("scanner/$busId") },
                            onVerHistorial = { navController.navigate("historial") }
                        )
                    }

                    composable("scanner/{busId}") { backStackEntry ->
                        val busId = backStackEntry.arguments?.getString("busId")?.toIntOrNull() ?: -1
                        if (busId <= 0) {
                            LaunchedEffect(Unit) { navController.navigate("seleccion") { popUpTo(0) } }
                        } else {
                            ScannerScreen(
                                busId = busId,
                                onCerrarSesion = {
                                    sessionManager.cerrarSesionBus()
                                    navController.navigate("seleccion") { popUpTo(0) }
                                },
                                onVolver = { navController.popBackStack() }
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

    private fun verificarYSolicitarPermisos() {
        val permissionsNeeded = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        val notGranted = permissionsNeeded.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            requestPermissionLauncher.launch(notGranted.toTypedArray())
        }
    }

    private fun setupSyncWorker() {
        val workerConstraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
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

@Composable
fun OfflineStatusBadge(datosListos: Boolean) {
    val colorBase = if (datosListos) Color(0xFF4CAF50) else Color(0xFFF44336)
    Surface(
        color = colorBase.copy(alpha = 0.1f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorBase.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (datosListos) Icons.Default.CloudDone else Icons.Default.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = colorBase
            )
            Text(
                text = if (datosListos) "MODO OFFLINE ACTIVO" else "SIN DATOS LOCALES",
                style = MaterialTheme.typography.labelLarge,
                color = colorBase,
                fontWeight = FontWeight.ExtraBold
            )
        }
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
    val db = AppDatabase.getDatabase(context)
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var hasCamera by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    var hasLocation by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) }

    var selectedBusId by remember { mutableStateOf<Int?>(sessionManager.obtenerBusId().takeIf { it > 0 }) }
    var selectedBusPatente by remember { mutableStateOf(sessionManager.obtenerBusNombre() ?: "Seleccione un bus") }
    var busList by remember { mutableStateOf(sessionManager.obtenerListaBusesCache()) }
    var expanded by remember { mutableStateOf(false) }
    var estaSincronizando by remember { mutableStateOf(false) }
    var datosListos by remember { mutableStateOf(false) }

    // Función para actualizar datos (Llamada al iniciar y por el botón)
    val actualizarDatosAction = {
        scope.launch {
            try {
                estaSincronizando = true
                // 1. Obtener Buses
                val respuestaBuses = RetrofitClient.instance.getBuses().filter { !it.deleted }
                if (respuestaBuses.isNotEmpty()) {
                    busList = respuestaBuses
                    sessionManager.guardarListaBuses(respuestaBuses)
                }

                // 2. Obtener Estudiantes y Limpiar previos
                val alumnosApi = RetrofitClient.instance.getEstudiantesAutorizados()
                if (alumnosApi.isNotEmpty()) {
                    db.estudianteDao().borrarTodo() // <--- Limpieza de datos antiguos
                    db.estudianteDao().insertarEstudiantes(alumnosApi.map {
                        EstudianteEntity(it.per_id, it.pna_nom, it.pna_apat ?: "", it.pna_amat ?: "", it.token, it.est_sem_id, it.qr_id)
                    })
                    datosListos = true
                    Toast.makeText(context, "Sincronización exitosa", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SYNC", "Error: ${e.message}")
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            } finally {
                estaSincronizando = false
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                hasLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        val locales = db.estudianteDao().obtenerTodosDirecto()
        if (locales.isNotEmpty()) { datosListos = true }
        actualizarDatosAction()
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), Color.White)))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.size(90.dp).background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(50.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("CONTROL DE ACCESO", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            // FILA DE ESTADO Y BOTÓN DE REFRESH
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                OfflineStatusBadge(datosListos = datosListos)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { actualizarDatosAction() },
                    enabled = !estaSincronizando,
                    modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, "Actualizar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                AnimatedVisibility(visible = !hasCamera || !hasLocation) {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(12.dp))
                            Text("Faltan permisos de Cámara/GPS.", color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }

                AnimatedVisibility(visible = estaSincronizando) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clip(CircleShape))
                }

                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp), shape = RoundedCornerShape(24.dp)) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Configuración", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedBusPatente, onValueChange = {}, readOnly = true,
                                label = { Text("Patente del Bus") }, modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) },
                                shape = RoundedCornerShape(16.dp)
                            )
                            Box(modifier = Modifier.matchParentSize().clickable { if (busList.isNotEmpty()) expanded = true })
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                busList.forEach { bus ->
                                    DropdownMenuItem(
                                        text = { Text(bus.bus_patente) },
                                        onClick = {
                                            selectedBusPatente = bus.bus_patente
                                            selectedBusId = bus.bus_id
                                            expanded = false
                                            sessionManager.guardarBus(bus.bus_id, bus.bus_patente)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.navigationBarsPadding()) {
                Button(
                    onClick = { selectedBusId?.let { onIrAlScanner(it) } },
                    enabled = datosListos && selectedBusId != null && hasCamera && hasLocation,
                    modifier = Modifier.fillMaxWidth().height(65.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, null)
                    Spacer(Modifier.width(12.dp))
                    Text("INICIAR ESCÁNER", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = onVerHistorial,
                    modifier = Modifier.fillMaxWidth().height(65.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.History, null)
                    Spacer(Modifier.width(12.dp))
                    Text("HISTORIAL LOCAL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}