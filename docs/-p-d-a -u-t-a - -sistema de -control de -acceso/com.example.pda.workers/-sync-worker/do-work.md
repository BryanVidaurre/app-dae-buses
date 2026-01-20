//[PDA UTA - Sistema de Control de Acceso](../../../index.md)/[com.example.pda.workers](../index.md)/[SyncWorker](index.md)/[doWork](do-work.md)

# doWork

[androidJvm]\
open suspend override fun [doWork](do-work.md)(): [ListenableWorker.Result](https://developer.android.com/reference/kotlin/androidx/work/ListenableWorker.Result.html)

Ejecuta la lógica de sincronización.

El flujo de trabajo consiste en:

1. 
   Consultar registros pendientes en la base de datos local.
2. 
   Validar que exista una patente configurada en [SessionManager](../../com.example.pda.database/-session-manager/index.md).
3. 
   Empaquetar los datos en una lista de [CreateIngresoBusDto](../../com.example.pda.models/-create-ingreso-bus-dto/index.md).
4. 
   Enviar los datos al servidor vía Retrofit.
5. 
   Si la subida es exitosa, marcar los registros locales como sincronizados.

#### Return

[Result.success](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-result/-companion/success.html) si se sincronizó todo, Result.retry si hubo error de red para reintentar más tarde, o [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-result/-companion/failure.html) si hay errores de configuración críticos.
