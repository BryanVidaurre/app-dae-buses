//[pda](../../../index.md)/[com.example.pda.api](../index.md)/[ApiService](index.md)

# ApiService

[androidJvm]\
interface [ApiService](index.md)

Interfaz que define los endpoints de la API REST para el sistema PDA.

Esta interfaz es utilizada por Retrofit para realizar las peticiones de red. Todas las funciones son de tipo `suspend`, lo que garantiza que las llamadas a la red se realicen de forma no bloqueante dentro de corrutinas.

## Functions

| Name | Summary |
|---|---|
| [getBuses](get-buses.md) | [androidJvm]<br>@GET(value = &quot;bus&quot;)<br>abstract suspend fun [getBuses](get-buses.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[BusResponse](../../com.example.pda.models/-bus-response/index.md)&gt;<br>Obtiene la lista de todos los buses registrados en el sistema. |
| [getEstudiantesAutorizados](get-estudiantes-autorizados.md) | [androidJvm]<br>@GET(value = &quot;ingresos/autorizados&quot;)<br>abstract suspend fun [getEstudiantesAutorizados](get-estudiantes-autorizados.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[EstudianteAutorizado](../../com.example.pda.models/-estudiante-autorizado/index.md)&gt;<br>Descarga el padrón completo de estudiantes autorizados para el semestre vigente. |
| [registrarIngreso](registrar-ingreso.md) | [androidJvm]<br>@POST(value = &quot;ingresos/registrar&quot;)<br>abstract suspend fun [registrarIngreso](registrar-ingreso.md)(@Bodyingreso: [CreateIngresoBusDto](../../com.example.pda.models/-create-ingreso-bus-dto/index.md)): Response&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)&gt;<br>Registra un único ingreso de pasajero en tiempo real. |
| [registrarIngresosBulk](registrar-ingresos-bulk.md) | [androidJvm]<br>@POST(value = &quot;ingresos/bulk&quot;)<br>abstract suspend fun [registrarIngresosBulk](registrar-ingresos-bulk.md)(@Bodyingresos: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CreateIngresoBusDto](../../com.example.pda.models/-create-ingreso-bus-dto/index.md)&gt;): Response&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)&gt;<br>Sincroniza un lote de registros de asistencia acumulados localmente. |
