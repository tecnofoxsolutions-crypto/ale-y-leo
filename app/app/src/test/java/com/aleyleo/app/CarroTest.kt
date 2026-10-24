package com.aleyleo.app

import com.aleyleo.app.model.ItemCarro
import com.aleyleo.app.viewmodel.calcularNuevaCantidad
import org.junit.Assert.assertEquals
import org.junit.Test

// Reglas del carro (HU-08 y HU-09).
class CarroTest {

    @Test
    fun productoNuevoSeAgregaConLaCantidadElegida() {
        assertEquals(2, calcularNuevaCantidad(enCarro = 0, agregar = 2, stock = 5))
    }

    @Test
    fun siYaEstaEnElCarroSeSumaLaCantidad() {
        assertEquals(4, calcularNuevaCantidad(enCarro = 1, agregar = 3, stock = 5))
    }

    @Test
    fun nuncaSuperaElStock() {
        assertEquals(3, calcularNuevaCantidad(enCarro = 2, agregar = 4, stock = 3))
        assertEquals(1, calcularNuevaCantidad(enCarro = 1, agregar = 1, stock = 1))
    }

    @Test
    fun subtotalEsCantidadPorPrecio() {
        val item = ItemCarro(6, 2, "Paila de Cobre", 14000, cantidad = 3, stockConocido = 5)
        assertEquals(42000, item.subtotal)
    }
}
