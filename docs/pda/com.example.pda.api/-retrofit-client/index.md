//[pda](../../../index.md)/[com.example.pda.api](../index.md)/[RetrofitClient](index.md)

# RetrofitClient

[androidJvm]\
object [RetrofitClient](index.md)

Cliente centralizado para las peticiones de red.

Implementa el patrón **Singleton** para asegurar que solo exista una instancia de Retrofit en toda la aplicación, optimizando así el uso de recursos y gestionando eficientemente el pool de conexiones.

## Properties

| Name | Summary |
|---|---|
| [instance](instance.md) | [androidJvm]<br>val [instance](instance.md): [ApiService](../-api-service/index.md)<br>Instancia perezosa (lazy) del servicio API. |
