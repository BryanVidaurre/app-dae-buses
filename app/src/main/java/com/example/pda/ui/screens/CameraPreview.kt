package com.example.pda.ui.screens

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * Componente de vista previa de cámara con análisis de códigos QR integrado.
 *
 * Este Composable integra la API de [CameraX] dentro de la UI de Jetpack Compose mediante
 * un [AndroidView]. Utiliza [Google ML Kit] para procesar el flujo de video en tiempo real
 * y extraer la información de los códigos QR.
 *
 * @param modifier Modificador de diseño para ajustar el tamaño y posición de la cámara.
 * @param onQrDetected Callback que se dispara cada vez que el escáner identifica un código QR válido.
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onQrDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    /** Ejecutor de hilo único dedicado para no bloquear el hilo principal (UI) durante el análisis de imagen */
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    /** * Configuración del cliente de escaneo de Google ML Kit.
     * Se restringe el formato exclusivamente a [Barcode.FORMAT_QR_CODE] para optimizar la velocidad de lectura.
     */
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }

    // Integración de vista clásica de Android (PreviewView) en Jetpack Compose
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    ) { previewView ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            /** Configuración del UseCase de Preview: Permite al conductor ver lo que apunta la cámara */
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            /** * Configuración del UseCase de Análisis de Imagen.
             * Utiliza [ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST] para evitar retrasos,
             * descartando frames antiguos si el procesador está ocupado.
             */
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            // Definición del analizador que conecta CameraX con ML Kit
            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    // Convertir el frame de la cámara al formato InputImage de ML Kit
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                // Se extrae el contenido de texto del QR
                                barcode.rawValue?.let { qrCode ->
                                    Log.d("PDA_DEBUG", "🔍 QR Detectado por Cámara: $qrCode")
                                    onQrDetected(qrCode)
                                }
                            }
                        }
                        .addOnCompleteListener {
                            // IMPORTANTE: Liberar el imageProxy para poder recibir el siguiente frame
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }

            try {
                // Desvincular cualquier uso previo antes de re-vincular al ciclo de vida actual
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA, // Se utiliza la cámara trasera por defecto
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("PDA_DEBUG", "Error al iniciar cámara: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }
}