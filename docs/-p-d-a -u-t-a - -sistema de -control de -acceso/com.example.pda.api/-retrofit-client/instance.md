//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.api](../index.md)/[RetrofitClient](index.md)/[instance](instance.md)

# instance

[androidJvm]\
val [instance](instance.md): [ApiService](../-api-service/index.md)

Instancia perezosa (lazy) del servicio API.

- 
   La inicialización ocurre únicamente la primera vez que se accede a la propiedad `instance`. Configura el convertidor GsonConverterFactory para manejar automáticamente la serialización y deserialización de objetos JSON a clases de datos de Kotlin.
