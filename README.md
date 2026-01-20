# 🚌 PDA UTA - Control de Acceso Estudiantil

Sistema móvil para la gestión de abordaje de buses institucionales mediante códigos QR.

## 🚀 Características Técnicas
* **Detección QR:** Integración con Google ML Kit para escaneo de alta velocidad.
* **Modo Offline:** Base de datos local Room que permite trabajar sin internet.
* **Geolocalización:** Captura de coordenadas GPS mediante Fused Location Provider.
* **Sincronización:** WorkManager para envío de datos en segundo plano cuando hay red.

## 📚 Documentación del Código
Hemos generado la documentación detallada de cada clase y método usando Dokka.

👉 **[Explorar Documentación Técnica Aquí](./docs/index.md)**

## 🛠️ Arquitectura
El proyecto sigue el patrón **MVVM** (Model-View-ViewModel) con Clean Architecture:

1.  **UI (Jetpack Compose):** Pantallas reactivas y modernas.
2.  **Data:** Repositorios que deciden si obtener datos de Room (local) o Retrofit (remoto).
3.  **Services:** Asistente de voz y localización.



---
© 2024 Proyecto PDA - Universidad de Tarapacá
