package com.example.pda.models

/**
 * Representa la respuesta del servidor al consultar la lista de buses disponibles.
 *
 * @property bus_id Identificador único del bus en la base de datos central.
 * @property bus_patente Placa patente del vehículo (ej. "ABCD-12").
 * @property deleted Flag lógico que indica si el bus ha sido dado de baja en el sistema.
 */
data class BusResponse(
    val bus_id: Int,
    val bus_patente: String,
    val deleted: Boolean
)

/**
 * Representa la información de un estudiante autorizado para utilizar el transporte.
 *
 * Esta clase se utiliza para descargar la base de datos de alumnos y permitir
 * la validación de códigos QR en modo offline (sin internet).
 *
 * @property per_id Identificador de la persona.
 * @property pna_nom Nombre(s) del estudiante.
 * @property pna_apat Apellido paterno del estudiante.
 * @property pna_amat Apellido materno del estudiante.
 * @property token Cadena de texto única contenida en el código QR del estudiante.
 * @property est_sem_id Identificador de la relación estudiante-semestre.
 * @property qr_id Identificador único del código QR físico/digital.
 */
data class EstudianteAutorizado(
    val per_id: String,
    val pna_nom: String,
    val pna_apat: String,
    val pna_amat: String,
    val token: String,
    val est_sem_id: Int,
    val qr_id: Int
)

/**
 * Objeto de Transferencia de Datos (DTO) para registrar un nuevo ingreso al bus.
 *
 * Este modelo se envía al servidor cuando el [SyncWorker] sincroniza los registros
 * locales. Incluye datos de identidad, ubicación geográfica y temporal.
 *
 * @property est_sem_id ID del semestre del estudiante capturado del QR.
 * @property bus_id ID del bus donde se realiza el ingreso.
 * @property bus_patente Patente del bus (requerida para validación en backend).
 * @property qr_id ID del código QR escaneado.
 * @property fecha_hora Timestamp del momento del escaneo en formato String.
 * @property latitud Coordenada de latitud capturada por el GPS del dispositivo.
 * @property longitud Coordenada de longitud capturada por el GPS del dispositivo.
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