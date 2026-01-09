package com.example.pda.database

import android.content.Context
import androidx.room.*
import com.example.pda.database.dao.EstudianteDao
import com.example.pda.database.entities.EstudianteEntity

@Database(entities = [EstudianteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun estudianteDao(): EstudianteDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "pda_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}