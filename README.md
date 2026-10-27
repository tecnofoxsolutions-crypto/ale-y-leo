# Ale y Leo

App Android de catálogo de artesanías + backend Spring Boot.

Equipo: Emanuel Reyes (planificación y diseño) · Jim Charles (desarrollo).

## Estructura

```
ale-y-leo/
├── app/       App Android (Kotlin, Jetpack Compose, Material 3, MVVM, Room, Navigation, lector QR)
├── backend/   API REST (Spring Boot 3 en Kotlin, MySQL, JWT)
├── postman/   Colección de Postman con los 27 endpoints (ver postman/README.md)
└── qr/        Códigos QR de prueba para el lector de la app
```

## Cómo correr la app

1. Abrir la carpeta `app/` en Android Studio (Open).
2. Esperar la sincronización de Gradle.
3. Ejecutar en un emulador o teléfono con Android 8.0 o superior.

## Cómo correr el backend

1. Encender MySQL (en XAMPP: botón *Start* de MySQL). Usuario `root` sin contraseña.
2. Abrir la carpeta `backend/` en IntelliJ IDEA (u Android Studio) y ejecutar `AleYLeoApplication`,
   o desde una terminal: `cd backend` y `./gradlew bootRun`.
3. El schema `aleyleo` y sus tablas se crean solos. La API queda en `http://localhost:8080/api`.
4. Con la base vacía se cargan los datos de prueba (4 categorías, 20 artesanías, 4 usuarios y 7 pedidos):
   - Admin: `admin@aleyleo.cl` / `Admin123`
   - Clientes: `sofia@correo.cl`, `matias@correo.cl`, `camila@correo.cl` / `Cliente123`
   Las fotos de las artesanías van en `backend/src/main/resources/semilla/imagenes/` (`art-1.jpg` a `art-20.jpg`).

## Convenciones de Git

- `main` estable; una rama por tarea: `feature/AP-04-navegacion`.
- Commits con el ID de la tarea al inicio: `AP-04: grafos de navegación`.
