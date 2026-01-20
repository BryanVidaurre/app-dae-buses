package com.example.pda.database.dao

import androidx.room.*
import com.example.pda.database.entities.EstudianteEntity

/**
 * Interfaz de Acceso a Datos (DAO) para la gestión de estudiantes autorizados.
 *
 * Esta interfaz define las operaciones necesarias para mantener actualizada la lista local
 * de alumnos. Su función principal es permitir la búsqueda instantánea por token de QR
 * sin requerir conexión a internet durante el proceso de abordaje.
 */
@Dao
interface EstudianteDao {

    /**
     * Inserta una lista completa de estudiantes en la base de datos local.
     * * Se utiliza típicamente durante el proceso de sincronización inicial o actualización
     * del padrón. Si un estudiante ya existe (basado en su clave primaria), será
     * actualizado con la nueva información.
     *
     * @param estudiantes Lista de objetos [EstudianteEntity] a persistir.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEstudiantes(estudiantes: List<EstudianteEntity>)

    /**
     * Busca a un estudiante en la base de datos local utilizando el contenido del código QR.
     *
     * Este es el método crítico utilizado por el [ScannerScreen] durante el escaneo.
     * Al estar indexado y ser una consulta local, la respuesta es prácticamente instantánea.
     *
     * @param tokenInput La cadena de texto (token) extraída tras procesar la imagen del QR.
     * @return El objeto [EstudianteEntity] si se encuentra una coincidencia, de lo contrario null.
     */
    @Query("SELECT * FROM estudiantes_autorizados WHERE token = :tokenInput LIMIT 1")
    suspend fun buscarPorToken(tokenInput: String): EstudianteEntity?

    /**
     * Elimina todos los registros de la tabla de estudiantes.
     *
     * Útil para realizar una limpieza completa del padrón antes de descargar una
     * lista nueva, evitando que queden registros obsoletos de semestres anteriores.
     */
    @Query("DELETE FROM estudiantes_autorizados")
    suspend fun borrarTodo()

    /**
     * Recupera la lista completa de estudiantes almacenados en el dispositivo.
     *
     * @return Una lista estática con todos los registros de [EstudianteEntity].
     */
    @Query("SELECT * FROM estudiantes_autorizados")
    suspend fun obtenerTodosDirecto(): List<EstudianteEntity>
}