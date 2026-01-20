//[pda](../../index.md)/[com.example.pda.ui.screens](index.md)/[CameraPreview](-camera-preview.md)

# CameraPreview

[androidJvm]\

@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)

fun [CameraPreview](-camera-preview.md)(modifier: [Modifier](https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier.html) = Modifier, onQrDetected: ([String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))

Componente de vista previa de cámara con análisis de códigos QR integrado.

Este Composable integra la API de [CameraX](https://developer.android.com/reference/kotlin/androidx/camera/core/CameraX.html) dentro de la UI de Jetpack Compose mediante un [AndroidView](https://developer.android.com/reference/kotlin/androidx/compose/ui/viewinterop/package-summary.html). Utiliza Google ML Kit para procesar el flujo de video en tiempo real y extraer la información de los códigos QR.

#### Parameters

androidJvm

| | |
|---|---|
| modifier | Modificador de diseño para ajustar el tamaño y posición de la cámara. |
| onQrDetected | Callback que se dispara cada vez que el escáner identifica un código QR válido. |
