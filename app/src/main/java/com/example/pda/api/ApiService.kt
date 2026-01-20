package com.example.pda.api

import com.example.pda.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz que define los endpoints de la API REST para el sistema PDA.
 *
 * Esta interfaz es utilizada por Retrofit para realizar las peticiones de red.
 * Todas las funciones son de tipo `suspend`, lo que garantiza que las llamadas a la red
 * se realicen de forma no bloqueante dentro de corrutinas.
 */
interface ApiService {

    /**
     * Obtiene la lista de todos los buses registrados en el sistema.
     * * Se utiliza en la pantalla de configuración inicial para que el conductor
     * seleccione su unidad.
     * * @return Una lista de [BusResponse] con la información de los buses.
     */
    @GET("bus")
    suspend fun getBuses(): List<BusResponse>

    /**
     * Descarga el padrón completo de estudiantes autorizados para el semestre vigente.
     * * Estos datos son almacenados en la base de datos local (Room) para permitir
     * la validación de códigos QR sin necesidad de conexión a internet.
     * * @return Una lista de [EstudianteAutorizado].
     */
    @GET("ingresos/autorizados")
    suspend fun getEstudiantesAutorizados(): List<EstudianteAutorizado>

    /**
     * Registra un único ingreso de pasajero en tiempo real.
     * * Aunque el sistema prioriza el envío por lotes (bulk), este endpoint permite
     * la notificación inmediata de un abordaje si el sistema así lo requiere.
     * * @param ingreso Objeto [CreateIngresoBusDto] con los datos del escaneo y GPS.
     * @return [Response] con código de estado HTTP 201 (Created) o error.
     */
    @POST("ingresos/registrar")
    suspend fun registrarIngreso(@Body ingreso: CreateIngresoBusDto): Response<Unit>

    /**
     * Sincroniza un lote de registros de asistencia acumulados localmente.
     * * Es el método principal utilizado por el [SyncWorker]. Permite enviar hasta 50 registros
     * en una sola petición HTTP, optimizando el uso de la batería y el consumo de datos móviles.
     * * @param ingresos Lista de objetos [CreateIngresoBusDto] pendientes de sincronización.
     * @return [Response] que indica si el lote fue procesado correctamente por el servidor.
     */
    @POST("ingresos/bulk")
    suspend fun registrarIngresosBulk(@Body ingresos: List<CreateIngresoBusDto>): Response<Unit>
}