package com.example.pda.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pda.api.RetrofitClient
import com.example.pda.database.AppDatabase
import com.example.pda.database.SessionManager
import com.example.pda.models.CreateIngresoBusDto

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.asistenciaDao()
        // Instanciamos el SessionManager para obtener la patente actual
        val sessionManager = SessionManager(applicationContext)
        val patenteActual = sessionManager.obtenerBusNombre()

        val pendientes = dao.obtenerPendientesManual()
        if (pendientes.isEmpty()) return Result.success()

        // Si por alguna razón no hay patente, no podemos validar en el servidor actual
        if (patenteActual == null) {
            Log.e("SyncWorker", "No se puede sincronizar: No hay patente configurada en la sesión")
            return Result.failure()
        }

        val registrosParaEnviar = pendientes.take(50)

        val listaDtos = registrosParaEnviar.map { asistencia ->
            // Creamos un mapa o ajustamos el DTO para incluir bus_patente
            // Asegúrate de que CreateIngresoBusDto tenga el campo bus_patente
            CreateIngresoBusDto(
                est_sem_id = asistencia.est_sem_id,
                bus_id = asistencia.bus_id,
                bus_patente = patenteActual,
                qr_id = asistencia.qr_id,
                fecha_hora = asistencia.fecha_hora.toString(),
                latitud = asistencia.latitud,
                longitud = asistencia.longitud
            )
        }

        return try {
            Log.d("SyncWorker", "Enviando lote con patente: $patenteActual")

            val response = RetrofitClient.instance.registrarIngresosBulk(listaDtos)

            if (response.isSuccessful) {
                val idsSincronizados = registrosParaEnviar.map { it.ingreso_id }
                idsSincronizados.forEach { dao.marcarSincronizado(it) }

                Log.d("SyncWorker", "Sincronización exitosa: ${listaDtos.size} registros")
                if (pendientes.size > registrosParaEnviar.size) Result.retry() else Result.success()
            } else {
                Log.e("SyncWorker", "Error en servidor: ${response.code()} - ${response.errorBody()?.string()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error de red: ${e.message}")
            Result.retry()
        }
    }
}