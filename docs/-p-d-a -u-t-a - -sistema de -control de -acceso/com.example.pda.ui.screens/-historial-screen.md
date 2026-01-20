//[PDA UTA - Sistema de Control de Acceso](../../index.md)/[com.example.pda.ui.screens](index.md)/[HistorialScreen](-historial-screen.md)

# HistorialScreen

[androidJvm]\

@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)

fun [HistorialScreen](-historial-screen.md)(db: [AppDatabase](../com.example.pda.database/-app-database/index.md), onBack: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))

Pantalla de Historial de Pasajeros.

- 
   Esta interfaz permite al conductor visualizar de manera detallada todos los registros de abordaje capturados por el dispositivo. Su función principal es proporcionar transparencia sobre qué datos han sido enviados al servidor y cuáles permanecen almacenados únicamente de forma local.
- 
   @param db Instancia de la base de datos [AppDatabase](../com.example.pda.database/-app-database/index.md) para consultar las asistencias.

#### Parameters

androidJvm

| | |
|---|---|
| onBack | Callback para navegar hacia la pantalla anterior. |
