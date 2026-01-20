//[pda](../../index.md)/[com.example.pda](index.md)/[MainScreen](-main-screen.md)

# MainScreen

[androidJvm]\

@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)

fun [MainScreen](-main-screen.md)(sessionManager: [SessionManager](../com.example.pda.database/-session-manager/index.md), onIrAlScanner: ([Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html), onVerHistorial: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html))

Pantalla principal de configuración y bienvenida.

Permite al usuario:

- 
   Sincronizar manualmente los datos del servidor (Buses y Estudiantes).
- 
   Seleccionar la patente del bus que se está operando.
- 
   Verificar el cumplimiento de permisos y disponibilidad de datos locales.

#### Parameters

androidJvm

| | |
|---|---|
| sessionManager | Gestor de persistencia para la sesión del bus. |
| onIrAlScanner | Callback para navegar hacia el escáner con el busId seleccionado. |
| onVerHistorial | Callback para navegar a la pantalla de historial. |
