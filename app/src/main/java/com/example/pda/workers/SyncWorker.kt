package com.example.pda.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pda.api.RetrofitClient
import com.example.pda.database.AppDatabase
import com.example.pda.models.CreateIngresoBusDto

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.asistenciaDao()
        val pendientes = dao.obtenerPendientesManual()

        if (pendientes.isEmpty()) return Result.success()

        val batchSincronizacion = pendientes.take(50)

        var registrosFallidos = 0

        return try {
            batchSincronizacion.forEach { asistencia ->
                val dto = CreateIngresoBusDto(
                    est_sem_id = asistencia.est_sem_id,
                    bus_id = asistencia.bus_id,
                    qr_id = asistencia.qr_id,
                    fecha_hora = asistencia.fecha_hora.toString(),
                    latitud = asistencia.latitud,
                    longitud = asistencia.longitud
                )

                try {
                    val response = RetrofitClient.instance.registrarIngreso(dto)

                    if (response.isSuccessful) {
                        dao.marcarSincronizado(asistencia.ingreso_id)
                        // Pausa un poco más larga para dejar respirar a Postgres
                        kotlinx.coroutines.delay(300)
                    } else if (response.code() == 429 || response.code() >= 500) {
                        // Si el servidor está saturado (429) o fallando (500),
                        // detenemos el bucle y reintentamos todo el worker más tarde.
                        return Result.retry()
                    } else {
                        registrosFallidos++
                    }
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Error de red: ${e.message}")
                    // Si no hay internet del todo, mejor reintentar luego
                    return Result.retry()
                }
            }

            if (registrosFallidos > 0 || pendientes.size > batchSincronizacion.size) {
                Result.retry()
            } else {
                Result.success()
            }

        } catch (e: Exception) {
            Result.retry()
        }
    }
}