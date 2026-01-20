//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.database](../index.md)/[AsistenciaDao](index.md)

# AsistenciaDao

[androidJvm]\
interface [AsistenciaDao](index.md)

Interfaz de Acceso a Datos (DAO) para la tabla de asistencias.

Contiene los métodos necesarios para realizar operaciones CRUD y consultas especializadas sobre los registros de abordaje. Utiliza Coroutines para operaciones asíncronas y Flow para actualizaciones en tiempo real de la interfaz de usuario.

## Functions

| Name | Summary |
|---|---|
| [existeRegistroReciente](existe-registro-reciente.md) | [androidJvm]<br>abstract suspend fun [existeRegistroReciente](existe-registro-reciente.md)(estSemId: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), haceDosHoras: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-long/index.html)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Verifica la existencia de un registro previo para un estudiante en un margen de tiempo. |
| [insertarAsistencia](insertar-asistencia.md) | [androidJvm]<br>abstract suspend fun [insertarAsistencia](insertar-asistencia.md)(asistencia: [AsistenciaEntity](../-asistencia-entity/index.md))<br>Inserta una nueva asistencia en la base de datos. |
| [marcarSincronizado](marcar-sincronizado.md) | [androidJvm]<br>abstract suspend fun [marcarSincronizado](marcar-sincronizado.md)(id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html))<br>Actualiza el estado de un registro tras una sincronización exitosa. |
| [obtenerPendientesManual](obtener-pendientes-manual.md) | [androidJvm]<br>abstract suspend fun [obtenerPendientesManual](obtener-pendientes-manual.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AsistenciaEntity](../-asistencia-entity/index.md)&gt;<br>Consulta los registros que aún no han sido enviados al servidor. |
| [obtenerTodas](obtener-todas.md) | [androidJvm]<br>abstract fun [obtenerTodas](obtener-todas.md)(): Flow&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AsistenciaEntity](../-asistencia-entity/index.md)&gt;&gt;<br>Obtiene todos los registros de asistencia ordenados por fecha descendente. |
| [obtenerTodosDirecto](obtener-todos-directo.md) | [androidJvm]<br>abstract suspend fun [obtenerTodosDirecto](obtener-todos-directo.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AsistenciaEntity](../-asistencia-entity/index.md)&gt;<br>Obtiene una lista estática de todas las asistencias. |
