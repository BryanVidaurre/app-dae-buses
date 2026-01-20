//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.api](../index.md)/[ApiService](index.md)/[registrarIngresosBulk](registrar-ingresos-bulk.md)

# registrarIngresosBulk

[androidJvm]\

@POST(value = &quot;ingresos/bulk&quot;)

abstract suspend fun [registrarIngresosBulk](registrar-ingresos-bulk.md)(@Bodyingresos: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CreateIngresoBusDto](../../com.example.pda.models/-create-ingreso-bus-dto/index.md)&gt;): Response&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)&gt;

Sincroniza un lote de registros de asistencia acumulados localmente.

- 
   Es el método principal utilizado por el SyncWorker. Permite enviar hasta 50 registros en una sola petición HTTP, optimizando el uso de la batería y el consumo de datos móviles.
- 
   @param ingresos Lista de objetos [CreateIngresoBusDto](../../com.example.pda.models/-create-ingreso-bus-dto/index.md) pendientes de sincronización.

#### Return

Response que indica si el lote fue procesado correctamente por el servidor.
