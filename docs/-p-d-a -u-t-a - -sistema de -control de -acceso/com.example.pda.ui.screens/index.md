//[PDA UTA - Sistema de Control de Acceso](../../index.md)/[com.example.pda.ui.screens](index.md)

# Package-level declarations

## Functions

| Name | Summary |
|---|---|
| [CameraPreview](-camera-preview.md) | [androidJvm]<br>@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)<br>fun [CameraPreview](-camera-preview.md)(modifier: [Modifier](https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier.html) = Modifier, onQrDetected: ([String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))<br>Componente de vista previa de cámara con análisis de códigos QR integrado. |
| [CornerOverlays](-corner-overlays.md) | [androidJvm]<br>@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)<br>fun [BoxScope](https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/BoxScope.html).[CornerOverlays](-corner-overlays.md)()<br>Dibuja las esquinas decorativas que simulan un visor de escáner sobre el área de la cámara. |
| [HistorialScreen](-historial-screen.md) | [androidJvm]<br>@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)<br>fun [HistorialScreen](-historial-screen.md)(db: [AppDatabase](../com.example.pda.database/-app-database/index.md), onBack: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))<br>Pantalla de Historial de Pasajeros. |
| [ScannerScreen](-scanner-screen.md) | [androidJvm]<br>@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)<br>fun [ScannerScreen](-scanner-screen.md)(busId: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), onCerrarSesion: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html), onVolver: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))<br>Pantalla de Escaneo de Control de Acceso Estudiantil. |
