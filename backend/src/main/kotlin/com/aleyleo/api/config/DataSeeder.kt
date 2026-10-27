package com.aleyleo.api.config

import com.aleyleo.api.archivo.FileStorageService
import com.aleyleo.api.artesania.Artesania
import com.aleyleo.api.artesania.ArtesaniaRepository
import com.aleyleo.api.categoria.Categoria
import com.aleyleo.api.categoria.CategoriaRepository
import com.aleyleo.api.pedido.DetallePedido
import com.aleyleo.api.pedido.EstadoPedido
import com.aleyleo.api.pedido.Pedido
import com.aleyleo.api.pedido.PedidoRepository
import com.aleyleo.api.usuario.Rol
import com.aleyleo.api.usuario.Usuario
import com.aleyleo.api.usuario.UsuarioRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.io.ClassPathResource
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

// BE-05: datos sintéticos de la sección 5.4, los mismos que usa la app en la E2 (DatosPrueba.kt).
// Se ejecuta al arrancar (CommandLineRunner) y solo si la BD está vacía, así no duplica nada.
// Todos los nombres, RUT, correos y direcciones son ficticios. Las contraseñas son solo de desarrollo.
@Component
@ConditionalOnProperty(name = ["aleyleo.datos-prueba"], havingValue = "true", matchIfMissing = true)
class DataSeeder(
    private val categoriaRepository: CategoriaRepository,
    private val artesaniaRepository: ArtesaniaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val pedidoRepository: PedidoRepository,
    private val passwordEncoder: PasswordEncoder,
    private val fileStorageService: FileStorageService
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(vararg args: String) {
        if (categoriaRepository.count() > 0 || usuarioRepository.count() > 0) {
            log.info("DataSeeder: la BD ya tiene datos, no se cargan los datos de prueba")
            return
        }
        val categorias = cargarCategorias()
        val artesanias = cargarArtesanias(categorias)
        val usuarios = cargarUsuarios()
        cargarPedidos(usuarios, artesanias)
        log.info(
            "DataSeeder: {} categorías, {} artesanías, {} usuarios y {} pedidos cargados",
            categorias.size, artesanias.size, usuarios.size, pedidoRepository.count()
        )
    }

    private fun cargarCategorias(): List<Categoria> = categoriaRepository.saveAll(
        listOf(
            Categoria(nombre = "Orfebrería", descripcion = "Joyas en plata y piedras chilenas"),
            Categoria(nombre = "Cobre", descripcion = "Piezas de cobre martillado y grabado"),
            Categoria(nombre = "Alfarería", descripcion = "Greda de Pomaire y Quinchamalí"),
            Categoria(nombre = "Textil", descripcion = "Tejidos a telar y a palillo")
        )
    )

    // 5 por categoría, con stock entre 0 y 5; 3 agotadas (stock 0) para probar.
    private fun cargarArtesanias(c: List<Categoria>): List<Artesania> {
        val (orfebreria, cobre, alfareria, textil) = c
        val datos = listOf(
            // categoría, nombre, descripción, materiales, precio, stock
            Fila(orfebreria, "Colgante Lapislázuli Cordillera", "Colgante de plata con piedra lapislázuli tallada a mano.", "Lapislázuli, plata 950", 28500, 1),
            Fila(orfebreria, "Aros Lapislázuli Gota", "Aros de plata martillada con lapislázuli en forma de gota.", "Plata 950, lapislázuli", 19900, 4),
            Fila(orfebreria, "Anillo Luna Mapuche", "Anillo ancho de plata martillada con una turquesa ovalada.", "Plata 950, turquesa", 24000, 0),
            Fila(orfebreria, "Collar de Cuarzo", "Collar de cuarzo transparente y ahumado con cierre de plata.", "Cuarzo, plata 950", 32000, 3),
            Fila(orfebreria, "Prendedor Trapelacucha", "Réplica de un prendedor tradicional mapuche.", "Alpaca, repujado", 45000, 2),
            Fila(cobre, "Paila de Cobre", "Paila tradicional para huevos o pebre.", "Cobre martillado", 14000, 5),
            Fila(cobre, "Jarro Martillado", "Jarro para agua, martillado a mano.", "Cobre, estaño", 36000, 2),
            Fila(cobre, "Bandeja Desierto", "Bandeja grabada con motivos del desierto de Atacama.", "Cobre grabado", 27500, 0),
            Fila(cobre, "Candelero de Cobre", "Candelero martillado a mano, con asa para llevarlo.", "Cobre martillado", 12500, 4),
            Fila(cobre, "Cuenco con Pátina", "Cuenco de cobre con pátina verde por fuera y brillo por dentro.", "Cobre, pátina natural", 9900, 5),
            Fila(alfareria, "Chanchito de la Suerte", "Chanchito de tres patas de Quinchamalí.", "Greda negra, incisión con caolín", 8500, 5),
            Fila(alfareria, "Fuente de Greda", "Fuente de Pomaire con asas, para el horno o la mesa.", "Greda de Pomaire", 15900, 3),
            Fila(alfareria, "Guitarrera de Quinchamalí", "Figura de mujer con guitarra, pieza típica de Ñuble.", "Greda negra pulida", 22000, 1),
            Fila(alfareria, "Jarro de Greda", "Jarro de greda roja para agua o chicha.", "Greda de Pomaire", 13500, 4),
            Fila(alfareria, "Figura de Talagante", "Figura de loza pintada a mano: mujer con canasto de flores.", "Loza policromada", 29000, 0),
            Fila(textil, "Camino de Mesa en Telar", "Camino de mesa tejido a telar con lana de oveja de Chiloé.", "Lana de oveja teñida, telar", 48000, 2),
            Fila(textil, "Chal de Alpaca", "Chal liviano en tonos tierra.", "Lana de alpaca, palillo", 39500, 3),
            Fila(textil, "Pie de Cama a Telar", "Pie de cama de lana con franjas, tejido a telar.", "Lana natural, telar", 18900, 5),
            Fila(textil, "Trarihue", "Faja mapuche tejida a telar con diseños geométricos.", "Lana, telar mapuche", 11900, 4),
            Fila(textil, "Alfombra Anudada", "Alfombra pequeña de lana anudada a mano con un rombo al centro.", "Lana teñida con tintes naturales", 54000, 1)
        )
        val artesanias = artesaniaRepository.saveAll(
            datos.map {
                Artesania(
                    categoria = it.categoria, nombre = it.nombre, descripcion = it.descripcion,
                    materialesTecnica = it.materiales, precioBase = it.precio, stock = it.stock
                )
            }
        )
        // Imágenes: si existe semilla/imagenes/art-N.jpg (o .png), se copia a /uploads.
        // Mientras no lleguen las fotos (DI-10), la artesanía queda sin imagen y la app muestra el ícono.
        artesanias.forEachIndexed { i, artesania -> artesania.imagenUrl = copiarImagen("art-${i + 1}", "art-${artesania.id}") }
        return artesanias
    }

    private fun cargarUsuarios(): List<Usuario> {
        val admin = passwordEncoder.encode("Admin123")
        val cliente = passwordEncoder.encode("Cliente123")
        return usuarioRepository.saveAll(
            listOf(
                Usuario(rut = "15.678.234-3", nombreCompleto = "Alejandra Torres", email = "admin@aleyleo.cl", telefono = "+56911112222",
                    direccion = "Los Artesanos 450", comuna = "Ñuñoa", passwordHash = admin, rol = Rol.ADMIN),
                Usuario(rut = "18.432.567-5", nombreCompleto = "Sofía Valenzuela", email = "sofia@correo.cl", telefono = "+56987654321",
                    direccion = "Av. Siempre Viva 123", comuna = "Providencia", passwordHash = cliente),
                Usuario(rut = "19.876.543-0", nombreCompleto = "Matías Rojas", email = "matias@correo.cl", telefono = "+56923456789",
                    direccion = "Pasaje Los Aromos 77", comuna = "Maipú", passwordHash = cliente),
                Usuario(rut = "20.345.678-6", nombreCompleto = "Camila Soto", email = "camila@correo.cl", telefono = "+56934567890",
                    direccion = "Calle Larga 1020", comuna = "La Florida", passwordHash = cliente)
            )
        )
    }

    // Un pedido en cada uno de los 7 estados, para poder mostrar todas las pantallas.
    private fun cargarPedidos(u: List<Usuario>, a: List<Artesania>) {
        val (_, sofia, matias, camila) = u
        val pagados = setOf(EstadoPedido.PAGADA, EstadoPedido.RECIBIDA, EstadoPedido.EN_PREPARACION,
            EstadoPedido.FINALIZADA, EstadoPedido.RECHAZADA)

        fun pedido(numero: Int, cliente: Usuario, estado: EstadoPedido, notas: String?, fecha: String, vararg items: Pair<Int, Int>) {
            val creado = LocalDateTime.parse(fecha)
            val p = Pedido(
                codigo = "PED-2026-%04d".format(numero), usuario = cliente, estado = estado,
                direccionDespacho = cliente.direccion, comunaDespacho = cliente.comuna, notas = notas,
                fechaCreacion = creado, fechaActualizacion = creado,
                fechaPago = if (estado in pagados) creado.plusMinutes(5) else null
            )
            for ((indice, cantidad) in items) {
                val artesania = a[indice - 1] // índice 1 = primera artesanía de la lista
                p.agregarDetalle(
                    DetallePedido(artesania = artesania, nombreArtesania = artesania.nombre, cantidad = cantidad,
                        precioUnitario = artesania.precioBase, subtotal = cantidad * artesania.precioBase)
                )
            }
            p.total = p.detalles.sumOf { it.subtotal }
            pedidoRepository.save(p)
        }

        pedido(1, sofia, EstadoPedido.FINALIZADA, null, "2026-10-05T11:20", 6 to 2)
        pedido(2, matias, EstadoPedido.EN_PREPARACION, "Envolver para regalo", "2026-10-12T16:45", 16 to 1)
        pedido(3, camila, EstadoPedido.RECIBIDA, null, "2026-10-15T09:10", 2 to 1, 11 to 2)
        pedido(4, sofia, EstadoPedido.PAGADA, null, "2026-10-18T20:05", 17 to 1)
        pedido(5, matias, EstadoPedido.CANCELADA, null, "2026-10-19T13:30", 7 to 1)
        pedido(6, camila, EstadoPedido.RECHAZADA, null, "2026-10-20T10:00", 20 to 1)
        pedido(7, sofia, EstadoPedido.GENERADA, "Envolver para regalo", "2026-10-21T18:30", 1 to 1, 14 to 2)
    }

    private fun copiarImagen(origen: String, destino: String): String? {
        for (extension in listOf("jpg", "png")) {
            val recurso = ClassPathResource("semilla/imagenes/$origen.$extension")
            if (recurso.exists()) {
                return fileStorageService.guardarBytes(recurso.inputStream.use { it.readBytes() }, destino)
            }
        }
        return null
    }

    private data class Fila(
        val categoria: Categoria, val nombre: String, val descripcion: String,
        val materiales: String, val precio: Int, val stock: Int
    )
}
