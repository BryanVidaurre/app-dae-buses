# Guía de producción (handoff para otro equipo)

Esta guía resume los pasos y decisiones necesarias para llevar la app a producción y entregarla a otro equipo técnico.

---

## 1) Requisitos de entorno
- **JDK 17**.
- **Android Studio** (o CLI con Gradle 8+).
- **Android SDK** con `minSdk=24`, `targetSdk=35`.

---

## 2) Configuración de backend (API)
La app usa Retrofit y actualmente tiene una **URL base hardcodeada** en:

```
app/src/main/java/com/example/pda/api/RetrofitClient.kt
```

Por defecto apunta a:
```
http://10.0.2.2:3000/
```

> `10.0.2.2` es el localhost del host visto desde el emulador Android.

### Recomendación para producción
1. Reemplazar por la URL real del backend (HTTPS).
2. Idealmente, mover la URL a **BuildConfig** o a un archivo de configuración por ambiente (dev/staging/prod).

### Endpoints principales usados
La app consume estos endpoints:
- `GET /bus`
- `GET /ingresos/autorizados`
- `POST /ingresos/registrar`
- `POST /ingresos/bulk`

---

## 3) Permisos y consideraciones de seguridad
En el `AndroidManifest.xml` se declaran permisos de:
- **Internet**
- **Cámara** (escaneo QR)
- **Ubicación** (GPS)

El manifest permite tráfico en claro (`android:usesCleartextTraffic="true"`), lo cual **no es recomendable en producción**.  
Para release:
- Asegúrate de que el backend esté en **HTTPS**.
- Cambia `usesCleartextTraffic` a `false` o elimínalo.

---

## 4) Firma y publicación

### 4.1 Crear keystore
Ejemplo:
```bash
keytool -genkeypair -v -keystore pda-release.keystore \
  -alias pda-release -keyalg RSA -keysize 2048 -validity 10000
```

### 4.2 Agregar configuración de signing
En `app/build.gradle.kts`, agregar un bloque `signingConfigs` y asociarlo al `buildTypes.release`.

> **No** subir el keystore al repositorio. Guardarlo en un lugar seguro.

### 4.3 Versionado
Actualizar en `app/build.gradle.kts`:
- `versionCode`
- `versionName`

---

## 5) Construcción de release
Para APK:
```bash
./gradlew assembleRelease
```

Para AAB (Google Play):
```bash
./gradlew bundleRelease
```

El resultado queda en:
```
app/build/outputs/
```

---

## 6) Checklist rápido antes de producción
- [ ] URL del backend configurada para **producción**.
- [ ] Tráfico en claro deshabilitado.
- [ ] Keystore de release configurada.
- [ ] `versionCode` y `versionName` actualizados.
- [ ] APK/AAB generado.
- [ ] Pruebas básicas en dispositivo físico (QR, GPS, modo offline, sincronización).

---

## 7) Generar documentación técnica (opcional)
```bash
./gradlew dokkaGfm publishDevDocs
```
Esto actualiza los archivos en `./docs/`.
