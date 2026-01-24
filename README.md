# PDA UTA - Control de Acceso Estudiantil

Sistema móvil para la gestión de abordaje de buses institucionales mediante códigos QR.

##  Características Técnicas
* **Detección QR:** Integración con Google ML Kit para escaneo de alta velocidad.
* **Modo Offline:** Base de datos local Room que permite trabajar sin internet.
* **Geolocalización:** Captura de coordenadas GPS mediante Fused Location Provider.
* **Sincronización:** WorkManager para envío de datos en segundo plano cuando hay red.

## Puesta en marcha rápida (desarrollo)
1. **Requisitos:** JDK 17, Android Studio (o Gradle 8+), SDK Android (min 24 / target 35).  
2. **Sincronizar dependencias:** abre el proyecto en Android Studio o ejecuta:
   ```bash
   ./gradlew build
   ```
3. **Ejecutar en emulador/dispositivo:**  
   ```bash
   ./gradlew installDebug
   ```
4. **Configurar API local:** la app apunta por defecto a `http://10.0.2.2:3000/` (localhost del host desde el emulador). Si usas un backend remoto, cambia la URL base según el entorno (ver guía de producción).  

## Guía para llevar a producción
Si vas a entregar este proyecto a otro equipo para publicarlo, revisa la guía detallada:
**[Guía de producción](./docs/production.md)**.

##  Documentación del Código
Para ver la documentación detallada de cada clase y método ve al siguente url:

 **[Explorar Documentación Técnica Aquí](./docs/index.md)**

##  Arquitectura
El proyecto sigue el patrón **MVVM** (Model-View-ViewModel) con Clean Architecture:

1.  **UI (Jetpack Compose):** Pantallas reactivas y modernas.
2.  **Data:** Repositorios que deciden si obtener datos de Room (local) o Retrofit (remoto).
3.  **Services:** Asistente de voz y localización.

## Mantenimiento y documentación
* **Generar documentación técnica (Dokka):**
  ```bash
  ./gradlew dokkaGfm publishDevDocs
  ```
  Esto actualiza los archivos bajo `./docs`.  



---
© 2024 Proyecto PDA - Universidad de Tarapacá
