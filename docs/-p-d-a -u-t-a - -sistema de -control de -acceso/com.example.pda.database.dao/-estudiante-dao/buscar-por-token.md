//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.database.dao](../index.md)/[EstudianteDao](index.md)/[buscarPorToken](buscar-por-token.md)

# buscarPorToken

[androidJvm]\
abstract suspend fun [buscarPorToken](buscar-por-token.md)(tokenInput: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)): [EstudianteEntity](../../com.example.pda.database.entities/-estudiante-entity/index.md)?

Busca a un estudiante en la base de datos local utilizando el contenido del código QR.

Este es el método crítico utilizado por el ScannerScreen durante el escaneo. Al estar indexado y ser una consulta local, la respuesta es prácticamente instantánea.

#### Return

El objeto [EstudianteEntity](../../com.example.pda.database.entities/-estudiante-entity/index.md) si se encuentra una coincidencia, de lo contrario null.

#### Parameters

androidJvm

| | |
|---|---|
| tokenInput | La cadena de texto (token) extraída tras procesar la imagen del QR. |
