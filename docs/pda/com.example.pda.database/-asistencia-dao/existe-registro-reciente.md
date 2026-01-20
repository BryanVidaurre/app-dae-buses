//[pda](../../../index.md)/[com.example.pda.database](../index.md)/[AsistenciaDao](index.md)/[existeRegistroReciente](existe-registro-reciente.md)

# existeRegistroReciente

[androidJvm]\
abstract suspend fun [existeRegistroReciente](existe-registro-reciente.md)(estSemId: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), haceDosHoras: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-long/index.html)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)

Verifica la existencia de un registro previo para un estudiante en un margen de tiempo.

- 
   Esta consulta es vital para el control de duplicados. Permite saber si un alumno ya marcó su ingreso recientemente, evitando registros redundantes si el QR se escanea dos veces.
- 
   @param estSemId ID del estudiante a verificar.

#### Return

Cantidad de registros encontrados (0 si es el primer escaneo en el rango).

#### Parameters

androidJvm

| | |
|---|---|
| haceDosHoras | Timestamp que define el límite inferior del rango de búsqueda. |
