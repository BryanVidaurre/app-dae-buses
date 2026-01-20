//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.database](../index.md)/[AsistenciaDao](index.md)/[obtenerPendientesManual](obtener-pendientes-manual.md)

# obtenerPendientesManual

[androidJvm]\
abstract suspend fun [obtenerPendientesManual](obtener-pendientes-manual.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AsistenciaEntity](../-asistencia-entity/index.md)&gt;

Consulta los registros que aún no han sido enviados al servidor.

- 
   Este método es utilizado principalmente por el SyncWorker para identificar qué datos deben ser procesados en el siguiente lote de sincronización.
- 
   @return Lista de asistencias con el flag `sincronizado = 0`.
