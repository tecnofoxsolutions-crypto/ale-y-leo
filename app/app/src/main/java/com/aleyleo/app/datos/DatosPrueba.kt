package com.aleyleo.app.datos

import com.aleyleo.app.model.Artesania
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.model.DetallePedido
import com.aleyleo.app.model.EstadoPedido
import com.aleyleo.app.model.Pedido
import com.aleyleo.app.model.Rol
import com.aleyleo.app.model.Usuario

// Datos ficticios para la E2 (la app todavía no se conecta al backend).
// Son los mismos que cargará el DataSeeder del backend (sección 5.4).
object DatosPrueba {

    val categorias = listOf(
        Categoria(1, "Orfebrería", "Joyas en plata y piedras chilenas"),
        Categoria(2, "Cobre", "Piezas de cobre martillado y grabado"),
        Categoria(3, "Alfarería", "Greda de Pomaire y Quinchamalí"),
        Categoria(4, "Textil", "Tejidos a telar y a palillo")
    )

    val artesanias = listOf(
        // Orfebrería
        Artesania(1, 1, "Colgante Lapislázuli Cordillera", "Colgante de plata con piedra lapislázuli tallada a mano.", "Lapislázuli, plata 950", 28500, 1, "/uploads/art-1.jpg"),
        Artesania(2, 1, "Aros Lapislázuli Gota", "Aros de plata martillada con lapislázuli en forma de gota.", "Plata 950, lapislázuli", 19900, 4, "/uploads/art-2.jpg"),
        Artesania(3, 1, "Anillo Luna Mapuche", "Anillo ancho de plata martillada con una turquesa ovalada.", "Plata 950, turquesa", 24000, 0, "/uploads/art-3.jpg"),
        Artesania(4, 1, "Collar de Cuarzo", "Collar de cuarzo transparente y ahumado con cierre de plata.", "Cuarzo, plata 950", 32000, 3, "/uploads/art-4.jpg"),
        Artesania(5, 1, "Prendedor Trapelacucha", "Réplica de un prendedor tradicional mapuche.", "Alpaca, repujado", 45000, 2, "/uploads/art-5.jpg"),
        // Cobre
        Artesania(6, 2, "Paila de Cobre", "Paila tradicional para huevos o pebre.", "Cobre martillado", 14000, 5, "/uploads/art-6.jpg"),
        Artesania(7, 2, "Jarro Martillado", "Jarro para agua, martillado a mano.", "Cobre, estaño", 36000, 2, "/uploads/art-7.jpg"),
        Artesania(8, 2, "Bandeja Desierto", "Bandeja grabada con motivos del desierto de Atacama.", "Cobre grabado", 27500, 0, "/uploads/art-8.jpg"),
        Artesania(9, 2, "Candelero de Cobre", "Candelero martillado a mano, con asa para llevarlo.", "Cobre martillado", 12500, 4, "/uploads/art-9.jpg"),
        Artesania(10, 2, "Cuenco con Pátina", "Cuenco de cobre con pátina verde por fuera y brillo por dentro.", "Cobre, pátina natural", 9900, 5, "/uploads/art-10.jpg"),
        // Alfarería
        Artesania(11, 3, "Chanchito de la Suerte", "Chanchito de tres patas de Quinchamalí.", "Greda negra, incisión con caolín", 8500, 5, "/uploads/art-11.jpg"),
        Artesania(12, 3, "Fuente de Greda", "Fuente de Pomaire con asas, para el horno o la mesa.", "Greda de Pomaire", 15900, 3, "/uploads/art-12.jpg"),
        Artesania(13, 3, "Guitarrera de Quinchamalí", "Figura de mujer con guitarra, pieza típica de Ñuble.", "Greda negra pulida", 22000, 1, "/uploads/art-13.jpg"),
        Artesania(14, 3, "Jarro de Greda", "Jarro de greda roja para agua o chicha.", "Greda de Pomaire", 13500, 4, "/uploads/art-14.jpg"),
        Artesania(15, 3, "Figura de Talagante", "Figura de loza pintada a mano: mujer con canasto de flores.", "Loza policromada", 29000, 0, "/uploads/art-15.jpg"),
        // Textil
        Artesania(16, 4, "Camino de Mesa en Telar", "Camino de mesa tejido a telar con lana de oveja de Chiloé.", "Lana de oveja teñida, telar", 48000, 2, "/uploads/art-16.jpg"),
        Artesania(17, 4, "Chal de Alpaca", "Chal liviano en tonos tierra.", "Lana de alpaca, palillo", 39500, 3, "/uploads/art-17.jpg"),
        Artesania(18, 4, "Pie de Cama a Telar", "Pie de cama de lana con franjas, tejido a telar.", "Lana natural, telar", 18900, 5, "/uploads/art-18.jpg"),
        Artesania(19, 4, "Trarihue", "Faja mapuche tejida a telar con diseños geométricos.", "Lana, telar mapuche", 11900, 4, "/uploads/art-19.jpg"),
        Artesania(20, 4, "Alfombra Anudada", "Alfombra pequeña de lana anudada a mano con un rombo al centro.", "Lana teñida con tintes naturales", 54000, 1, "/uploads/art-20.jpg")
    )

    val usuarios = listOf(
        Usuario(1, "15.678.234-3", "Alejandra Torres", "admin@aleyleo.cl", "+56911112222", "Los Artesanos 450", "Ñuñoa", Rol.ADMIN),
        Usuario(2, "18.432.567-5", "Sofía Valenzuela", "sofia@correo.cl", "+56987654321", "Av. Siempre Viva 123", "Providencia", Rol.CLIENTE),
        Usuario(3, "19.876.543-0", "Matías Rojas", "matias@correo.cl", "+56923456789", "Pasaje Los Aromos 77", "Maipú", Rol.CLIENTE),
        Usuario(4, "20.345.678-6", "Camila Soto", "camila@correo.cl", "+56934567890", "Calle Larga 1020", "La Florida", Rol.CLIENTE)
    )

    // Contraseñas solo de desarrollo (email -> contraseña).
    val contrasenas = mapOf(
        "admin@aleyleo.cl" to "Admin123",
        "sofia@correo.cl" to "Cliente123",
        "matias@correo.cl" to "Cliente123",
        "camila@correo.cl" to "Cliente123"
    )

    // Un pedido en cada estado, para poder mostrar todas las pantallas.
    val pedidos = listOf(
        Pedido(1, "PED-2026-0001", 2, EstadoPedido.FINALIZADA, "Av. Siempre Viva 123", "Providencia", "", "2026-10-05 11:20",
            listOf(DetallePedido(6, "Paila de Cobre", 2, 14000))),
        Pedido(2, "PED-2026-0002", 3, EstadoPedido.EN_PREPARACION, "Pasaje Los Aromos 77", "Maipú", "Envolver para regalo", "2026-10-12 16:45",
            listOf(DetallePedido(16, "Camino de Mesa en Telar", 1, 48000))),
        Pedido(3, "PED-2026-0003", 4, EstadoPedido.RECIBIDA, "Calle Larga 1020", "La Florida", "", "2026-10-15 09:10",
            listOf(DetallePedido(2, "Aros Lapislázuli Gota", 1, 19900), DetallePedido(11, "Chanchito de la Suerte", 2, 8500))),
        Pedido(4, "PED-2026-0004", 2, EstadoPedido.PAGADA, "Av. Siempre Viva 123", "Providencia", "", "2026-10-18 20:05",
            listOf(DetallePedido(17, "Chal de Alpaca", 1, 39500))),
        Pedido(5, "PED-2026-0005", 3, EstadoPedido.CANCELADA, "Pasaje Los Aromos 77", "Maipú", "", "2026-10-19 13:30",
            listOf(DetallePedido(7, "Jarro Martillado", 1, 36000))),
        Pedido(6, "PED-2026-0006", 4, EstadoPedido.RECHAZADA, "Calle Larga 1020", "La Florida", "", "2026-10-20 10:00",
            listOf(DetallePedido(20, "Alfombra Anudada", 1, 54000))),
        Pedido(7, "PED-2026-0007", 2, EstadoPedido.GENERADA, "Av. Siempre Viva 123", "Providencia", "Envolver para regalo", "2026-10-21 18:30",
            listOf(DetallePedido(1, "Colgante Lapislázuli Cordillera", 1, 28500), DetallePedido(14, "Jarro de Greda", 2, 13500)))
    )
}
