//[pda](../../../../index.md)/[com.example.pda.database](../../index.md)/[AppDatabase](../index.md)/[Companion](index.md)/[getDatabase](get-database.md)

# getDatabase

[androidJvm]\
fun [getDatabase](get-database.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)): [AppDatabase](../index.md)

Retorna la instancia única de la base de datos.

Implementa un bloqueo sincronizado para prevenir que múltiples hilos creen instancias competidoras simultáneamente.

#### Return

La instancia persistente de [AppDatabase](../index.md).

#### Parameters

androidJvm

| | |
|---|---|
| context | El contexto de la aplicación. |
