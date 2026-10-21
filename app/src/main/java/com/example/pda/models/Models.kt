package com.example.pda.models

/**
 * Representa la respuesta del servidor al consultar la lista de buses disponibles.
 */
data class BusResponse(
    val bus_id: Int,
    val bus_patente: String?,
    val deleted: Boolean = false
)

/**
 * Representa la información de un estudiante autorizado.
 * Se han marcado los campos como opcionales (?) para evitar errores de parsing
 * si el backend devuelve valores nulos.
 */
data class EstudianteAutorizado(
    val per_id: String?,
    val pna_nom: String?,
    val pna_apat: String?,
    val pna_amat: String?,
    val token: String?,
    val est_sem_id: Int?,
    val qr_id: Int?
)

/**
 * DTO para registrar un nuevo ingreso al bus.
 */
data class CreateIngresoBusDto(
    val est_sem_id: Int,
    val bus_id: Int,
    val bus_patente: String,
    val qr_id: Int,
    val fecha_hora: String,
    val latitud: Double,
    val longitud: Double
)