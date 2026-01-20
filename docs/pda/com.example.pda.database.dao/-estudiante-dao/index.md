//[pda](../../../index.md)/[com.example.pda.database.dao](../index.md)/[EstudianteDao](index.md)

# EstudianteDao

[androidJvm]\
interface [EstudianteDao](index.md)

Interfaz de Acceso a Datos (DAO) para la gestión de estudiantes autorizados.

Esta interfaz define las operaciones necesarias para mantener actualizada la lista local de alumnos. Su función principal es permitir la búsqueda instantánea por token de QR sin requerir conexión a internet durante el proceso de abordaje.

## Functions

| Name | Summary |
|---|---|
| [borrarTodo](borrar-todo.md) | [androidJvm]<br>abstract suspend fun [borrarTodo](borrar-todo.md)()<br>Elimina todos los registros de la tabla de estudiantes. |
| [buscarPorToken](buscar-por-token.md) | [androidJvm]<br>abstract suspend fun [buscarPorToken](buscar-por-token.md)(tokenInput: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): [EstudianteEntity](../../com.example.pda.database.entities/-estudiante-entity/index.md)?<br>Busca a un estudiante en la base de datos local utilizando el contenido del código QR. |
| [insertarEstudiantes](insertar-estudiantes.md) | [androidJvm]<br>abstract suspend fun [insertarEstudiantes](insertar-estudiantes.md)(estudiantes: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[EstudianteEntity](../../com.example.pda.database.entities/-estudiante-entity/index.md)&gt;)<br>Inserta una lista completa de estudiantes en la base de datos local. |
| [obtenerTodosDirecto](obtener-todos-directo.md) | [androidJvm]<br>abstract suspend fun [obtenerTodosDirecto](obtener-todos-directo.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[EstudianteEntity](../../com.example.pda.database.entities/-estudiante-entity/index.md)&gt;<br>Recupera la lista completa de estudiantes almacenados en el dispositivo. |
