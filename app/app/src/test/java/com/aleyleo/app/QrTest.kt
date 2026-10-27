package com.aleyleo.app

import com.aleyleo.app.repository.CatalogoRepository
import com.aleyleo.app.util.leerIdDeQr
import com.aleyleo.app.util.textoQrArtesania
import com.aleyleo.app.viewmodel.ResultadoQr
import com.aleyleo.app.viewmodel.interpretarQr
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Lector de QR (AP-26): qué hace la app con el texto que devuelve la cámara.
class QrTest {

    @Before
    @After
    fun reiniciar() {
        CatalogoRepository.reiniciar()
    }

    @Test
    fun formatoDelQr() {
        assertEquals("aleyleo:artesania:6", textoQrArtesania(6))
        assertEquals(6, leerIdDeQr("aleyleo:artesania:6"))
        assertEquals(16, leerIdDeQr("  ALEYLEO:artesania:16 "))
    }

    @Test
    fun qrAjenoOMalFormadoNoTieneId() {
        assertNull(leerIdDeQr("https://www.google.cl"))
        assertNull(leerIdDeQr("aleyleo:artesania:"))
        assertNull(leerIdDeQr("aleyleo:artesania:abc"))
        assertNull(leerIdDeQr("aleyleo:artesania:0"))
        assertNull(leerIdDeQr(""))
    }

    @Test
    fun qrValidoAbreLaArtesania() {
        assertEquals(ResultadoQr.AbrirArtesania(6), interpretarQr("aleyleo:artesania:6"))
    }

    @Test
    fun qrDeOtraCosaDaError() {
        val resultado = interpretarQr("https://www.google.cl")
        assertTrue(resultado is ResultadoQr.Error)
        assertEquals("Este código QR no es de Ale y Leo", (resultado as ResultadoQr.Error).mensaje)
    }

    @Test
    fun artesaniaInexistenteOInactivaDaError() {
        assertTrue(interpretarQr("aleyleo:artesania:999") is ResultadoQr.Error)

        CatalogoRepository.cambiarActivoArtesania(6, false)
        assertTrue(interpretarQr("aleyleo:artesania:6") is ResultadoQr.Error)
    }
}
