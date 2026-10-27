package com.aleyleo.app

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.ResultadoCatalogo
import com.aleyleo.app.repository.CatalogoRepository
import com.aleyleo.app.util.validarImagen
import com.aleyleo.app.util.validarPrecio
import com.aleyleo.app.util.validarStock
import com.aleyleo.app.viewmodel.FormularioProducto
import com.aleyleo.app.viewmodel.filtrarCatalogo
import com.aleyleo.app.viewmodel.filtrarProductosAdmin
import com.aleyleo.app.viewmodel.validarProducto
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Reglas de HU-16 (productos) y HU-19 (categorías) de la admin.
class AdminCatalogoTest {

    @Before
    @After
    fun reiniciar() {
        CatalogoRepository.reiniciar()
    }

    private val productoValido = FormularioProducto(
        nombre = "Vasija Pomaire", categoriaId = 3, descripcion = "Vasija de greda",
        materiales = "Greda", precio = "12000", stock = "0"
    )

    @Test
    fun formularioValidoNoTieneErrores() {
        assertFalse(validarProducto(productoValido).hayErrores) // stock 0 está permitido
    }

    @Test
    fun camposObligatoriosPrecioYStock() {
        val vacio = validarProducto(FormularioProducto())
        assertNotNull(vacio.errorNombre)
        assertNotNull(vacio.errorCategoria)
        assertNotNull(vacio.errorDescripcion)
        assertNotNull(vacio.errorMateriales)

        assertNotNull(validarPrecio("0"))
        assertNotNull(validarPrecio(""))
        assertNull(validarPrecio("990"))
        assertNotNull(validarStock("-1"))
        assertNull(validarStock("0"))
    }

    @Test
    fun imagenSoloJpgOPngDeHasta5MB() {
        assertNull(validarImagen("image/jpeg", 1_000_000))
        assertNull(validarImagen("image/png", 5L * 1024 * 1024))
        assertNotNull(validarImagen("image/gif", 1_000))
        assertNotNull(validarImagen("image/jpeg", 5L * 1024 * 1024 + 1))
    }

    @Test
    fun crearYEditarArtesania() {
        val nueva = DatosPrueba.artesanias[0].copy(id = 0, nombre = "Nueva pieza")
        val resultado = CatalogoRepository.guardarArtesania(nueva) as ResultadoCatalogo.Exito
        assertEquals(21, resultado.id)
        assertEquals("Nueva pieza", CatalogoRepository.obtenerArtesania(21)!!.nombre)

        val editada = CatalogoRepository.obtenerArtesania(6)!!.copy(precioBase = 15000)
        CatalogoRepository.guardarArtesania(editada)
        assertEquals(15000, CatalogoRepository.obtenerArtesania(6)!!.precioBase)
    }

    @Test
    fun desactivarLaOcultaDelCatalogoPeroNoLaBorra() {
        CatalogoRepository.cambiarActivoArtesania(6, false)

        val artesanias = CatalogoRepository.artesanias.value
        val categorias = CatalogoRepository.categorias.value
        assertTrue(filtrarCatalogo(artesanias, categorias, null, "").none { it.id == 6 }) // cliente no la ve
        assertTrue(filtrarProductosAdmin(artesanias, "").any { it.id == 6 })            // admin sí
        assertEquals(20, artesanias.size)
    }

    @Test
    fun nombreDeCategoriaObligatorioYUnico() {
        assertTrue(CatalogoRepository.guardarCategoria(0, "  ", "") is ResultadoCatalogo.Error)
        assertTrue(CatalogoRepository.guardarCategoria(0, "cobre", "") is ResultadoCatalogo.Error) // repetida
        assertTrue(CatalogoRepository.guardarCategoria(0, "Cestería", "") is ResultadoCatalogo.Exito)
        // Editar manteniendo su propio nombre sí se puede.
        assertTrue(CatalogoRepository.guardarCategoria(2, "Cobre", "Nueva descripción") is ResultadoCatalogo.Exito)
    }

    @Test
    fun noSeDesactivaUnaCategoriaConArtesaniasActivas() {
        val resultado = CatalogoRepository.cambiarActivoCategoria(2, false)
        assertTrue(resultado is ResultadoCatalogo.Error)
        assertTrue((resultado as ResultadoCatalogo.Error).mensaje.contains("5 artesanía"))
        assertTrue(CatalogoRepository.obtenerCategoria(2)!!.activo)

        // Si se desactivan sus 5 artesanías, ahora sí se puede.
        (6..10).forEach { CatalogoRepository.cambiarActivoArtesania(it, false) }
        assertTrue(CatalogoRepository.cambiarActivoCategoria(2, false) is ResultadoCatalogo.Exito)
    }
}
