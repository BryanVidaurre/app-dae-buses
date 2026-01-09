package com.example.pda
import androidx.compose.runtime.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.* // Esto arregla remember y mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pda.ui.theme.PDATheme

//verificar commit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PDATheme {
                // Surface proporciona el color de fondo estándar del tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    // Estados para la UI
    var selectedBus by remember { mutableStateOf("Seleccione un bus") }
    var expanded by remember { mutableStateOf(false) }

    // Estado para la lista que viene del backend
    var busList by remember { mutableStateOf<List<BusResponse>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Este bloque ejecuta la petición al backend al iniciar la pantalla
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.instance.getBuses()
            // Filtramos los que no estén eliminados si el backend no lo hace
            busList = response.filter { !it.deleted }
        } catch (e: Exception) {
            errorMessage = "Error al conectar con el servidor"
        }
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text(text = "Control de Acceso", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        }

        // Selector de Bus (Dropdown)
        Box {
            OutlinedTextField(
                value = selectedBus,
                onValueChange = { },
                readOnly = true,
                label = { Text("Patente del Bus") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.DirectionsBus, contentDescription = null)
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
                            selectedBus = bus.bus_patente
                            expanded = false
                        }
                    )
                }
            }

            // Capa invisible para detectar click en todo el campo
            Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
        }
    }
}