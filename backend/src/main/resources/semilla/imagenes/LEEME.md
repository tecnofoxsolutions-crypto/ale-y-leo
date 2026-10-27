# Imágenes de los datos de prueba (DI-10)

Aquí van las 20 fotos de las artesanías, nombradas `art-1.jpg` a `art-20.jpg` (también sirve `.png`),
en el mismo orden que la lista de `DataSeeder.kt` (1 = Colgante Lapislázuli Cordillera ... 20 = Tapiz Andino).

Al arrancar con la base de datos vacía, el DataSeeder las copia a la carpeta `uploads/`
y quedan disponibles en `http://localhost:8080/uploads/art-N.jpg`.
Si una foto no está, esa artesanía queda sin imagen y la app muestra el ícono de su categoría.
