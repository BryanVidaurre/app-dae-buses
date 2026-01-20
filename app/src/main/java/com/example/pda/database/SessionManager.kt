package com.example.pda.database

import android.content.Context
import android.content.SharedPreferences
import com.example.pda.models.BusResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Gestor de persistencia de sesión y caché ligera.
 *
 * Esta clase utiliza [SharedPreferences] para almacenar de forma persistente la configuración
 * del terminal PDA. Su objetivo principal es gestionar la identidad del bus actual y
 * mantener una copia local de la lista de buses para permitir el funcionamiento offline.
 *
 * @param context El contexto de la aplicación necesario para acceder a las preferencias.
 */
class SessionManager(context: Context) {

    /** Nombre del archivo XML de preferencias privadas. */
    private val PREFS_NAME = "pda_bus_session"

    /** Clave para almacenar el identificador del bus seleccionado. */
    private val KEY_BUS_ID = "selected_bus_id"

    /** Clave para almacenar la patente del bus seleccionado. */
    private val KEY_BUS_NOMBRE = "selected_bus_nombre"

    /** Clave para almacenar el JSON de la lista de buses disponibles. */
    private val KEY_BUSES_CACHE = "buses_cache"

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Persiste el ID y la patente del bus seleccionado en el almacenamiento local.
     *
     * @param id Identificador único del bus (bus_id).
     * @param nombre Patente del bus (bus_patente).
     */
    fun guardarBus(id: Int, nombre: String) {
        val editor = prefs.edit()
        editor.putInt(KEY_BUS_ID, id)
        editor.putString(KEY_BUS_NOMBRE, nombre)
        editor.apply() // Guarda de forma asíncrona en disco
    }

    /**
     * Recupera el ID del bus guardado actualmente.
     * * @return El ID del bus o -1 si no se ha seleccionado ninguna unidad todavía.
     */
    fun obtenerBusId(): Int {
        return prefs.getInt(KEY_BUS_ID, -1)
    }

    /**
     * Recupera la patente o el nombre del bus configurado.
     * * @return String con la patente o "No seleccionado" como valor por defecto.
     */
    fun obtenerBusNombre(): String? {
        return prefs.getString(KEY_BUS_NOMBRE, "No seleccionado")
    }

    /**
     * Valida si existe una sesión de bus activa.
     * * @return true si el ID es un valor positivo válido (distinto de -1 y 0).
     */
    fun estaBusSeleccionado(): Boolean {
        val id = obtenerBusId()
        return id != -1 && id != 0
    }

    /**
     * Elimina los datos del bus seleccionado de la sesión actual.
     * * Se utiliza para permitir que el conductor cambie de vehículo.
     * **Nota:** No utiliza clear() para preservar otros datos como la caché de buses.
     */
    fun cerrarSesionBus() {
        val editor = prefs.edit()
        editor.remove(KEY_BUS_ID)
        editor.remove(KEY_BUS_NOMBRE)
        editor.apply()
    }

    /**
     * Guarda la lista completa de buses en formato JSON.
     * * Esto permite que la pantalla de selección de bus muestre opciones incluso si no hay
     * conexión a internet en el momento de abrir la app.
     * * @param buses Lista de objetos [BusResponse] obtenidos de la API.
     */
    fun guardarListaBuses(buses: List<BusResponse>) {
        val json = Gson().toJson(buses)
        prefs.edit().putString(KEY_BUSES_CACHE, json).apply()
    }

    /**
     * Recupera la última lista de buses almacenada en caché.
     * * @return Lista de [BusResponse]. Si no hay caché, retorna una lista vacía.
     */
    fun obtenerListaBusesCache(): List<BusResponse> {
        val json = prefs.getString(KEY_BUSES_CACHE, null) ?: return emptyList()
        val type = object : TypeToken<List<BusResponse>>() {}.type
        return Gson().fromJson(json, type)
    }
}