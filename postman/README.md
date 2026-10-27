# Colección de Postman (BE-17)

Prueba los **27 endpoints** de la API (sección 6.1) en orden, con **47 peticiones** que incluyen los casos de error
(400, 401, 403, 404 y 409). El número entre corchetes en cada petición es el endpoint de la tabla 6.1.

## Archivos

| Archivo | Para qué |
|---|---|
| `AleYLeo.postman_collection.json` | La colección, en 7 carpetas |
| `AleYLeo-local.postman_environment.json` | Entorno con `baseUrl = http://localhost:8080` y las variables que llena la colección |
| `imagen-prueba.png` | Imagen que usan las 2 peticiones de subida (foto de perfil e imagen de artesanía) |

## Cómo correrla

1. Encender MySQL (XAMPP) y el backend (`cd backend` y `./gradlew bootRun`).
2. En Postman: **Import** → elegir los dos `.json`.
3. Arriba a la derecha, elegir el entorno **Ale y Leo - local**.
4. En las 2 peticiones de subida (*Subir foto de perfil* y *Subir imagen de la artesanía*), pestaña **Body**:
   si Postman no encuentra el archivo, volver a elegir `postman/imagen-prueba.png`.
5. Clic derecho en la colección → **Run collection** → **Run Ale y Leo API**.

## Token automático

Las peticiones de login guardan el token en el entorno (`tokenAdmin` y `tokenCliente`) con un script en la
pestaña **Tests**. Las demás lo usan en la cabecera `Authorization: Bearer {{tokenAdmin}}`, así que no hay
que copiarlo a mano. Los ids que se crean (categoría, artesanía, pedidos) también se guardan solos.

## Se puede correr muchas veces

El registro genera un RUT válido y un email nuevos en cada corrida (pestaña **Pre-request Script**), y la
categoría lleva la hora en el nombre. Por eso no choca con datos de corridas anteriores.

Cada corrida deja en la base un cliente "Cliente Postman" con 3 pedidos y una categoría y artesanía
desactivadas. Para volver solo a los datos de prueba: en MySQL `DROP DATABASE aleyleo;` y arrancar el
backend de nuevo (el DataSeeder la recrea).
