//[PDA UTA - Sistema de Control de Acceso](../../index.md)/[com.example.pda](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [MainActivity](-main-activity/index.md) | [androidJvm]<br>class [MainActivity](-main-activity/index.md) : [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html)<br>Actividad principal y punto de entrada de la aplicación PDA. |

## Functions

| Name | Summary |
|---|---|
| [MainScreen](-main-screen.md) | [androidJvm]<br>@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)<br>fun [MainScreen](-main-screen.md)(sessionManager: [SessionManager](../com.example.pda.database/-session-manager/index.md), onIrAlScanner: ([Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html), onVerHistorial: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))<br>Pantalla principal de configuración y bienvenida. |
| [OfflineStatusBadge](-offline-status-badge.md) | [androidJvm]<br>@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)<br>fun [OfflineStatusBadge](-offline-status-badge.md)(datosListos: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html))<br>Componente visual que muestra el estado de la base de datos local. |
