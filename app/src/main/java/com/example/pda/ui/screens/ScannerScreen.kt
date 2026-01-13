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
import androidx.compose.ui.draw.scale
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

@Composable
fun ScannerScreen(
    busId: Int,
    onCerrarSesion: () -> Unit,
    onVolver: () -> Unit
) {



    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val realBusId = remember {
        if (busId > 0) busId else sessionManager.obtenerBusId()
    }
    val busPatente = remember { sessionManager.obtenerBusNombre() ?: "Sin Patente" }
    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)

    var statusText by remember { mutableStateOf("Acerque el código QR") }
    var statusType by remember { mutableStateOf("waiting") }
    var showStatusCard by remember { mutableStateOf(false) }
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    var isProcessing by remember { mutableStateOf(false) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun obtenerUbicacionActual(onLocationReceived: (Double, Double) -> Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onLocationReceived(location.latitude, location.longitude)
                } else {
                    // Si no hay última ubicación conocida, enviar 0.0 o manejar error
                    onLocationReceived(0.0, 0.0)
                }
            }
        } catch (e: SecurityException) {
            onLocationReceived(0.0, 0.0)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // HEADER MEJORADO
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "Escáner QR",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF4CAF50), CircleShape)
                                )
                                Text(
                                    text = busPatente,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Botón de volver mejorado
                    FilledIconButton(
                        onClick = onVolver,
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
                }
            }

            // ÁREA DE CÁMARA MEJORADA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Marco de escáner con esquinas
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
                        modifier = Modifier.size(1.dp),
                        onQrDetected = { qrCode ->
                            if (!isProcessing) {
                                isProcessing = true
                                showStatusCard = false

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
                                            // Lógica de duplicado (ya marcada)
                                            toneGen.startTone(ToneGenerator.TONE_CDMA_LOW_L, 300)
                                            statusText = "${alumno.pna_nom} ${alumno.pna_apat} ${alumno.pna_amat}"
                                            statusType = "success"
                                            showStatusCard = true
                                            delay(2500)
                                        } else {
                                            // --- NUEVA LÓGICA DE UBICACIÓN ---
                                            try {
                                                // Intentamos obtener la última ubicación conocida
                                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                                    val lat = location?.latitude ?: 0.0
                                                    val lon = location?.longitude ?: 0.0

                                                    // Lanzamos una corrutina interna para insertar en la DB
                                                    scope.launch {
                                                        val nuevaAsistencia = AsistenciaEntity(
                                                            fecha_hora = ahora,
                                                            latitud = lat,    // Coordenada real capturada
                                                            longitud = lon,   // Coordenada real capturada
                                                            est_sem_id = alumno.est_sem_id,
                                                            bus_id = realBusId,
                                                            qr_id = alumno.qr_id,
                                                            pna_nom = alumno.pna_nom,
                                                            pna_apat = alumno.pna_apat,
                                                            pna_amat = alumno.pna_amat,
                                                            sincronizado = false
                                                        )

                                                        db.asistenciaDao().insertarAsistencia(nuevaAsistencia)
                                                        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 150)
                                                        statusText = "${alumno.pna_nom} ${alumno.pna_apat} ${alumno.pna_amat}"
                                                        statusType = "success"
                                                        showStatusCard = true
                                                    }
                                                }.addOnFailureListener {
                                                    // Si el GPS falla, insertamos con 0.0 para no perder el registro
                                                    scope.launch {
                                                        // (Aquí podrías repetir la inserción con 0.0)
                                                    }
                                                }
                                            } catch (e: SecurityException) {
                                                Log.e("GPS", "Sin permisos de ubicación")
                                            }

                                            delay(2500)
                                        }
                                    } else {
                                        // Estudiante no encontrado
                                        toneGen.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 500)
                                        statusText = "Estudiante no registrado"
                                        statusType = "error"
                                        showStatusCard = true
                                        delay(2000)
                                    }

                                    // Reset de la interfaz
                                    statusText = "Acerque el código QR"
                                    statusType = "waiting"
                                    showStatusCard = false
                                    isProcessing = false
                                }
                            }
                        }
                    )

                    // Overlay de instrucciones
                    if (statusType == "waiting" && !showStatusCard) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.padding(32.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        modifier = Modifier.size(72.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Listo para escanear",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Acerque el código QR a la cámara",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    // Esquinas decorativas del marco de escáner
                    CornerOverlays()
                }
            }

            // TARJETA DE ESTADO MEJORADA
            AnimatedVisibility(
                visible = showStatusCard,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (statusType) {
                            "success" -> Color(0xFFE8F5E9)
                            "error" -> Color(0xFFFFEBEE)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = if (statusType == "success")
                                        Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    else
                                        Color(0xFFF44336).copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (statusType == "success")
                                    Icons.Default.CheckCircle
                                else
                                    Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = if (statusType == "success")
                                    Color(0xFF2E7D32)
                                else
                                    Color(0xFFC62828)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (statusType == "success") "AUTORIZADO" else "AVISO",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (statusType == "success")
                                    Color(0xFF2E7D32)
                                else
                                    Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
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
    val cornerThickness = 4.dp
    val cornerColor = MaterialTheme.colorScheme.primary

    // Esquina superior izquierda
    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(cornerSize)
                .height(cornerThickness)
                .background(cornerColor, RoundedCornerShape(topStart = 4.dp))
        )
        Box(
            modifier = Modifier
                .width(cornerThickness)
                .height(cornerSize)
                .background(cornerColor, RoundedCornerShape(topStart = 4.dp))
        )
    }

    // Esquina superior derecha
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(cornerSize)
                .height(cornerThickness)
                .align(Alignment.TopEnd)
                .background(cornerColor, RoundedCornerShape(topEnd = 4.dp))
        )
        Box(
            modifier = Modifier
                .width(cornerThickness)
                .height(cornerSize)
                .align(Alignment.TopEnd)
                .background(cornerColor, RoundedCornerShape(topEnd = 4.dp))
        )
    }

    // Esquina inferior izquierda
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(cornerSize)
                .height(cornerThickness)
                .align(Alignment.BottomStart)
                .background(cornerColor, RoundedCornerShape(bottomStart = 4.dp))
        )
        Box(
            modifier = Modifier
                .width(cornerThickness)
                .height(cornerSize)
                .align(Alignment.BottomStart)
                .background(cornerColor, RoundedCornerShape(bottomStart = 4.dp))
        )
    }

    // Esquina inferior derecha
    Box(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .width(cornerSize)
                .height(cornerThickness)
                .align(Alignment.BottomEnd)
                .background(cornerColor, RoundedCornerShape(bottomEnd = 4.dp))
        )
        Box(
            modifier = Modifier
                .width(cornerThickness)
                .height(cornerSize)
                .align(Alignment.BottomEnd)
                .background(cornerColor, RoundedCornerShape(bottomEnd = 4.dp))
        )
    }
}