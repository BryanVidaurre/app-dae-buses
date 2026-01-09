package com.example.pda.api

import com.example.pda.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("bus")
    suspend fun getBuses(): List<BusResponse>

    @GET("ingresos/autorizados")
    suspend fun getEstudiantesAutorizados(): List<EstudianteAutorizado>

    @POST("ingresos/registrar")
    suspend fun registrarIngreso(@Body ingreso: CreateIngresoBusDto): Response<Unit>
}