//[pda](../../../index.md)/[com.example.pda.models](../index.md)/[EstudianteAutorizado](index.md)

# EstudianteAutorizado

[androidJvm]\
data class [EstudianteAutorizado](index.md)(val per_id: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val pna_nom: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val pna_apat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val pna_amat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val token: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), val est_sem_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), val qr_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html))

Representa la información de un estudiante autorizado para utilizar el transporte.

Esta clase se utiliza para descargar la base de datos de alumnos y permitir la validación de códigos QR en modo offline (sin internet).

## Constructors

| | |
|---|---|
| [EstudianteAutorizado](-estudiante-autorizado.md) | [androidJvm]<br>constructor(per_id: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), pna_nom: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), pna_apat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), pna_amat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), token: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html), est_sem_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html), qr_id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [est_sem_id](est_sem_id.md) | [androidJvm]<br>val [est_sem_id](est_sem_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Identificador de la relación estudiante-semestre. |
| [per_id](per_id.md) | [androidJvm]<br>val [per_id](per_id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Identificador de la persona. |
| [pna_amat](pna_amat.md) | [androidJvm]<br>val [pna_amat](pna_amat.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Apellido materno del estudiante. |
| [pna_apat](pna_apat.md) | [androidJvm]<br>val [pna_apat](pna_apat.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Apellido paterno del estudiante. |
| [pna_nom](pna_nom.md) | [androidJvm]<br>val [pna_nom](pna_nom.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Nombre(s) del estudiante. |
| [qr_id](qr_id.md) | [androidJvm]<br>val [qr_id](qr_id.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)<br>Identificador único del código QR físico/digital. |
| [token](token.md) | [androidJvm]<br>val [token](token.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)<br>Cadena de texto única contenida en el código QR del estudiante. |
