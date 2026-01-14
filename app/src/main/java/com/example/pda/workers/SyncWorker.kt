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

        // 1. Obtenemos todos los registros pendientes
        val pendientes = dao.obtenerPendientesManual()
        if (pendientes.isEmpty()) return Result.success()

        // 2. Tomamos un lote (ahora podemos subirlo a 50 sin miedo porque es una sola petición)
        val registrosParaEnviar = pendientes.take(50)

        // 3. Transformamos la lista de la DB local a la lista de DTOs para el servidor
        val listaDtos = registrosParaEnviar.map { asistencia ->
            CreateIngresoBusDto(
                est_sem_id = asistencia.est_sem_id,
                bus_id = asistencia.bus_id,
                qr_id = asistencia.qr_id,
                fecha_hora = asistencia.fecha_hora.toString(),
                latitud = asistencia.latitud,
                longitud = asistencia.longitud
            )
        }

        return try {
            Log.d("SyncWorker", "Iniciando sincronización masiva de ${listaDtos.size} registros")

            // 4. Enviamos TODO el lote en una sola llamada HTTP
            val response = RetrofitClient.instance.registrarIngresosBulk(listaDtos)

            if (response.isSuccessful) {
                // 5. Si el servidor guardó todo bien, marcamos todos como sincronizados localmente
                registrosParaEnviar.forEach {
                    dao.marcarSincronizado(it.ingreso_id)
                }

                Log.d("SyncWorker", "Sincronización masiva exitosa")

                // Si aún quedan más en la DB, reintentamos para procesar el siguiente lote
                if (pendientes.size > registrosParaEnviar.size) Result.retry() else Result.success()
            } else {
                Log.e("SyncWorker", "Error en servidor: ${response.code()}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error de red crítico: ${e.message}")
            Result.retry()
        }
    }
}