//[PDA UTA - Sistema de Control de Acceso](../../index.md)/[com.example.pda.database](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [AppDatabase](-app-database/index.md) | [androidJvm]<br>abstract class [AppDatabase](-app-database/index.md) : [RoomDatabase](https://developer.android.com/reference/kotlin/androidx/room/RoomDatabase.html)<br>Base de datos principal de la aplicación PDA. |
| [AsistenciaDao](-asistencia-dao/index.md) | [androidJvm]<br>interface [AsistenciaDao](-asistencia-dao/index.md)<br>Interfaz de Acceso a Datos (DAO) para la tabla de asistencias. |
| [AsistenciaEntity](-asistencia-entity/index.md) | [androidJvm]<br>data class [AsistenciaEntity](-asistencia-entity/index.md)(val ingreso_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 0, val fecha_hora: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-long/index.html), val latitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html) = 0.0, val longitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html) = 0.0, val est_sem_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val bus_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val qr_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val pna_nom: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val pna_apat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val pna_amat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val sincronizado: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) = false)<br>Entidad que representa un registro de asistencia o abordaje en la base de datos local. |
| [SessionManager](-session-manager/index.md) | [androidJvm]<br>class [SessionManager](-session-manager/index.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html))<br>Gestor de persistencia de sesión y caché ligera. |
