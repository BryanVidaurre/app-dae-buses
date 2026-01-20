//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.models](../index.md)/[CreateIngresoBusDto](index.md)

# CreateIngresoBusDto

[androidJvm]\
data class [CreateIngresoBusDto](index.md)(val est_sem_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val bus_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val bus_patente: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val qr_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val fecha_hora: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val latitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html), val longitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html))

Objeto de Transferencia de Datos (DTO) para registrar un nuevo ingreso al bus.

Este modelo se envía al servidor cuando el SyncWorker sincroniza los registros locales. Incluye datos de identidad, ubicación geográfica y temporal.

## Constructors

| | |
|---|---|
| [CreateIngresoBusDto](-create-ingreso-bus-dto.md) | [androidJvm]<br>constructor(est_sem_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), bus_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), bus_patente: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), qr_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), fecha_hora: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), latitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html), longitud: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [bus_id](bus_id.md) | [androidJvm]<br>val [bus_id](bus_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>ID del bus donde se realiza el ingreso. |
| [bus_patente](bus_patente.md) | [androidJvm]<br>val [bus_patente](bus_patente.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Patente del bus (requerida para validación en backend). |
| [est_sem_id](est_sem_id.md) | [androidJvm]<br>val [est_sem_id](est_sem_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>ID del semestre del estudiante capturado del QR. |
| [fecha_hora](fecha_hora.md) | [androidJvm]<br>val [fecha_hora](fecha_hora.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Timestamp del momento del escaneo en formato String. |
| [latitud](latitud.md) | [androidJvm]<br>val [latitud](latitud.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html)<br>Coordenada de latitud capturada por el GPS del dispositivo. |
| [longitud](longitud.md) | [androidJvm]<br>val [longitud](longitud.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-double/index.html)<br>Coordenada de longitud capturada por el GPS del dispositivo. |
| [qr_id](qr_id.md) | [androidJvm]<br>val [qr_id](qr_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>ID del código QR escaneado. |
