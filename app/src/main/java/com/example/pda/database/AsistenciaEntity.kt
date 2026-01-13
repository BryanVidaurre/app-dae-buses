package com.example.pda.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistencias")
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val ingreso_id: Int = 0,        // PK en Postgres
    val fecha_hora: Long,           // timestamp
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val est_sem_id: Int,            // FK a estudiante_semestre
    val bus_id: Int,                // FK a bus
    val qr_id: Int,                 // FK a qr_token
    val pna_nom: String,
    val pna_apat:String,
    val pna_amat:String,
    val sincronizado: Boolean = false // Flag para saber qué subir
)