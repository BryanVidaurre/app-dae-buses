//[pda](../../../index.md)/[com.example.pda.api](../index.md)/[ApiService](index.md)/[registrarIngreso](registrar-ingreso.md)

# registrarIngreso

[androidJvm]\

@POST(value = &quot;ingresos/registrar&quot;)

abstract suspend fun [registrarIngreso](registrar-ingreso.md)(@Bodyingreso: [CreateIngresoBusDto](../../com.example.pda.models/-create-ingreso-bus-dto/index.md)): Response&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-unit/index.html)&gt;

Registra un único ingreso de pasajero en tiempo real.

- 
   Aunque el sistema prioriza el envío por lotes (bulk), este endpoint permite la notificación inmediata de un abordaje si el sistema así lo requiere.
- 
   @param ingreso Objeto [CreateIngresoBusDto](../../com.example.pda.models/-create-ingreso-bus-dto/index.md) con los datos del escaneo y GPS.

#### Return

Response con código de estado HTTP 201 (Created) o error.
