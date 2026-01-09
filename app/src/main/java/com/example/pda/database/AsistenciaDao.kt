package com.example.pda.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AsistenciaDao {
    @Insert
    suspend fun insertarAsistencia(asistencia: AsistenciaEntity)

    @Query("SELECT * FROM asistencias ORDER BY fecha_hora DESC")
    suspend fun obtenerTodas(): List<AsistenciaEntity>

    @Query("SELECT * FROM asistencias WHERE sincronizado = 0")
    suspend fun obtenerPendientes(): List<AsistenciaEntity>
}