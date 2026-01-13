package com.example.pda.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface AsistenciaDao {
    @Query("SELECT * FROM asistencias ORDER BY fecha_hora DESC")
    fun obtenerTodas(): Flow<List<AsistenciaEntity>> // Sin 'suspend'
    @Query("SELECT * FROM asistencias")
    suspend fun obtenerTodosDirecto(): List<AsistenciaEntity>

    @Query("SELECT * FROM asistencias WHERE sincronizado = 0")
    suspend fun obtenerPendientesManual(): List<AsistenciaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAsistencia(asistencia: AsistenciaEntity)

    @Query("UPDATE asistencias SET sincronizado = 1 WHERE ingreso_id = :id")
    suspend fun marcarSincronizado(id: Int)

    @Query("""
    SELECT COUNT(*) FROM asistencias 
    WHERE est_sem_id = :estSemId 
    AND sincronizado = 0 
    AND fecha_hora > :haceDosHoras
""")
    suspend fun existeRegistroReciente(estSemId: Int, haceDosHoras: Long): Int
}