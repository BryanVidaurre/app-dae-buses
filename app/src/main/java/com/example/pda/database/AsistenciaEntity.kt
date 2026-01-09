package com.example.pda.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistencias")
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val estudianteToken: String,
    val pna_nom: String,
    val fecha_hora: Long, // Guardaremos el timestamp (System.currentTimeMillis())
    val busId: Int,
    val sincronizado: Boolean = false // Para saber si ya se envió a NestJS
)