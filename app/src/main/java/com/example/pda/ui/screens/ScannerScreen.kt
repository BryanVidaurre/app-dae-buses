package com.example.pda.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pda.database.AppDatabase
import com.example.pda.database.AsistenciaEntity
import com.example.pda.database.SessionManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.pda.ui.utils.VoiceAssistant

/**
 * Pantalla de Escaneo de Control de Acceso Estudiantil.
 *
 * Esta pantalla constituye el núcleo operativo del sistema. Orquesta la interacción entre:
 * 1. **Visor de Cámara**: Integración de CameraX para detección de QR.
 * 2. **Geolocalización**: Captura automática de coordenadas GPS en el momento del escaneo.
 * 3. **Validación Offline**: Consulta inmediata a la base de datos local (Room) para verificar al alumno.
 * 4. **Feedback Multimodal**: Emisión de beeps, mensajes de voz (TTS) y alertas visuales dinámicas.
 * 5. **Control de Flujo**: Sistema que evita registros duplicados en un margen de tiempo definido.
 *
 * @param busId Identificador del bus para el registro de asistencias.
 * @param onCerrarSesion Función para limpiar los datos de sesión del bus.
 * @param onVolver Función para retornar a la pantalla de selección de configuración.
 */
@Composable
fun ScannerScreen(
    busId: Int,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val voiceAssistant = remember { VoiceAssistant(context) }

    /** Determina el ID del bus basándose en el parámetro o en la sesión guardada */
    val realBusId = remember { if (busId > 0) busId else sessionManager.obtenerBusId() }
    val busPatente = remember { sessionManager.obtenerBusNombre() ?: "Sin Patente" }

    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)

    // Estados de control de la interfaz y procesamiento
    var statusText by remember { mutableStateOf("Acerque el código QR") }
    var statusType by remember { mutableStateOf("waiting") } // success, error, waiting
    var showStatusCard by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    /** Generador de tonos para feedback auditivo inmediato */
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70) }

    /** Cliente de ubicación de Google Play Services */
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Limpieza del motor de voz al destruir el componente
    DisposableEffect(Unit) {
        onDispose { voiceAssistant.stop() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- HEADER DE INFORMACIÓN ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(
                            modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Column {
                            Text("Escáner QR", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                                Text(busPatente, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    FilledIconButton(
                        onClick = onVolver,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            }

            // --- ÁREA DE CÁMARA Y PROCESAMIENTO ---
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            when (statusType) {
                                "success" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                "error" -> Color(0xFFF44336).copy(alpha = 0.15f)
                                else -> Color.Black.copy(alpha = 0.05f)
                            }
                        )
                        .border(
                            width = 3.dp,
                            color = when (statusType) {
                                "success" -> Color(0xFF4CAF50)
                                "error" -> Color(0xFFF44336)
                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            },
                            shape = RoundedCornerShape(32.dp)
                        )
                ) {
                    CameraPreview(
                        modifier = Modifier.size(1.dp), // Activación silenciosa
                        onQrDetected = { qrCode ->
                            /** Lógica de detección: Bloquea nuevos escaneos mientras uno está en curso */
                            if (!isProcessing) {
                                isProcessing = true
                                showStatusCard = false

                                scope.launch {
                                    val alumno = db.estudianteDao().buscarPorToken(qrCode)

                                    if (alumno != null) {
                                        val ahora = System.currentTimeMillis()
                                        // Margen de 30 minutos para evitar doble registro por error
                                        val margenTiempo = ahora - (30 * 60 * 1000)

                                        val esDuplicado = db.asistenciaDao().existeRegistroReciente(
                                            estSemId = alumno.est_sem_id,
                                            haceDosHoras = margenTiempo
                                        ) > 0

                                        if (esDuplicado) {
                                            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                            scope.launch { delay(200); voiceAssistant.speak("Registro exitoso") }
                                            statusText = "${alumno.pna_nom} ${alumno.pna_apat} ${alumno.pna_amat}"
                                            statusType = "success"
                                            showStatusCard = true
                                            delay(2500)
                                        } else {
                                            // Captura de GPS y persistencia local
                                            try {
                                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                    val lat = location?.latitude ?: 0.0
                                                    val lon = location?.longitude ?: 0.0

                                                    scope.launch {
                                                        db.asistenciaDao().insertarAsistencia(AsistenciaEntity(
                                                            fecha_hora = ahora,
                                                            latitud = lat,
                                                            longitud = lon,
                                                            est_sem_id = alumno.est_sem_id,
                                                            bus_id = realBusId,
                                                            qr_id = alumno.qr_id,
                                                            pna_nom = alumno.pna_nom,
                                                            pna_apat = alumno.pna_apat,
                                                            pna_amat = alumno.pna_amat,
                                                            sincronizado = false
                                                        ))
                                                        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                                        scope.launch { delay(200); voiceAssistant.speak("Registro exitoso") }
                                                        statusText = "${alumno.pna_nom} ${alumno.pna_apat} ${alumno.pna_amat}"
                                                        statusType = "success"
                                                        showStatusCard = true
                                                    }
                                                }.addOnFailureListener { isProcessing = false }
                                            } catch (e: SecurityException) { Log.e("GPS", "Error de permisos") }
                                            delay(2500)
                                        }
                                    } else {
                                        // Caso: El token no existe en la base de datos descargada
                                        toneGen.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 500)
                                        scope.launch { delay(200); voiceAssistant.speak("Registro fallido") }
                                        statusText = "Estudiante no registrado"
                                        statusType = "error"
                                        showStatusCard = true
                                        delay(2000)
                                    }

                                    // Reset para el próximo alumno
                                    statusType = "waiting"
                                    showStatusCard = false
                                    isProcessing = false
                                }
                            }
                        }
                    )

                    // Overlay instructivo inicial
                    if (statusType == "waiting" && !showStatusCard) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Card(
                                modifier = Modifier.padding(32.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(12.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text("Listo para escanear", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Text("Acerque el código QR a la cámara", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                    CornerOverlays()
                }
            }

            // --- TARJETA DE RESULTADO DINÁMICA ---
            AnimatedVisibility(
                visible = showStatusCard,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (statusType == "success") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Box(
                            modifier = Modifier.size(64.dp).background(
                                color = if (statusType == "success") Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFF44336).copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (statusType == "success") Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = if (statusType == "success") Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (statusType == "success") "AUTORIZADO" else "AVISO",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (statusType == "success") Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(statusText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Dibuja las esquinas decorativas que simulan un visor de escáner sobre el área de la cámara.
 */
@Composable
fun BoxScope.CornerOverlays() {
    val cornerSize = 40.dp
    val cornerThickness = 4.dp
    val cornerColor = MaterialTheme.colorScheme.primary

    listOf(Alignment.TopStart, Alignment.TopEnd, Alignment.BottomStart, Alignment.BottomEnd).forEach { align ->
        Box(modifier = Modifier.align(align).padding(16.dp)) {
            // Línea horizontal de la esquina
            Box(modifier = Modifier
                .width(cornerSize).height(cornerThickness)
                .align(align).background(cornerColor, RoundedCornerShape(4.dp)))
            // Línea vertical de la esquina
            Box(modifier = Modifier
                .width(cornerThickness).height(cornerSize)
                .align(align).background(cornerColor, RoundedCornerShape(4.dp)))
        }
    }
}