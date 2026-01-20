//[pda](../../../index.md)/[com.example.pda.api](../index.md)/[ApiService](index.md)/[getEstudiantesAutorizados](get-estudiantes-autorizados.md)

# getEstudiantesAutorizados

[androidJvm]\

@GET(value = &quot;ingresos/autorizados&quot;)

abstract suspend fun [getEstudiantesAutorizados](get-estudiantes-autorizados.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[EstudianteAutorizado](../../com.example.pda.models/-estudiante-autorizado/index.md)&gt;

Descarga el padrón completo de estudiantes autorizados para el semestre vigente.

- 
   Estos datos son almacenados en la base de datos local (Room) para permitir la validación de códigos QR sin necesidad de conexión a internet.
- 
   @return Una lista de [EstudianteAutorizado](../../com.example.pda.models/-estudiante-autorizado/index.md).
