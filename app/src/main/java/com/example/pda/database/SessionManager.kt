package com.example.pda.database

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    // Nombre del archivo de preferencias
    private val PREFS_NAME = "pda_bus_session"

    // Claves para los datos
    private val KEY_BUS_ID = "selected_bus_id"
    private val KEY_BUS_NOMBRE = "selected_bus_nombre"

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Guarda el ID y la patente del bus seleccionado.
     */
    fun guardarBus(id: Int, nombre: String) {
        val editor = prefs.edit()
        editor.putInt(KEY_BUS_ID, id)
        editor.putString(KEY_BUS_NOMBRE, nombre)
        editor.apply() // Guarda de forma asíncrona
    }

    /**
     * Retorna el ID del bus guardado.
     * Retorna -1 si no hay ninguno registrado.
     */
    fun obtenerBusId(): Int {
        return prefs.getInt(KEY_BUS_ID, -1)
    }

    /**
     * Retorna el nombre o patente del bus guardado.
     */
    fun obtenerBusNombre(): String? {
        return prefs.getString(KEY_BUS_NOMBRE, "No seleccionado")
    }

    /**
     * Verifica si el conductor ya seleccionó un bus previamente.
     */
    fun estaBusSeleccionado(): Boolean {
        return obtenerBusId() != -1
    }

    /**
     * Borra los datos del bus seleccionado.
     * Útil cuando el conductor necesita cambiar de vehículo.
     */
    fun cerrarSesionBus() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}