package com.example.pda.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
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
import androidx.core.content.ContextCompat
import com.example.pda.database.AppDatabase
import com.example.pda.database.AsistenciaEntity
import com.example.pda.database.SessionManager
import com.example.pda.ui.utils.VoiceAssistant
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScannerScreen(
    busId: Int,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val voiceAssistant = remember { VoiceAssistant(context) }

    val realBusId = remember { if (busId > 0) busId else sessionManager.obtenerBusId() }
    val busPatente = remember { sessionManager.obtenerBusNombre() ?: "Sin Patente" }

    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)

    var statusText by remember { mutableStateOf("Acerque el código QR") }
    var statusType by remember { mutableStateOf("waiting") } 
    var showStatusCard by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    val toneGen = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    DisposableEffect(Unit) {
        onDispose { 
            voiceAssistant.stop()
            toneGen.release()
        }
    }

    suspend fun resetStatus(delayMs: Long = 2500) {
        delay(delayMs)
        statusType = "waiting"
        showStatusCard = false
        isProcessing = false
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface)))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- HEADER ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.QrCodeScanner, null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Column {
                            Text("Escáner Activo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(busPatente, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { 
                            onCerrarSesion()
                            onVolver() 
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar Sesión")
                        }
                        FilledIconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    }
                }
            }

            // --- ÁREA DE CÁMARA ---
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp)) {
                Box(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp))
                        .background(Color.Black.copy(alpha = 0.05f))
                        .border(3.dp, if (statusType == "success") Color(0xFF4CAF50) else if (statusType == "error") Color(0xFFF44336) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                ) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onQrDetected = { qrCode ->
                            if (!isProcessing) {
                                isProcessing = true
                                scope.launch {
                                    val alumno = db.estudianteDao().buscarPorToken(qrCode)
                                    if (alumno != null) {
                                        val estSemId = alumno.est_sem_id ?: 0
                                        val ahora = System.currentTimeMillis()
                                        val esDuplicado = db.asistenciaDao().existeRegistroReciente(estSemId, ahora - (30 * 60 * 1000)) > 0

                                        if (esDuplicado) {
                                            toneGen.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 200)
                                            voiceAssistant.speak("Registro duplicado")
                                            statusText = "${alumno.pna_nom ?: "Estudiante"} (Ya marcado)"
                                            statusType = "error"
                                            showStatusCard = true
                                            resetStatus()
                                        } else {
                                            val hasGps = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                            
                                            if (hasGps) {
                                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                    scope.launch {
                                                        db.asistenciaDao().insertarAsistencia(AsistenciaEntity(
                                                            fecha_hora = ahora,
                                                            latitud = location?.latitude ?: 0.0,
                                                            longitud = location?.longitude ?: 0.0,
                                                            est_sem_id = estSemId,
                                                            bus_id = realBusId,
                                                            qr_id = alumno.qr_id ?: 0,
                                                            pna_nom = alumno.pna_nom ?: "Estudiante",
                                                            pna_apat = alumno.pna_apat ?: "",
                                                            pna_amat = alumno.pna_amat ?: "",
                                                            sincronizado = false
                                                        ))
                                                        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                                        voiceAssistant.speak("Bienvenido ${alumno.pna_nom ?: ""}")
                                                        statusText = "${alumno.pna_nom ?: ""} ${alumno.pna_apat ?: ""}"
                                                        statusType = "success"
                                                        showStatusCard = true
                                                        resetStatus()
                                                    }
                                                }.addOnFailureListener { isProcessing = false }
                                            } else {
                                                db.asistenciaDao().insertarAsistencia(AsistenciaEntity(
                                                    fecha_hora = ahora,
                                                    est_sem_id = estSemId,
                                                    bus_id = realBusId,
                                                    qr_id = alumno.qr_id ?: 0,
                                                    pna_nom = alumno.pna_nom ?: "Estudiante",
                                                    pna_apat = alumno.pna_apat ?: "",
                                                    pna_amat = alumno.pna_amat ?: "",
                                                    sincronizado = false
                                                ))
                                                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                                statusType = "success"
                                                showStatusCard = true
                                                resetStatus()
                                            }
                                        }
                                    } else {
                                        toneGen.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 500)
                                        voiceAssistant.speak("No registrado")
                                        statusText = "Código no válido"
                                        statusType = "error"
                                        showStatusCard = true
                                        resetStatus()
                                    }
                                }
                            }
                        }
                    )
                    
                    if (statusType == "waiting" && !showStatusCard) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Acerque el código QR", 
                                color = Color.White, 
                                modifier = Modifier.background(Color.Black.copy(0.5f), RoundedCornerShape(8.dp)).padding(12.dp)
                            )
                        }
                    }
                    CornerOverlays()
                }
            }

            // --- DIÁLOGO DE ESTADO ---
            AnimatedVisibility(visible = showStatusCard, enter = slideInVertically { it } + fadeIn(), exit = slideOutVertically { it } + fadeOut()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (statusType == "success") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Icon(
                            imageVector = if (statusType == "success") Icons.Default.CheckCircle else Icons.Default.Error, 
                            contentDescription = null, 
                            modifier = Modifier.size(40.dp), 
                            tint = if (statusType == "success") Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Column {
                            Text(
                                text = if (statusType == "success") "AUTORIZADO" else "ERROR", 
                                style = MaterialTheme.typography.labelLarge, 
                                fontWeight = FontWeight.Bold, 
                                color = if (statusType == "success") Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Text(statusText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BoxScope.CornerOverlays() {
    val cornerSize = 40.dp
    val color = MaterialTheme.colorScheme.primary
    listOf(Alignment.TopStart, Alignment.TopEnd, Alignment.BottomStart, Alignment.BottomEnd).forEach { align ->
        Box(modifier = Modifier.align(align).padding(16.dp)) {
            Box(modifier = Modifier.width(cornerSize).height(4.dp).align(align).background(color, RoundedCornerShape(4.dp)))
            Box(modifier = Modifier.width(4.dp).height(cornerSize).align(align).background(color, RoundedCornerShape(4.dp)))
        }
    }
}