package com.example.pda.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de Acceso a Datos (DAO) para la tabla de asistencias.
 *
 * Contiene los métodos necesarios para realizar operaciones CRUD y consultas
 * especializadas sobre los registros de abordaje. Utiliza Coroutines para operaciones
 * asíncronas y Flow para actualizaciones en tiempo real de la interfaz de usuario.
 */
@Dao
interface AsistenciaDao {

    /**
     * Obtiene todos los registros de asistencia ordenados por fecha descendente.
     * * Retorna un [Flow], lo que permite que la UI (como la pantalla de Historial) se
     * actualice automáticamente cada vez que hay un cambio en la tabla sin necesidad
     * de volver a consultar manualmente.
     * * @return Flujo reactivo con la lista de todas las asistencias.
     */
    @Query("SELECT * FROM asistencias ORDER BY fecha_hora DESC")
    fun obtenerTodas(): Flow<List<AsistenciaEntity>>

    /**
     * Obtiene una lista estática de todas las asistencias.
     * * A diferencia de [obtenerTodas], esta es una función de suspensión que devuelve
     * el estado actual de la base de datos una sola vez.
     * * @return Lista completa de [AsistenciaEntity].
     */
    @Query("SELECT * FROM asistencias")
    suspend fun obtenerTodosDirecto(): List<AsistenciaEntity>

    /**
     * Consulta los registros que aún no han sido enviados al servidor.
     * * Este método es utilizado principalmente por el [SyncWorker] para identificar
     * qué datos deben ser procesados en el siguiente lote de sincronización.
     * * @return Lista de asistencias con el flag `sincronizado = 0`.
     */
    @Query("SELECT * FROM asistencias WHERE sincronizado = 0")
    suspend fun obtenerPendientesManual(): List<AsistenciaEntity>

    /**
     * Inserta una nueva asistencia en la base de datos.
     * * Si ocurre un conflicto de ID (poco probable por el autogenerado), reemplaza el registro existente.
     * * @param asistencia El objeto [AsistenciaEntity] a persistir.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAsistencia(asistencia: AsistenciaEntity)

    /**
     * Actualiza el estado de un registro tras una sincronización exitosa.
     * * @param id El identificador único del ingreso ([AsistenciaEntity.ingreso_id]).
     */
    @Query("UPDATE asistencias SET sincronizado = 1 WHERE ingreso_id = :id")
    suspend fun marcarSincronizado(id: Int)

    /**
     * Verifica la existencia de un registro previo para un estudiante en un margen de tiempo.
     * * Esta consulta es vital para el control de duplicados. Permite saber si un alumno ya
     * marcó su ingreso recientemente, evitando registros redundantes si el QR se escanea dos veces.
     * * @param estSemId ID del estudiante a verificar.
     * @param haceDosHoras Timestamp que define el límite inferior del rango de búsqueda.
     * @return Cantidad de registros encontrados (0 si es el primer escaneo en el rango).
     */
    @Query("""
        SELECT COUNT(*) FROM asistencias 
        WHERE est_sem_id = :estSemId 
        AND sincronizado = 0 
        AND fecha_hora > :haceDosHoras
    """)
    suspend fun existeRegistroReciente(estSemId: Int, haceDosHoras: Long): Int
}