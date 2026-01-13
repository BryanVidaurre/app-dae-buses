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

        var registrosExitosos = 0
        var registrosFallidos = 0

        return try {
            pendientes.forEach { asistencia ->
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
                        registrosExitosos++
                        Log.d("SyncWorker", "Éxito: ${asistencia.pna_nom}")

                        kotlinx.coroutines.delay(150)
                    } else {
                        registrosFallidos++
                        Log.e("SyncWorker", "Error Servidor (Código ${response.code()}) en ID: ${asistencia.ingreso_id}")
                    }
                } catch (e: Exception) {
                    registrosFallidos++
                    Log.e("SyncWorker", "Error de red individual: ${e.message}")
                    // No cortamos el bucle, intentamos con el siguiente
                }
            }

            Log.d("SyncWorker", "Resumen: $registrosExitosos éxitos, $registrosFallidos fallos.")

            // Si hubo fallos, pedimos a WorkManager que reintente más tarde
            if (registrosFallidos > 0) Result.retry() else Result.success()

        } catch (e: Exception) {
            Log.e("SyncWorker", "Fallo crítico", e)
            Result.retry()
        }
    }
}