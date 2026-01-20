//[pda](../../../index.md)/[com.example.pda.workers](../index.md)/[SyncWorker](index.md)

# SyncWorker

class [SyncWorker](index.md)(appContext: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), workerParams: [WorkerParameters](https://developer.android.com/reference/kotlin/androidx/work/WorkerParameters.html)) : [CoroutineWorker](https://developer.android.com/reference/kotlin/androidx/work/CoroutineWorker.html)

Trabajador en segundo plano encargado de la sincronización de datos.

Esta clase utiliza [CoroutineWorker](https://developer.android.com/reference/kotlin/androidx/work/CoroutineWorker.html) para ejecutar tareas asíncronas de red. Su función principal es recolectar los registros de abordaje almacenados localmente en la base de datos Room y enviarlos al servidor mediante una solicitud bulk (lote).

#### Parameters

androidJvm

| | |
|---|---|
| appContext | El contexto de la aplicación. |
| workerParams | Parámetros de configuración del trabajador. |

## Constructors

| | |
|---|---|
| [SyncWorker](-sync-worker.md) | [androidJvm]<br>constructor(appContext: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), workerParams: [WorkerParameters](https://developer.android.com/reference/kotlin/androidx/work/WorkerParameters.html)) |

## Properties

| Name | Summary |
|---|---|
| [coroutineContext](index.md#1269180052%2FProperties%2F-912451524) | [androidJvm]<br>open val [~~coroutineContext~~](index.md#1269180052%2FProperties%2F-912451524): CoroutineDispatcher |

## Functions

| Name | Summary |
|---|---|
| [doWork](do-work.md) | [androidJvm]<br>open suspend override fun [doWork](do-work.md)(): [ListenableWorker.Result](https://developer.android.com/reference/kotlin/androidx/work/ListenableWorker.Result.html)<br>Ejecuta la lógica de sincronización. |
| [getApplicationContext](index.md#-560782721%2FFunctions%2F-912451524) | [androidJvm]<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>fun [getApplicationContext](index.md#-560782721%2FFunctions%2F-912451524)(): [Context](https://developer.android.com/reference/kotlin/android/content/Context.html) |
| [getBackgroundExecutor](index.md#1421258461%2FFunctions%2F-912451524) | [androidJvm]<br>@[RestrictTo](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.html)(value = [[RestrictTo.Scope.LIBRARY_GROUP](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.Scope.LIBRARY_GROUP.html)])<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>open fun [getBackgroundExecutor](index.md#1421258461%2FFunctions%2F-912451524)(): [Executor](https://developer.android.com/reference/kotlin/java/util/concurrent/Executor.html) |
| [getForegroundInfo](index.md#1577343784%2FFunctions%2F-912451524) | [androidJvm]<br>open suspend fun [getForegroundInfo](index.md#1577343784%2FFunctions%2F-912451524)(): [ForegroundInfo](https://developer.android.com/reference/kotlin/androidx/work/ForegroundInfo.html) |
| [getForegroundInfoAsync](index.md#67363926%2FFunctions%2F-912451524) | [androidJvm]<br>override fun [getForegroundInfoAsync](index.md#67363926%2FFunctions%2F-912451524)(): ListenableFuture&lt;[ForegroundInfo](https://developer.android.com/reference/kotlin/androidx/work/ForegroundInfo.html)&gt; |
| [getId](index.md#-1759193821%2FFunctions%2F-912451524) | [androidJvm]<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>fun [getId](index.md#-1759193821%2FFunctions%2F-912451524)(): [UUID](https://developer.android.com/reference/kotlin/java/util/UUID.html) |
| [getInputData](index.md#-907781528%2FFunctions%2F-912451524) | [androidJvm]<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>fun [getInputData](index.md#-907781528%2FFunctions%2F-912451524)(): [Data](https://developer.android.com/reference/kotlin/androidx/work/Data.html) |
| [getNetwork](index.md#-1225012274%2FFunctions%2F-912451524) | [androidJvm]<br>@[RequiresApi](https://developer.android.com/reference/kotlin/androidx/annotation/RequiresApi.html)(value = 28)<br>@[Nullable](https://developer.android.com/reference/kotlin/androidx/annotation/Nullable.html)<br>fun [getNetwork](index.md#-1225012274%2FFunctions%2F-912451524)(): [Network](https://developer.android.com/reference/kotlin/android/net/Network.html)? |
| [getRunAttemptCount](index.md#1096617839%2FFunctions%2F-912451524) | [androidJvm]<br>@[IntRange](https://developer.android.com/reference/kotlin/androidx/annotation/IntRange.html)(from = 0)<br>fun [getRunAttemptCount](index.md#1096617839%2FFunctions%2F-912451524)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [getStopReason](index.md#-1809449288%2FFunctions%2F-912451524) | [androidJvm]<br>@[RequiresApi](https://developer.android.com/reference/kotlin/androidx/annotation/RequiresApi.html)(value = 31)<br>fun [getStopReason](index.md#-1809449288%2FFunctions%2F-912451524)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html) |
| [getTags](index.md#1356325797%2FFunctions%2F-912451524) | [androidJvm]<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>fun [getTags](index.md#1356325797%2FFunctions%2F-912451524)(): [MutableSet](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-mutable-set/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt; |
| [getTaskExecutor](index.md#1625383462%2FFunctions%2F-912451524) | [androidJvm]<br>@[RestrictTo](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.html)(value = [[RestrictTo.Scope.LIBRARY_GROUP](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.Scope.LIBRARY_GROUP.html)])<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>open fun [getTaskExecutor](index.md#1625383462%2FFunctions%2F-912451524)(): TaskExecutor |
| [getTriggeredContentAuthorities](index.md#514689021%2FFunctions%2F-912451524) | [androidJvm]<br>@[RequiresApi](https://developer.android.com/reference/kotlin/androidx/annotation/RequiresApi.html)(value = 24)<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>fun [getTriggeredContentAuthorities](index.md#514689021%2FFunctions%2F-912451524)(): [MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-mutable-list/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-string/index.html)&gt; |
| [getTriggeredContentUris](index.md#-1016068107%2FFunctions%2F-912451524) | [androidJvm]<br>@[RequiresApi](https://developer.android.com/reference/kotlin/androidx/annotation/RequiresApi.html)(value = 24)<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>fun [getTriggeredContentUris](index.md#-1016068107%2FFunctions%2F-912451524)(): [MutableList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin.collections/-mutable-list/index.html)&lt;[Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html)&gt; |
| [getWorkerFactory](index.md#-473896752%2FFunctions%2F-912451524) | [androidJvm]<br>@[RestrictTo](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.html)(value = [[RestrictTo.Scope.LIBRARY_GROUP](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.Scope.LIBRARY_GROUP.html)])<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>open fun [getWorkerFactory](index.md#-473896752%2FFunctions%2F-912451524)(): [WorkerFactory](https://developer.android.com/reference/kotlin/androidx/work/WorkerFactory.html) |
| [isStopped](index.md#-43937871%2FFunctions%2F-912451524) | [androidJvm]<br>fun [isStopped](index.md#-43937871%2FFunctions%2F-912451524)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [isUsed](index.md#2101847327%2FFunctions%2F-912451524) | [androidJvm]<br>@[RestrictTo](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.html)(value = [[RestrictTo.Scope.LIBRARY_GROUP](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.Scope.LIBRARY_GROUP.html)])<br>fun [isUsed](index.md#2101847327%2FFunctions%2F-912451524)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-boolean/index.html) |
| [onStopped](index.md#-1990082143%2FFunctions%2F-912451524) | [androidJvm]<br>override fun [onStopped](index.md#-1990082143%2FFunctions%2F-912451524)() |
| [setForeground](index.md#317365985%2FFunctions%2F-912451524) | [androidJvm]<br>suspend fun [setForeground](index.md#317365985%2FFunctions%2F-912451524)(foregroundInfo: [ForegroundInfo](https://developer.android.com/reference/kotlin/androidx/work/ForegroundInfo.html)) |
| [setForegroundAsync](index.md#-1269350234%2FFunctions%2F-912451524) | [androidJvm]<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>fun [setForegroundAsync](index.md#-1269350234%2FFunctions%2F-912451524)(@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)foregroundInfo: [ForegroundInfo](https://developer.android.com/reference/kotlin/androidx/work/ForegroundInfo.html)): ListenableFuture&lt;[Void](https://developer.android.com/reference/kotlin/java/lang/Void.html)&gt; |
| [setProgress](index.md#1755411902%2FFunctions%2F-912451524) | [androidJvm]<br>suspend fun [setProgress](index.md#1755411902%2FFunctions%2F-912451524)(data: [Data](https://developer.android.com/reference/kotlin/androidx/work/Data.html)) |
| [setProgressAsync](index.md#-348364649%2FFunctions%2F-912451524) | [androidJvm]<br>@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)<br>open fun [setProgressAsync](index.md#-348364649%2FFunctions%2F-912451524)(@[NonNull](https://developer.android.com/reference/kotlin/androidx/annotation/NonNull.html)data: [Data](https://developer.android.com/reference/kotlin/androidx/work/Data.html)): ListenableFuture&lt;[Void](https://developer.android.com/reference/kotlin/java/lang/Void.html)&gt; |
| [setUsed](index.md#1019169525%2FFunctions%2F-912451524) | [androidJvm]<br>@[RestrictTo](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.html)(value = [[RestrictTo.Scope.LIBRARY_GROUP](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.Scope.LIBRARY_GROUP.html)])<br>fun [setUsed](index.md#1019169525%2FFunctions%2F-912451524)() |
| [startWork](index.md#-1181660772%2FFunctions%2F-912451524) | [androidJvm]<br>override fun [startWork](index.md#-1181660772%2FFunctions%2F-912451524)(): ListenableFuture&lt;[ListenableWorker.Result](https://developer.android.com/reference/kotlin/androidx/work/ListenableWorker.Result.html)&gt; |
| [stop](index.md#-1498077086%2FFunctions%2F-912451524) | [androidJvm]<br>@[RestrictTo](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.html)(value = [[RestrictTo.Scope.LIBRARY_GROUP](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.Scope.LIBRARY_GROUP.html)])<br>fun [stop](index.md#-1498077086%2FFunctions%2F-912451524)(reason: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin-stdlib/kotlin/-int/index.html)) |
