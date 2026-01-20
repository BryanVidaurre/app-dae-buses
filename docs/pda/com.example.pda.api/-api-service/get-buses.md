//[pda](../../../index.md)/[com.example.pda.api](../index.md)/[ApiService](index.md)/[getBuses](get-buses.md)

# getBuses

[androidJvm]\

@GET(value = &quot;bus&quot;)

abstract suspend fun [getBuses](get-buses.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[BusResponse](../../com.example.pda.models/-bus-response/index.md)&gt;

Obtiene la lista de todos los buses registrados en el sistema.

- 
   Se utiliza en la pantalla de configuración inicial para que el conductor seleccione su unidad.
- 
   @return Una lista de [BusResponse](../../com.example.pda.models/-bus-response/index.md) con la información de los buses.
