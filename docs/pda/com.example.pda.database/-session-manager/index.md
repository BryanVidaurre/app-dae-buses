//[pda](../../../index.md)/[com.example.pda.database](../index.md)/[SessionManager](index.md)

# SessionManager

class [SessionManager](index.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html))

Gestor de persistencia de sesión y caché ligera.

Esta clase utiliza [SharedPreferences](https://developer.android.com/reference/kotlin/android/content/SharedPreferences.html) para almacenar de forma persistente la configuración del terminal PDA. Su objetivo principal es gestionar la identidad del bus actual y mantener una copia local de la lista de buses para permitir el funcionamiento offline.

#### Parameters

androidJvm

| | |
|---|---|
| context | El contexto de la aplicación necesario para acceder a las preferencias. |

## Constructors

| | |
|---|---|
| [SessionManager](-session-manager.md) | [androidJvm]<br>constructor(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)) |

## Functions

| Name | Summary |
|---|---|
| [cerrarSesionBus](cerrar-sesion-bus.md) | [androidJvm]<br>fun [cerrarSesionBus](cerrar-sesion-bus.md)()<br>Elimina los datos del bus seleccionado de la sesión actual. |
| [estaBusSeleccionado](esta-bus-seleccionado.md) | [androidJvm]<br>fun [estaBusSeleccionado](esta-bus-seleccionado.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)<br>Valida si existe una sesión de bus activa. |
| [guardarBus](guardar-bus.md) | [androidJvm]<br>fun [guardarBus](guardar-bus.md)(id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), nombre: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html))<br>Persiste el ID y la patente del bus seleccionado en el almacenamiento local. |
| [guardarListaBuses](guardar-lista-buses.md) | [androidJvm]<br>fun [guardarListaBuses](guardar-lista-buses.md)(buses: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[BusResponse](../../com.example.pda.models/-bus-response/index.md)&gt;)<br>Guarda la lista completa de buses en formato JSON. |
| [obtenerBusId](obtener-bus-id.md) | [androidJvm]<br>fun [obtenerBusId](obtener-bus-id.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Recupera el ID del bus guardado actualmente. |
| [obtenerBusNombre](obtener-bus-nombre.md) | [androidJvm]<br>fun [obtenerBusNombre](obtener-bus-nombre.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)?<br>Recupera la patente o el nombre del bus configurado. |
| [obtenerListaBusesCache](obtener-lista-buses-cache.md) | [androidJvm]<br>fun [obtenerListaBusesCache](obtener-lista-buses-cache.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[BusResponse](../../com.example.pda.models/-bus-response/index.md)&gt;<br>Recupera la última lista de buses almacenada en caché. |
