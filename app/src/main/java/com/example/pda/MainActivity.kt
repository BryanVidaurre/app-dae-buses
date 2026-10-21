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
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.*
import com.example.pda.api.RetrofitClient
import com.example.pda.database.AppDatabase
import com.example.pda.database.SessionManager
import com.example.pda.database.entities.EstudianteEntity
import com.example.pda.ui.screens.HistorialScreen
import com.example.pda.ui.screens.ScannerScreen
import com.example.pda.ui.theme.PDATheme
import com.example.pda.workers.SyncWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(this, "Permisos necesarios para el escáner y GPS", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sessionManager = SessionManager(this)

        verificarYSolicitarPermisos()
        setupSyncWorker()

        setContent {
            PDATheme {
                val navController = rememberNavController()
                val db = AppDatabase.getDatabase(LocalContext.current)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        modifier = Modifier.padding(innerPadding).fillMaxSize()
                    ) {
                        composable("main") {
                            MainScreen(
                                sessionManager = sessionManager,
                                onIrAlScanner = { busId ->
                                    Log.d("PDA_NAV", "Navegando a Scanner: $busId")
                                    navController.navigate("scanner/$busId")
                                },
                                onVerHistorial = {
                                    Log.d("PDA_NAV", "Navegando a Historial")
                                    navController.navigate("historial")
                                }
                            )
                        }
                        composable(
                            route = "scanner/{busId}",
                            arguments = listOf(navArgument("busId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val busId = backStackEntry.arguments?.getInt("busId") ?: 0
                            ScannerScreen(
                                busId = busId,
                                onCerrarSesion = {
                                    sessionManager.cerrarSesionBus()
                                },
                                onVolver = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("historial") {
                            HistorialScreen(
                                db = db,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun verificarYSolicitarPermisos() {
        val permissionsNeeded = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
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
            "SyncAsistencias", ExistingPeriodicWorkPolicy.KEEP, syncRequest
        )
    }
}

@Composable
fun OfflineStatusBadge(datosListos: Boolean, cantidad: Int) {
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
                text = if (datosListos) "LISTO ($cantidad ALUMNOS)" else "SIN DATOS LOCALES",
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
    var hasLocation by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var selectedBusId by remember { mutableStateOf<Int?>(sessionManager.obtenerBusId().takeIf { it > 0 }) }
    var selectedBusPatente by remember { mutableStateOf(sessionManager.obtenerBusNombre() ?: "Seleccione un bus") }
    var busList by remember { mutableStateOf(sessionManager.obtenerListaBusesCache()) }
    var expanded by remember { mutableStateOf(false) }

    var estaSincronizando by remember { mutableStateOf(false) }
    var datosListos by remember { mutableStateOf(false) }
    var cantidadAlumnos by remember { mutableIntStateOf(0) }

    val actualizarDatosAction = {
        scope.launch {
            try {
                estaSincronizando = true
                val respuestaBuses = RetrofitClient.instance.getBuses().filter { !it.deleted }
                busList = respuestaBuses
                sessionManager.guardarListaBuses(respuestaBuses)

                val alumnosApi = RetrofitClient.instance.getEstudiantesAutorizados()
                db.estudianteDao().borrarTodo()
                val entidades = alumnosApi.mapNotNull { api ->
                    api.per_id?.let { id ->
                        EstudianteEntity(id, api.pna_nom, api.pna_apat, api.pna_amat, api.token, api.est_sem_id, api.qr_id)
                    }
                }
                db.estudianteDao().insertarEstudiantes(entidades)
                cantidadAlumnos = entidades.size
                datosListos = true
            } catch (e: Exception) {
                Log.e("SYNC", "Error sincronización", e)
                val locales = db.estudianteDao().obtenerTodosDirecto()
                if (locales.isNotEmpty()) {
                    cantidadAlumnos = locales.size
                    datosListos = true
                }
            } finally {
                estaSincronizando = false
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                hasLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                              ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        val locales = db.estudianteDao().obtenerTodosDirecto()
        if (locales.isNotEmpty()) {
            cantidadAlumnos = locales.size
            datosListos = true
        }
        actualizarDatosAction()
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), Color.White)))) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DirectionsBus, null, modifier = Modifier.size(45.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("CONTROL DE ACCESO", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                OfflineStatusBadge(datosListos = datosListos, cantidad = cantidadAlumnos)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { actualizarDatosAction() }, enabled = !estaSincronizando) {
                    Icon(if (estaSincronizando) Icons.Default.Sync else Icons.Default.Refresh, "Actualizar")
                }
            }
            
            Column(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.Center) {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(6.dp), shape = RoundedCornerShape(28.dp)) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Configuración de Unidad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
                                    val patente = bus.bus_patente ?: "Sin Patente"
                                    DropdownMenuItem(
                                        text = { Text(patente) },
                                        onClick = {
                                            selectedBusPatente = patente
                                            selectedBusId = bus.bus_id
                                            expanded = false
                                            sessionManager.guardarBus(bus.bus_id, patente)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { 
                        Log.d("PDA_DEBUG", "Click Iniciar Escáner")
                        if (selectedBusId != null && hasCamera && hasLocation) {
                            onIrAlScanner(selectedBusId!!)
                        } else {
                            val msg = when {
                                selectedBusId == null -> "Seleccione un bus en la configuración"
                                !hasCamera -> "Falta permiso de cámara"
                                !hasLocation -> "Falta permiso de ubicación"
                                else -> "Faltan datos para operar"
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(65.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, null)
                    Spacer(Modifier.width(12.dp))
                    Text("INICIAR ESCÁNER", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = {
                        Log.d("PDA_DEBUG", "Click Ver Historial")
                        onVerHistorial()
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.History, null)
                    Spacer(Modifier.width(12.dp))
                    Text("VER HISTORIAL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}