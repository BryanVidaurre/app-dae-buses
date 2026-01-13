package com.example.pda.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estudiantes_autorizados")
data class EstudianteEntity(
    @PrimaryKey val per_id: String,
    val pna_nom: String,
    val pna_apat: String,
    val pna_amat: String,
    val token: String,
    val est_sem_id: Int,
    val qr_id: Int
)