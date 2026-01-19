package com.example.pda.models

data class BusResponse(val bus_id: Int, val bus_patente: String, val deleted: Boolean)

data class EstudianteAutorizado(
    val per_id: String,
    val pna_nom: String,
    val pna_apat:String,
    val pna_amat:String,
    val token: String,
    val est_sem_id: Int,
    val qr_id: Int
)

data class CreateIngresoBusDto(
    val est_sem_id: Int,
    val bus_id: Int,
    val bus_patente: String,
    val qr_id: Int,
    val fecha_hora: String,
    val latitud: Double,
    val longitud: Double
)