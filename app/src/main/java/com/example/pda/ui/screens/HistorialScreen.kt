package com.example.pda.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pda.database.AppDatabase
import com.example.pda.database.AsistenciaEntity // <-- ¡IMPORTANTE!
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(db: AppDatabase, onBack: () -> Unit) {
    // CollectAsState con Flow hace que si escaneas a alguien, aparezca aquí mágicamente
    val asistencias by db.asistenciaDao().obtenerTodas().collectAsState(initial = emptyList())
    val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pasajeros a bordo (${asistencias.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (asistencias.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay alumnos registrados aún", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(asistencias) { registro ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(registro.pna_nom+' '+registro.pna_apat+' '+registro.pna_amat, style = MaterialTheme.typography.titleMedium)
                                Text("Escaneado a las: ${format.format(Date(registro.fecha_hora))}",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        // Indicador de Sincronización
                        Column(horizontalAlignment = Alignment.End) {
                            val iconColor = if (registro.sincronizado) Color(0xFF4CAF50) else Color.Gray
                            val statusText = if (registro.sincronizado) "Subido" else "Pendiente"

                            Icon(
                                imageVector = if (registro.sincronizado) Icons.Default.CloudDone else Icons.Default.Storage,
                                contentDescription = null,
                                tint = iconColor
                            )
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.labelSmall,
                                color = iconColor
                            )
                        }
                    }
                }
            }
        }
    }
}