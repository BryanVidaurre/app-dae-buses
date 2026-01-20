//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.database](../index.md)/[SessionManager](index.md)/[guardarListaBuses](guardar-lista-buses.md)

# guardarListaBuses

[androidJvm]\
fun [guardarListaBuses](guardar-lista-buses.md)(buses: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[BusResponse](../../com.example.pda.models/-bus-response/index.md)&gt;)

Guarda la lista completa de buses en formato JSON.

- 
   Esto permite que la pantalla de selección de bus muestre opciones incluso si no hay conexión a internet en el momento de abrir la app.
- 
   @param buses Lista de objetos [BusResponse](../../com.example.pda.models/-bus-response/index.md) obtenidos de la API.
