//[pda](../../../index.md)/[com.example.pda.database](../index.md)/[AsistenciaEntity](index.md)

# AsistenciaEntity

[androidJvm]\
data class [AsistenciaEntity](index.md)(val ingreso_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 0, val fecha_hora: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-long/index.html), val latitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html) = 0.0, val longitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html) = 0.0, val est_sem_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val bus_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val qr_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val pna_nom: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val pna_apat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val pna_amat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val sincronizado: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) = false)

Entidad que representa un registro de asistencia o abordaje en la base de datos local.

Cada instancia de esta clase corresponde a una fila en la tabla `asistencias`. Se utiliza para almacenar de forma persistente los escaneos realizados por el conductor, permitiendo su posterior sincronización con el servidor central mediante el SyncWorker.

## Constructors

| | |
|---|---|
| [AsistenciaEntity](-asistencia-entity.md) | [androidJvm]<br>constructor(ingreso_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 0, fecha_hora: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-long/index.html), latitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html) = 0.0, longitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html) = 0.0, est_sem_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), bus_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), qr_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), pna_nom: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), pna_apat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), pna_amat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), sincronizado: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) = false) |

## Properties

| Name | Summary |
|---|---|
| [bus_id](bus_id.md) | [androidJvm]<br>val [bus_id](bus_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Identificador del bus donde se realizó el registro. |
| [est_sem_id](est_sem_id.md) | [androidJvm]<br>val [est_sem_id](est_sem_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Identificador de la relación estudiante-semestre proveniente del QR. |
| [fecha_hora](fecha_hora.md) | [androidJvm]<br>val [fecha_hora](fecha_hora.md): [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-long/index.html)<br>Marca de tiempo (Unix timestamp) del momento exacto del escaneo. |
| [ingreso_id](ingreso_id.md) | [androidJvm]<br>val [ingreso_id](ingreso_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) = 0<br>Identificador único autogenerado por Room para la clave primaria. |
| [latitud](latitud.md) | [androidJvm]<br>val [latitud](latitud.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html) = 0.0<br>Coordenada de latitud capturada por el GPS al momento de la validación. |
| [longitud](longitud.md) | [androidJvm]<br>val [longitud](longitud.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html) = 0.0<br>Coordenada de longitud capturada por el GPS al momento de la validación. |
| [pna_amat](pna_amat.md) | [androidJvm]<br>val [pna_amat](pna_amat.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Apellido materno del pasajero (caché local para visualización rápida). |
| [pna_apat](pna_apat.md) | [androidJvm]<br>val [pna_apat](pna_apat.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Apellido paterno del pasajero (caché local para visualización rápida). |
| [pna_nom](pna_nom.md) | [androidJvm]<br>val [pna_nom](pna_nom.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Nombre(s) del pasajero (caché local para visualización rápida). |
| [qr_id](qr_id.md) | [androidJvm]<br>val [qr_id](qr_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Identificador único del código QR físico que fue escaneado. |
| [sincronizado](sincronizado.md) | [androidJvm]<br>val [sincronizado](sincronizado.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) = false<br>Estado de la sincronización. `false` indica que el registro solo existe en el teléfono; `true` indica que ya fue enviado con éxito a la API central. |
