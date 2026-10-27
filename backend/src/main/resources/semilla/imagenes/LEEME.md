# Imágenes de los datos de prueba (DI-10)

Las 20 fotos de las artesanías, de Emanuel, nombradas `art-1.jpg` a `art-20.jpg`
en el mismo orden que la lista de `DataSeeder.kt` (1 = Colgante Lapislázuli Cordillera ... 20 = Alfombra Anudada).
Están recortadas a 4:5 (896 x 1120).

Al arrancar con la base de datos vacía, el DataSeeder las copia a la carpeta `uploads/`
y quedan disponibles en `http://localhost:8080/uploads/art-N.jpg`.
La app guarda una copia más chica de cada una en `res/drawable-nodpi/art_N.jpg`.
