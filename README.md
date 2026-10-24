# Ale y Leo

App Android de catálogo de artesanías + backend Spring Boot.

Equipo: Emanuel Reyes (planificación y diseño) · Jim Charles (desarrollo).

## Estructura

```
ale-y-leo/
├── app/       App Android (Kotlin, Jetpack Compose, Material 3, MVVM, Room, Navigation, lector QR)
└── backend/   API REST (Spring Boot en Kotlin, MySQL, JWT)  — próximamente
```

## Cómo correr la app

1. Abrir la carpeta `app/` en Android Studio (Open).
2. Esperar la sincronización de Gradle.
3. Ejecutar en un emulador o teléfono con Android 8.0 o superior.

## Convenciones de Git

- `main` estable; una rama por tarea: `feature/AP-04-navegacion`.
- Commits con el ID de la tarea al inicio: `AP-04: grafos de navegación`.
