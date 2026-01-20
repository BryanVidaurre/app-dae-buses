package com.example.pda.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un registro de asistencia o abordaje en la base de datos local.
 *
 * Cada instancia de esta clase corresponde a una fila en la tabla `asistencias`.
 * Se utiliza para almacenar de forma persistente los escaneos realizados por el conductor,
 * permitiendo su posterior sincronización con el servidor central mediante el [SyncWorker].
 *
 * @property ingreso_id Identificador único autogenerado por Room para la clave primaria.
 * @property fecha_hora Marca de tiempo (Unix timestamp) del momento exacto del escaneo.
 * @property latitud Coordenada de latitud capturada por el GPS al momento de la validación.
 * @property longitud Coordenada de longitud capturada por el GPS al momento de la validación.
 * @property est_sem_id Identificador de la relación estudiante-semestre proveniente del QR.
 * @property bus_id Identificador del bus donde se realizó el registro.
 * @property qr_id Identificador único del código QR físico que fue escaneado.
 * @property pna_nom Nombre(s) del pasajero (caché local para visualización rápida).
 * @property pna_apat Apellido paterno del pasajero (caché local para visualización rápida).
 * @property pna_amat Apellido materno del pasajero (caché local para visualización rápida).
 * @property sincronizado Estado de la sincronización. `false` indica que el registro solo
 * existe en el teléfono; `true` indica que ya fue enviado con éxito a la API central.
 */
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
    val pna_apat: String,
    val pna_amat: String,
    val sincronizado: Boolean = false
)