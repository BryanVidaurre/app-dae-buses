package com.example.pda.api

import com.example.pda.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente centralizado para las peticiones de red.
 *
 * Implementa el patrón **Singleton** para asegurar que solo exista una instancia de
 * [Retrofit] en toda la aplicación, optimizando así el uso de recursos y
 * gestionando eficientemente el pool de conexiones.
 */
object RetrofitClient {

    /**
     * URL base del servidor API.
     * * Nota: `10.0.2.2` es la dirección especial de Android para acceder al localhost
     * de la máquina de desarrollo desde el emulador.
     */
    private const val BASE_URL = "http://10.0.2.2:3000/"

    /**
     * Instancia perezosa (lazy) del servicio API.
     * * La inicialización ocurre únicamente la primera vez que se accede a la propiedad `instance`.
     * Configura el convertidor [GsonConverterFactory] para manejar automáticamente la
     * serialización y deserialización de objetos JSON a clases de datos de Kotlin.
     */
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}