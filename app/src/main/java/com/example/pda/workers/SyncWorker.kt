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

        // 1. Obtenemos las asistencias que no han sido enviadas al servidor
        val pendientes = dao.obtenerPendientesManual()

        if (pendientes.isEmpty()) return Result.success()

        return try {
            var algunError = false

            pendientes.forEach { asistencia ->
                // 2. CORRECCIÓN: Convertimos fecha_hora (Long) a String para el DTO
                val dto = CreateIngresoBusDto(
                    est_sem_id = asistencia.est_sem_id,
                    bus_id = asistencia.bus_id,
                    qr_id = asistencia.qr_id,
                    fecha_hora = asistencia.fecha_hora.toString(), // Fix: Long to String
                    latitud = asistencia.latitud,
                    longitud = asistencia.longitud
                )

                val response = RetrofitClient.instance.registrarIngreso(dto)

                if (response.isSuccessful) {
                    // 4. Marcamos como sincronizado usando el ID correcto: ingreso_id
                    dao.marcarSincronizado(asistencia.ingreso_id)
                    Log.d("SyncWorker", "Sincronizado: ${asistencia.pna_nom}")
                } else {
                    algunError = true
                    Log.e("SyncWorker", "Error al sincronizar ID: ${asistencia.ingreso_id}")
                }
            }

            if (algunError) Result.retry() else Result.success()

        } catch (e: Exception) {
            Log.e("SyncWorker", "Fallo crítico de red o base de datos", e)
            Result.retry()
        }
    }
}