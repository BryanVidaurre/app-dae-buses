//[pda](../../../index.md)/[com.example.pda.database](../index.md)/[AsistenciaDao](index.md)/[insertarAsistencia](insertar-asistencia.md)

# insertarAsistencia

[androidJvm]\
abstract suspend fun [insertarAsistencia](insertar-asistencia.md)(asistencia: [AsistenciaEntity](../-asistencia-entity/index.md))

Inserta una nueva asistencia en la base de datos.

- 
   Si ocurre un conflicto de ID (poco probable por el autogenerado), reemplaza el registro existente.
- 
   @param asistencia El objeto [AsistenciaEntity](../-asistencia-entity/index.md) a persistir.
