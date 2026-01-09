package com.example.pda.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pda.database.AppDatabase
import kotlinx.coroutines.launch
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.pda.database.AsistenciaEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScannerScreen(busId: Int) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)

    var statusText by remember { mutableStateOf("Apunte al código QR del alumno") }
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
            // Header Card
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
                        .padding(20.dp),
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
                            text = "Escáner de Acceso",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Bus ID: $busId",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
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
                                Log.d("PDA_DEBUG", "Procesando token único: $qrCode")
                                val alumno = db.estudianteDao().buscarPorToken(qrCode)

                                if (alumno != null) {
                                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                    val nuevaAsistencia = AsistenciaEntity(
                                        estudianteToken = qrCode,
                                        pna_nom = alumno.pna_nom,
                                        fecha_hora = System.currentTimeMillis(),
                                        busId = busId,
                                        sincronizado = false
                                    )
                                    db.asistenciaDao().insertarAsistencia(nuevaAsistencia)
                                    Log.d("PDA_DEBUG", "Asistencia guardada para: ${alumno.pna_nom}")
                                    statusText = "${alumno.pna_nom}"
                                    statusType = "success"
                                    backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.2f)

                                    delay(2500)
                                } else {
                                    toneGen.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 500)

                                    statusText = "Sin Registro"
                                    statusType = "error"

                                    backgroundColor = Color(0xFFF44336).copy(alpha = 0.6f)

                                    Log.e("PDA_DEBUG", "QR leído pero no existe en la DB local: $qrCode")

                                    delay(2000)
                                }

                                backgroundColor = Color.Transparent
                                statusText = "Apunte al código QR del alumno"
                                statusType = "waiting"
                                isProcessing = false
                            }
                        }
                    }
                )

                // Overlay con instrucciones cuando está esperando
                if (statusType == "waiting") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Listo para escanear",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Apunte la cámara al código QR del alumno",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Status Card at bottom
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (statusType) {
                        "success" -> MaterialTheme.colorScheme.primaryContainer
                        "error" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (statusType) {
                        "success" -> Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF2E7D32)
                        )
                        "error" -> Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFFC62828)
                        )
                        else -> Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (statusType) {
                                "success" -> "✅ AUTORIZADO"
                                "error" -> "❌ NO AUTORIZADO"
                                else -> "ESPERANDO"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = when (statusType) {
                                "success" -> Color(0xFF2E7D32)
                                "error" -> Color(0xFFC62828)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = when (statusType) {
                                "success" -> MaterialTheme.colorScheme.onPrimaryContainer
                                "error" -> MaterialTheme.colorScheme.onErrorContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}