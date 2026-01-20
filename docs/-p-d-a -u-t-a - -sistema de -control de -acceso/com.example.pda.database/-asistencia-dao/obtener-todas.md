//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.database](../index.md)/[AsistenciaDao](index.md)/[obtenerTodas](obtener-todas.md)

# obtenerTodas

[androidJvm]\
abstract fun [obtenerTodas](obtener-todas.md)(): Flow&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AsistenciaEntity](../-asistencia-entity/index.md)&gt;&gt;

Obtiene todos los registros de asistencia ordenados por fecha descendente.

- 
   Retorna un Flow, lo que permite que la UI (como la pantalla de Historial) se actualice automáticamente cada vez que hay un cambio en la tabla sin necesidad de volver a consultar manualmente.
- 
   @return Flujo reactivo con la lista de todas las asistencias.
