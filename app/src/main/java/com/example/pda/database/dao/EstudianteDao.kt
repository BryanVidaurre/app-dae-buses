package com.example.pda.database.dao

import androidx.room.*
import com.example.pda.database.entities.EstudianteEntity

@Dao
interface EstudianteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEstudiantes(estudiantes: List<EstudianteEntity>)

    @Query("SELECT * FROM estudiantes_autorizados WHERE token = :tokenInput LIMIT 1")
    suspend fun buscarPorToken(tokenInput: String): EstudianteEntity?

    @Query("DELETE FROM estudiantes_autorizados")
    suspend fun borrarTodo()

    @Query("SELECT * FROM estudiantes_autorizados")
    suspend fun obtenerTodosDirecto(): List<EstudianteEntity>
}