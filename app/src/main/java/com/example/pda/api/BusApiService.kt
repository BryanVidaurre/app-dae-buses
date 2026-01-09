package com.example.pda

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. Modelo de datos sincronizado con tu entidad NestJS
data class BusResponse(
    val bus_id: Int,
    val bus_patente: String,
    val deleted: Boolean
)

// 2. Interfaz API
interface BusApiService {
    @GET("bus")
    suspend fun getBuses(): List<BusResponse>
}

// 3. Cliente Retrofit
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val instance: BusApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BusApiService::class.java)
    }
}