//[pda](../../../index.md)/[com.example.pda.database.dao](../index.md)/[EstudianteDao](index.md)/[insertarEstudiantes](insertar-estudiantes.md)

# insertarEstudiantes

[androidJvm]\
abstract suspend fun [insertarEstudiantes](insertar-estudiantes.md)(estudiantes: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[EstudianteEntity](../../com.example.pda.database.entities/-estudiante-entity/index.md)&gt;)

Inserta una lista completa de estudiantes en la base de datos local.

- 
   Se utiliza típicamente durante el proceso de sincronización inicial o actualización del padrón. Si un estudiante ya existe (basado en su clave primaria), será actualizado con la nueva información.

#### Parameters

androidJvm

| | |
|---|---|
| estudiantes | Lista de objetos [EstudianteEntity](../../com.example.pda.database.entities/-estudiante-entity/index.md) a persistir. |
