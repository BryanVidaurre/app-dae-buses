package com.example.pda.database

import android.content.Context
import androidx.room.*
import com.example.pda.database.dao.EstudianteDao
import com.example.pda.database.entities.EstudianteEntity

/**
 * Base de datos principal de la aplicación PDA.
 *
 * Utiliza la librería [Room] para gestionar el almacenamiento local en SQLite. Esta base de datos
 * es el pilar para el funcionamiento offline, almacenando tanto el padrón de estudiantes
 * autorizados como los registros de asistencia capturados.
 *
 * @property entities Define las tablas que componen la base de datos: [EstudianteEntity] y [AsistenciaEntity].
 * @property version Versión actual del esquema. Si se cambia la estructura, debe incrementarse.
 */
@Database(
    entities = [EstudianteEntity::class, AsistenciaEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /** Proporciona acceso a las operaciones relacionadas con los estudiantes. */
    abstract fun estudianteDao(): EstudianteDao

    /** Proporciona acceso a las operaciones relacionadas con las asistencias. */
    abstract fun asistenciaDao(): AsistenciaDao

    companion object {
        /**
         * Instancia de la base de datos siguiendo el patrón Singleton.
         * La anotación [Volatile] asegura que los cambios realizados por un hilo sean
         * inmediatamente visibles para los demás.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna la instancia única de la base de datos.
         *
         * Implementa un bloqueo sincronizado para prevenir que múltiples hilos creen
         * instancias competidoras simultáneamente.
         *
         * @param context El contexto de la aplicación.
         * @return La instancia persistente de [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pda_database"
                )
                    /**
                     * Política de migración destructiva:
                     * Si la versión de la DB cambia y no hay un plan de migración definido,
                     * se borrarán los datos existentes y se recrearán las tablas.
                     */
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}