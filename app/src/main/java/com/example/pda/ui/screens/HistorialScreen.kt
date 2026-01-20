package com.example.pda.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pda.database.AppDatabase
import com.example.pda.database.AsistenciaEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de Historial de Pasajeros.
 * * Esta interfaz permite al conductor visualizar de manera detallada todos los registros
 * de abordaje capturados por el dispositivo. Su función principal es proporcionar
 * transparencia sobre qué datos han sido enviados al servidor y cuáles permanecen
 * almacenados únicamente de forma local.
 * * @param db Instancia de la base de datos [AppDatabase] para consultar las asistencias.
 * @param onBack Callback para navegar hacia la pantalla anterior.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistorialScreen(db: AppDatabase, onBack: () -> Unit) {
    /** * Observa la lista de asistencias desde la base de datos Room.
     * Al usar [collectAsState], la UI se actualiza automáticamente si un registro se sincroniza en segundo plano.
     */
    val asistencias by db.asistenciaDao().obtenerTodas().collectAsState(initial = emptyList())

    /** Formateador de hora para mostrar la precisión del escaneo en formato HH:mm:ss */
    val format = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    // Cálculo de métricas para el encabezado
    val sincronizados = asistencias.count { it.sincronizado }
    val pendientes = asistencias.size - sincronizados

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
        Column(modifier = Modifier.fillMaxSize()) {
            // SECCIÓN SUPERIOR: Encabezado y Estadísticas
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Barra de navegación superior
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FilledIconButton(
                            onClick = onBack,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pasajeros",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${asistencias.size} registros totales",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Fila de Tarjetas de Estado (Sincronizados vs Pendientes)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Métrica: Registros ya subidos a la API central
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).background(Color(0xFF4CAF50).copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudDone, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(24.dp))
                                }
                                Column {
                                    Text(text = sincronizados.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    Text(text = "Subidos", style = MaterialTheme.typography.bodySmall, color = Color(0xFF2E7D32).copy(alpha = 0.7f))
                                }
                            }
                        }

                        // Métrica: Registros guardados solo en el teléfono (esperando internet)
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Storage, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                                }
                                Column {
                                    Text(text = pendientes.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    Text(text = "Pendientes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }

            // SECCIÓN INFERIOR: Listado dinámico de registros
            if (asistencias.isEmpty()) {
                // Estado vacío: Se muestra cuando no hay datos en la tabla de asistencias
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                        Text("No hay pasajeros registrados", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("Los escaneos aparecerán aquí", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    }
                }
            } else {
                // Lista de tarjetas: Representa cada registro individual de entrada
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(asistencias, key = { it.ingreso_id }) { registro ->
                        Card(
                            modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Avatar genérico para el estudiante
                                Box(
                                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }

                                // Información de identidad y tiempo
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "${registro.pna_nom} ${registro.pna_apat} ${registro.pna_amat}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        Text(
                                            text = format.format(Date(registro.fecha_hora)),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                // Indicador de Sincronización (Nube verde si subió, Disco gris si es local)
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(
                                        modifier = Modifier.size(40.dp).background(
                                            color = if (registro.sincronizado) Color(0xFF4CAF50).copy(alpha = 0.15f)
                                            else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CircleShape
                                        ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (registro.sincronizado) Icons.Default.CloudDone else Icons.Default.Storage,
                                            contentDescription = null,
                                            tint = if (registro.sincronizado) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Text(
                                        text = if (registro.sincronizado) "Subido" else "Local",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (registro.sincronizado) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}