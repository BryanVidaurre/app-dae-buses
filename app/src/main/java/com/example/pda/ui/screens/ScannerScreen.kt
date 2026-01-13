package com.example.pda.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pda.database.AppDatabase
import com.example.pda.database.AsistenciaEntity
import com.example.pda.database.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScannerScreen(
    busId: Int,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current


    val sessionManager = remember { com.example.pda.database.SessionManager(context) }

    val realBusId = remember {
        if (busId > 0) busId else sessionManager.obtenerBusId()
    }
    val busPatente = remember { sessionManager.obtenerBusNombre() ?: "Sin Patente" }
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)

    var statusText by remember { mutableStateOf("Acerque el código QR a la pantalla") }
    var backgroundColor by remember { mutableStateOf(Color.Transparent) }
    var statusType by remember { mutableStateOf("waiting") } // waiting, success, error
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    var isProcessing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- HEADER ACTUALIZADO CON BOTÓN DE SALIDA ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Column {
                            Text(
                                text = "Escáner",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Patente: $busPatente",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Botón para cerrar sesión / cambiar bus
                    IconButton(
                        onClick = onVolver,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cambiar Bus",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Camera Preview Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(backgroundColor)
            ) {
                CameraPreview(
                    modifier = Modifier.size(1.dp),
                    onQrDetected = { qrCode ->
                        if (!isProcessing) {
                            isProcessing = true
                            scope.launch {
                                val alumno = db.estudianteDao().buscarPorToken(qrCode)

                                if (alumno != null) {
                                    val ahora = System.currentTimeMillis()
                                    val margenTiempo = ahora - (30 * 60 * 1000)

                                    val esDuplicado = db.asistenciaDao().existeRegistroReciente(
                                        estSemId = alumno.est_sem_id,
                                        haceDosHoras = margenTiempo
                                    ) > 0

                                    if (esDuplicado) {
                                        toneGen.startTone(ToneGenerator.TONE_CDMA_LOW_L, 300)
                                        statusText = alumno.pna_nom + ' '+alumno.pna_apat+' '+alumno.pna_amat
                                        statusType = "success"
                                        backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                                        delay(2500)
                                    } else {
                                        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                        val nuevaAsistencia = AsistenciaEntity(
                                            fecha_hora = ahora,
                                            latitud = 0.0,
                                            longitud = 0.0,
                                            est_sem_id = alumno.est_sem_id,
                                            bus_id = realBusId,
                                            qr_id = alumno.qr_id,
                                            pna_nom = alumno.pna_nom,
                                            pna_apat = alumno.pna_apat,
                                            pna_amat = alumno.pna_amat,
                                            sincronizado = false
                                        )

                                        db.asistenciaDao().insertarAsistencia(nuevaAsistencia)
                                        statusText = alumno.pna_nom + ' '+alumno.pna_apat+' '+alumno.pna_amat
                                        statusType = "success"
                                        backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                                        delay(2500)
                                    }
                                } else {
                                    toneGen.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 500)
                                    statusText = "Sin Registro"
                                    statusType = "error"
                                    backgroundColor = Color(0xFFF44336).copy(alpha = 0.6f)
                                    delay(2000)
                                }

                                backgroundColor = Color.Transparent
                                statusText = "Acerque el código QR a la pantalla"
                                statusType = "waiting"
                                isProcessing = false
                            }
                        }
                    }
                )

                // Overlay instrucciones
                if (statusType == "waiting") {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, null, Modifier.size(64.dp), MaterialTheme.colorScheme.primary)
                                Text("Listo para escanear", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Status Card at bottom
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (statusType) {
                        "success" -> Color(0xFFE8F5E9)
                        "error" -> Color(0xFFFFEBEE)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = if (statusType == "success") Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (statusType == "success") Color(0xFF2E7D32) else if (statusType == "error") Color(0xFFC62828) else Color.Gray
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (statusType == "success") "✅ AUTORIZADO" else if (statusType == "error") "❌ AVISO" else "ESPERANDO",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (statusType == "success") Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}