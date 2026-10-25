package com.aleyleo.app

import com.aleyleo.app.model.DatosRegistro
import com.aleyleo.app.model.ResultadoAuth
import com.aleyleo.app.model.Rol
import com.aleyleo.app.repository.AuthRepository
import com.aleyleo.app.viewmodel.AuthViewModel
import com.aleyleo.app.viewmodel.FormularioRegistro
import com.aleyleo.app.viewmodel.validarRegistro
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Login (HU-02) y Registro (HU-01).
class AuthTest {

    @Before
    @After
    fun reiniciar() {
        AuthRepository.reiniciar()
    }

    private val datosValidos = DatosRegistro(
        rut = "10.000.013-K",
        nombreCompleto = "Pedro Pérez",
        email = "pedro@correo.cl",
        telefono = "+56955554444",
        direccion = "Calle Uno 1",
        comuna = "Santiago",
        password = "Secreta123"
    )

    @Test
    fun loginDevuelveElRolParaElegirElGrafo() {
        val vm = AuthViewModel()
        vm.cambiarEmail("admin@aleyleo.cl")
        vm.cambiarPassword("Admin123")
        assertEquals(Rol.ADMIN, vm.iniciarSesion())

        val vm2 = AuthViewModel()
        vm2.cambiarEmail("SOFIA@correo.cl") // el email no distingue mayúsculas
        vm2.cambiarPassword("Cliente123")
        assertEquals(Rol.CLIENTE, vm2.iniciarSesion())
    }

    @Test
    fun loginInvalidoMuestraMensajeGenerico() {
        val vm = AuthViewModel()
        vm.cambiarEmail("sofia@correo.cl")
        vm.cambiarPassword("equivocada")

        assertNull(vm.iniciarSesion())
        assertEquals("Email o contraseña incorrectos", vm.formulario.value.error)

        // Con un email que no existe, el mensaje es el mismo (no revela cuál falló).
        vm.cambiarEmail("nadie@correo.cl")
        vm.iniciarSesion()
        assertEquals("Email o contraseña incorrectos", vm.formulario.value.error)
    }

    @Test
    fun registroCreaClienteConSesionIniciadaYPuedeVolverAEntrar() {
        val resultado = AuthRepository.registrar(datosValidos)

        assertTrue(resultado is ResultadoAuth.Exito)
        assertEquals(Rol.CLIENTE, AuthRepository.sesion.value?.rol)
        assertEquals(5, AuthRepository.sesion.value?.id)

        AuthRepository.cerrarSesion()
        assertNotNull(AuthRepository.iniciarSesion("pedro@correo.cl", "Secreta123"))
    }

    @Test
    fun emailORutRepetidoDaError() {
        val emailRepetido = AuthRepository.registrar(datosValidos.copy(email = "SOFIA@correo.cl"))
        assertEquals("Ya existe una cuenta con ese email", (emailRepetido as ResultadoAuth.Error).mensaje)

        val rutRepetido = AuthRepository.registrar(datosValidos.copy(rut = "184325675")) // RUT de Sofía sin puntos
        assertEquals("Ya existe una cuenta con ese RUT", (rutRepetido as ResultadoAuth.Error).mensaje)
    }

    @Test
    fun formularioDeRegistroValidaTodosLosCampos() {
        val vacio = validarRegistro(FormularioRegistro())
        assertTrue(vacio.hayErrores)
        assertNotNull(vacio.rut)
        assertNotNull(vacio.email)
        assertNotNull(vacio.confirmacion)

        val f = FormularioRegistro(
            rut = "10.000.013-K", nombre = "Pedro Pérez", email = "pedro@correo.cl",
            telefono = "+56955554444", direccion = "Calle Uno 1", comuna = "Santiago",
            password = "Secreta123", confirmacion = "Secreta124"
        )
        assertEquals("Las contraseñas no coinciden", validarRegistro(f).confirmacion)
        assertFalse(validarRegistro(f.copy(confirmacion = "Secreta123")).hayErrores)
    }
}
