package com.example.pda.api

import com.example.pda.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("bus")
    suspend fun getBuses(): List<BusResponse>

    @GET("ingresos/autorizados")
    suspend fun getEstudiantesAutorizados(): List<EstudianteAutorizado>

    // 1. Mantenemos el registro individual por si lo necesitas
    @POST("ingresos/registrar")
    suspend fun registrarIngreso(@Body ingreso: CreateIngresoBusDto): Response<Unit>

    // 2. NUEVO: Registro masivo (Bulk)
    // Este enviará la lista completa en una sola conexión HTTP
    @POST("ingresos/bulk")
    suspend fun registrarIngresosBulk(@Body ingresos: List<CreateIngresoBusDto>): Response<Unit>
}