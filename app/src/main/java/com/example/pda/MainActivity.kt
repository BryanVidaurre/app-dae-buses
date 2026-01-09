package com.example.pda

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
import com.example.pda.api.RetrofitClient
import com.example.pda.database.AppDatabase
import com.example.pda.database.entities.EstudianteEntity
import com.example.pda.models.BusResponse
import com.example.pda.ui.screens.ScannerScreen
import com.example.pda.ui.theme.PDATheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDATheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "seleccion") {
                    composable("seleccion") {
                        MainScreen(onIrAlScanner = { busId ->
                            navController.navigate("scanner/$busId")
                        })
                    }
                    composable("scanner/{busId}") { backStackEntry ->
                        val busId = backStackEntry.arguments?.getString("busId")?.toInt() ?: 0
                        ScannerScreen(busId = busId)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onIrAlScanner: (Int) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)

    var selectedBusPatente by remember { mutableStateOf("Seleccione un bus") }
    var selectedBusId by remember { mutableStateOf<Int?>(null) } // Guardamos el ID del bus
    var busList by remember { mutableStateOf<List<BusResponse>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var estaSincronizando by remember { mutableStateOf(false) }
    var datosListos by remember { mutableStateOf(false) } // Para habilitar el botón

    LaunchedEffect(Unit) {
        try {
            busList = RetrofitClient.instance.getBuses().filter { !it.deleted }
        } catch (e: Exception) {
            Toast.makeText(context, "Error de red: Revise IP y Firewall", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("PDA - Control de Acceso", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))

            if (estaSincronizando) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            // Selector de Bus
            Box {
                OutlinedTextField(
                    value = selectedBusPatente,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Patente del Bus") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.DirectionsBus, null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { expanded = true })

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
                                        Log.d("PDA_DEBUG", "API respondió con ${alumnos.size} alumnos.")
                                        db.estudianteDao().borrarTodo()
                                        db.estudianteDao().insertarEstudiantes(alumnos.map {
                                            EstudianteEntity(it.per_id, it.pna_nom, it.token, it.est_sem_id, it.qr_id)
                                        })
                                        datosListos = true // Ya hay datos en Room
                                        Toast.makeText(context, "Sincronizado Offline", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Log.e("PDA_DEBUG", "Error: ${e.message}")
                                        Toast.makeText(context, "Error al sincronizar", Toast.LENGTH_SHORT).show()
                                    } finally { estaSincronizando = false }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // BOTÓN QUE FALTABA PARA INICIAR EL ESCÁNER
            Button(
                onClick = { selectedBusId?.let { onIrAlScanner(it) } },
                enabled = datosListos, // Solo si ya se descargaron los alumnos
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("INICIAR ESCÁNER QR")
            }
        }
    }
}