package com.aleyleo.api

import com.aleyleo.api.artesania.Artesania
import com.aleyleo.api.artesania.ArtesaniaRepository
import com.aleyleo.api.categoria.Categoria
import com.aleyleo.api.categoria.CategoriaRepository
import com.aleyleo.api.config.JwtService
import com.aleyleo.api.usuario.Rol
import com.aleyleo.api.usuario.Usuario
import com.aleyleo.api.usuario.UsuarioRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.transaction.annotation.Transactional

// BE-12: subir imágenes (endpoints 5 y 22) y verlas en /uploads (endpoint 27).
// Las pruebas guardan en build/test-uploads para no ensuciar la carpeta "uploads" real.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = ["almacenamiento.carpeta=build/test-uploads"])
class ImagenesTest {

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var json: ObjectMapper
    @Autowired lateinit var jwtService: JwtService
    @Autowired lateinit var categorias: CategoriaRepository
    @Autowired lateinit var artesanias: ArtesaniaRepository
    @Autowired lateinit var usuarios: UsuarioRepository

    private val admin by lazy { "Bearer " + jwtService.generarToken(1, "admin@aleyleo.cl", Rol.ADMIN) }
    private lateinit var artesania: Artesania

    // Imágenes mínimas: lo que importa son los primeros bytes (la "firma" del formato).
    private val png = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 1, 2, 3)
    private val jpg = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 4, 5, 6)
    private val gif = "GIF89a....".toByteArray()

    @BeforeEach
    fun datos() {
        val categoria = categorias.save(Categoria(nombre = "ZZ Imágenes"))
        artesania = artesanias.save(
            Artesania(categoria = categoria, nombre = "ZZ Con foto", descripcion = "x", materialesTecnica = "x", precioBase = 1000, stock = 1)
        )
    }

    private fun subir(url: String, token: String, bytes: ByteArray, nombre: String = "foto.png", campo: String = "archivo") =
        mvc.multipart(url) {
            file(MockMultipartFile(campo, nombre, "image/png", bytes))
            header("Authorization", token)
        }

    @Test
    fun seSubeUnaImagenYSeVeEnElNavegador() {
        val respuesta = subir("/api/admin/artesanias/${artesania.id}/imagen", admin, png).andExpect {
            status { isOk() }
            jsonPath("$.imagenUrl") { value("/uploads/art-${artesania.id}.png") }
        }.andReturn().response.contentAsString
        val url = json.readTree(respuesta)["imagenUrl"].asText()

        // GET /uploads/... es público y devuelve el mismo archivo.
        val bajada = mvc.get(url).andExpect { status { isOk() } }.andReturn().response.contentAsByteArray
        assertArrayEquals(png, bajada)
    }

    @Test
    fun reemplazarLaImagenCambiaElArchivo() {
        subir("/api/admin/artesanias/${artesania.id}/imagen", admin, png)
        subir("/api/admin/artesanias/${artesania.id}/imagen", admin, jpg, "foto.jpg").andExpect {
            jsonPath("$.imagenUrl") { value("/uploads/art-${artesania.id}.jpg") }
        }
        // La versión anterior (.png) ya no existe.
        mvc.get("/uploads/art-${artesania.id}.png").andExpect { status { isNotFound() } }
    }

    @Test
    fun soloJpgOPngDeHasta5MB() {
        // Un GIF renombrado como .png no pasa: se revisa el contenido, no el nombre.
        subir("/api/admin/artesanias/${artesania.id}/imagen", admin, gif).andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("ARCHIVO_INVALIDO") }
            jsonPath("$.mensaje") { value("La imagen debe ser JPG o PNG") }
        }
        val grande = png + ByteArray(5 * 1024 * 1024)
        subir("/api/admin/artesanias/${artesania.id}/imagen", admin, grande).andExpect {
            status { isBadRequest() }
            jsonPath("$.mensaje") { value("La imagen pesa más de 5 MB") }
        }
        // Sin el campo "archivo".
        subir("/api/admin/artesanias/${artesania.id}/imagen", admin, png, campo = "otro").andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("ARCHIVO_INVALIDO") }
        }
    }

    @Test
    fun soloLaAdminSubeImagenesDeProductos() {
        val cliente = "Bearer " + jwtService.generarToken(2, "sofia@correo.cl", Rol.CLIENTE)
        subir("/api/admin/artesanias/${artesania.id}/imagen", cliente, png).andExpect { status { isForbidden() } }
    }

    @Test
    fun fotoDePerfil() {
        val usuario = usuarios.save(
            Usuario(rut = "77.000.009-9", nombreCompleto = "ZZ Foto", email = "zz.foto@correo.cl", telefono = "+56911111111",
                direccion = "Calle 1", comuna = "Santiago", passwordHash = "x")
        )
        val token = "Bearer " + jwtService.generarToken(usuario.id!!, usuario.email, Rol.CLIENTE)

        subir("/api/usuarios/me/foto", token, jpg, "yo.jpg").andExpect {
            status { isOk() }
            jsonPath("$.fotoUrl") { value("/uploads/usuario-${usuario.id}.jpg") }
        }
        assertEquals("/uploads/usuario-${usuario.id}.jpg", usuarios.findById(usuario.id!!).get().fotoUrl)
    }
}
