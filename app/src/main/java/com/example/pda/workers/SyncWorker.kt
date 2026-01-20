package com.example.pda.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pda.api.RetrofitClient
import com.example.pda.database.AppDatabase
import com.example.pda.database.SessionManager
import com.example.pda.models.CreateIngresoBusDto

/**
 * Trabajador en segundo plano encargado de la sincronización de datos.
 *
 * Esta clase utiliza [CoroutineWorker] para ejecutar tareas asíncronas de red. Su función principal
 * es recolectar los registros de abordaje almacenados localmente en la base de datos Room y
 * enviarlos al servidor mediante una solicitud bulk (lote).
 *
 * @param appContext El contexto de la aplicación.
 * @param workerParams Parámetros de configuración del trabajador.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    /**
     * Ejecuta la lógica de sincronización.
     *
     * El flujo de trabajo consiste en:
     * 1. Consultar registros pendientes en la base de datos local.
     * 2. Validar que exista una patente configurada en [SessionManager].
     * 3. Empaquetar los datos en una lista de [CreateIngresoBusDto].
     * 4. Enviar los datos al servidor vía Retrofit.
     * 5. Si la subida es exitosa, marcar los registros locales como sincronizados.
     *
     * @return [Result.success] si se sincronizó todo, [Result.retry] si hubo error de red para
     * reintentar más tarde, o [Result.failure] si hay errores de configuración críticos.
     */
    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.asistenciaDao()

        // Instanciamos el SessionManager para obtener la patente actual que será enviada al servidor
        val sessionManager = SessionManager(applicationContext)
        val patenteActual = sessionManager.obtenerBusNombre()

        // Obtener registros que aún no tienen el flag de sincronizado
        val pendientes = dao.obtenerPendientesManual()
        if (pendientes.isEmpty()) return Result.success()

        // Si por alguna razón no hay patente, no podemos validar en el servidor actual
        if (patenteActual == null) {
            Log.e("SyncWorker", "No se puede sincronizar: No hay patente configurada en la sesión")
            return Result.failure()
        }

        // Limitamos el envío a lotes de 50 para evitar saturar el payload de la petición
        val registrosParaEnviar = pendientes.take(50)

        /**
         * Mapeo de entidades de base de datos local a objetos de transferencia de datos (DTO)
         */
        val listaDtos = registrosParaEnviar.map { asistencia ->
            CreateIngresoBusDto(
                est_sem_id = asistencia.est_sem_id,
                bus_id = asistencia.bus_id,
                bus_patente = patenteActual, // Se inyecta la patente actual de la sesión
                qr_id = asistencia.qr_id,
                fecha_hora = asistencia.fecha_hora.toString(),
                latitud = asistencia.latitud,
                longitud = asistencia.longitud
            )
        }

        return try {
            Log.d("SyncWorker", "Enviando lote con patente: $patenteActual")

            // Intento de envío al endpoint del servidor central
            val response = RetrofitClient.instance.registrarIngresosBulk(listaDtos)

            if (response.isSuccessful) {
                // Si el servidor acepta el lote, actualizamos los estados locales
                val idsSincronizados = registrosParaEnviar.map { it.ingreso_id }
                idsSincronizados.forEach { dao.marcarSincronizado(it) }

                Log.d("SyncWorker", "Sincronización exitosa: ${listaDtos.size} registros")

                // Si aún quedan más registros por enviar, solicitamos un nuevo intento inmediato
                if (pendientes.size > registrosParaEnviar.size) Result.retry() else Result.success()
            } else {
                Log.e("SyncWorker", "Error en servidor: ${response.code()} - ${response.errorBody()?.string()}")
                Result.retry() // Reintenta según la política de retroceso (backoff) configurada
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error de red: ${e.message}")
            Result.retry() // Error de conexión, se reintenta automáticamente
        }
    }
}