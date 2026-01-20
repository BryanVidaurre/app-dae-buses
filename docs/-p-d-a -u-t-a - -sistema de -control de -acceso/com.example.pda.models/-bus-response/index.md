//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.models](../index.md)/[BusResponse](index.md)

# BusResponse

[androidJvm]\
data class [BusResponse](index.md)(val bus_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val bus_patente: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val deleted: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html))

Representa la respuesta del servidor al consultar la lista de buses disponibles.

## Constructors

| | |
|---|---|
| [BusResponse](-bus-response.md) | [androidJvm]<br>constructor(bus_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), bus_patente: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), deleted: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [bus_id](bus_id.md) | [androidJvm]<br>val [bus_id](bus_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Identificador único del bus en la base de datos central. |
| [bus_patente](bus_patente.md) | [androidJvm]<br>val [bus_patente](bus_patente.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Placa patente del vehículo (ej. &quot;ABCD-12&quot;). |
| [deleted](deleted.md) | [androidJvm]<br>val [deleted](deleted.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html)<br>Flag lógico que indica si el bus ha sido dado de baja en el sistema. |
