//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.database](../index.md)/[AsistenciaDao](index.md)/[obtenerTodosDirecto](obtener-todos-directo.md)

# obtenerTodosDirecto

[androidJvm]\
abstract suspend fun [obtenerTodosDirecto](obtener-todos-directo.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AsistenciaEntity](../-asistencia-entity/index.md)&gt;

Obtiene una lista estática de todas las asistencias.

- 
   A diferencia de [obtenerTodas](obtener-todas.md), esta es una función de suspensión que devuelve el estado actual de la base de datos una sola vez.
- 
   @return Lista completa de [AsistenciaEntity](../-asistencia-entity/index.md).
