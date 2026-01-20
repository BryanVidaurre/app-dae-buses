//[PDA UTA - Sistema de Control de Acceso](../../index.md)/[com.example.pda.ui.screens](index.md)/[ScannerScreen](-scanner-screen.md)

# ScannerScreen

[androidJvm]\

@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)

fun [ScannerScreen](-scanner-screen.md)(busId: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), onCerrarSesion: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html), onVolver: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))

Pantalla de Escaneo de Control de Acceso Estudiantil.

Esta pantalla constituye el núcleo operativo del sistema. Orquesta la interacción entre:

1. 
   **Visor de Cámara**: Integración de CameraX para detección de QR.
2. 
   **Geolocalización**: Captura automática de coordenadas GPS en el momento del escaneo.
3. 
   **Validación Offline**: Consulta inmediata a la base de datos local (Room) para verificar al alumno.
4. 
   **Feedback Multimodal**: Emisión de beeps, mensajes de voz (TTS) y alertas visuales dinámicas.
5. 
   **Control de Flujo**: Sistema que evita registros duplicados en un margen de tiempo definido.

#### Parameters

androidJvm

| | |
|---|---|
| busId | Identificador del bus para el registro de asistencias. |
| onCerrarSesion | Función para limpiar los datos de sesión del bus. |
| onVolver | Función para retornar a la pantalla de selección de configuración. |
