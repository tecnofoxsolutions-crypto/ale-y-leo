package com.aleyleo.app

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.repository.AuthRepository
import com.aleyleo.app.util.formatoFecha
import com.aleyleo.app.util.validarEmail
import com.aleyleo.app.util.validarNombre
import com.aleyleo.app.util.validarPassword
import com.aleyleo.app.util.validarRut
import com.aleyleo.app.util.validarTelefono
import com.aleyleo.app.viewmodel.filtrarMisPedidos
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// Validadores (HU-01 y HU-03), Mis pedidos (HU-12) y edición del perfil.
class PerfilTest {

    @After
    fun limpiarSesion() {
        AuthRepository.cerrarSesion()
    }

    @Test
    fun rutValidoConYSinPuntos() {
        assertNull(validarRut("18.432.567-5"))
        assertNull(validarRut("184325675"))
        assertNotNull(validarRut("18.432.567-4")) // dígito verificador incorrecto
        assertNotNull(validarRut(""))
    }

    @Test
    fun rutConDigitoK() {
        assertNull(validarRut("10.000.013-K"))
        assertNull(validarRut("10000013k")) // minúscula también vale
    }

    @Test
    fun telefonoCelularChileno() {
        assertNull(validarTelefono("+56987654321"))
        assertNull(validarTelefono("987654321"))
        assertNull(validarTelefono("+56 9 8765 4321"))
        assertNotNull(validarTelefono("22345678")) // fijo, no celular
        assertNotNull(validarTelefono(""))
    }

    @Test
    fun emailNombreYPassword() {
        assertNull(validarEmail("sofia@correo.cl"))
        assertNotNull(validarEmail("sofia@correo"))
        assertNotNull(validarNombre("  "))
        assertNotNull(validarPassword("1234567"))
        assertNull(validarPassword("Secreta123"))
    }

    @Test
    fun misPedidosSoloDelClienteYDelMasNuevoAlMasAntiguo() {
        val deSofia = filtrarMisPedidos(DatosPrueba.pedidos, usuarioId = 2)
        assertEquals(listOf(7, 4, 1), deSofia.map { it.id })
        assertEquals(emptyList<Int>(), filtrarMisPedidos(DatosPrueba.pedidos, null).map { it.id })
    }

    @Test
    fun fechaEnFormatoChileno() {
        assertEquals("21/10/2026 18:30", formatoFecha("2026-10-21 18:30"))
    }

    @Test
    fun actualizarPerfilCambiaLosDatosPeroNoElEmailNiElRut() {
        AuthRepository.iniciarSesion("camila@correo.cl", "Cliente123")

        val actualizado = AuthRepository.actualizarPerfil("Camila Soto Pérez", "+56 9 1111 2222", "Nueva 45", "Macul")

        assertEquals("Camila Soto Pérez", actualizado!!.nombreCompleto)
        assertEquals("+56911112222", actualizado.telefono)
        assertEquals("camila@correo.cl", actualizado.email)
        assertEquals("20.345.678-6", actualizado.rut)
        assertEquals(actualizado, AuthRepository.sesion.value)
    }
}
