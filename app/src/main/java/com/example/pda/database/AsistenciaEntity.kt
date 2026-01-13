package com.example.pda.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistencias")
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val ingreso_id: Int = 0,
    val fecha_hora: Long,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val est_sem_id: Int,
    val bus_id: Int,
    val qr_id: Int,
    val pna_nom: String,
    val pna_apat:String,
    val pna_amat:String,
    val sincronizado: Boolean = false
)