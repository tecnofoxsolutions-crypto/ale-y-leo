package com.aleyleo.app

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.util.formatoClp
import com.aleyleo.app.viewmodel.filtrarCatalogo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas del formato de precios y de los filtros del catálogo (HU-04 y HU-05).
class CatalogoFiltroTest {

    private val artesanias = DatosPrueba.artesanias
    private val categorias = DatosPrueba.categorias

    @Test
    fun formatoClpUsaPuntoDeMiles() {
        assertEquals("$28.500", formatoClp(28500))
        assertEquals("$990", formatoClp(990))
        assertEquals("$1.000.000", formatoClp(1000000))
    }

    @Test
    fun sinFiltrosMuestraLas20() {
        assertEquals(20, filtrarCatalogo(artesanias, categorias, null, "").size)
    }

    @Test
    fun chipDeCategoriaMuestraSoloEsaCategoria() {
        val resultado = filtrarCatalogo(artesanias, categorias, 2, "")
        assertEquals(5, resultado.size)
        assertTrue(resultado.all { it.categoriaId == 2 })
    }

    @Test
    fun busquedaNoDistingueMayusculasYMiraNombreDescripcionYMateriales() {
        // "PLATA" está en los materiales de varias piezas de orfebrería
        assertTrue(filtrarCatalogo(artesanias, categorias, null, "PLATA").isNotEmpty())
        // "chiloé" está en la descripción del Camino de Mesa en Telar
        assertEquals("Camino de Mesa en Telar", filtrarCatalogo(artesanias, categorias, null, "chiloé").single().nombre)
        // "paila" está en el nombre
        assertEquals(6, filtrarCatalogo(artesanias, categorias, null, "paila").single().id)
    }

    @Test
    fun busquedaSeCombinaConElChip() {
        // "lana" aparece en Textil; dentro de Cobre no debe haber resultados
        assertTrue(filtrarCatalogo(artesanias, categorias, 4, "lana").isNotEmpty())
        assertTrue(filtrarCatalogo(artesanias, categorias, 2, "lana").isEmpty())
    }

    @Test
    fun noMuestraArtesaniasInactivasNiDeCategoriasInactivas() {
        val conInactiva = artesanias.map { if (it.id == 1) it.copy(activo = false) else it }
        assertTrue(filtrarCatalogo(conInactiva, categorias, null, "").none { it.id == 1 })

        val sinTextil = categorias.filter { it.id != 4 }
        assertTrue(filtrarCatalogo(artesanias, sinTextil, null, "").none { it.categoriaId == 4 })
    }
}
