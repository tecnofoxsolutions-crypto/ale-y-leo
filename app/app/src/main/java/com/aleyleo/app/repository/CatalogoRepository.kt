package com.aleyleo.app.repository

import com.aleyleo.app.datos.DatosPrueba
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.model.ResultadoCatalogo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Repositorio del catálogo. En la E2 lee de DatosPrueba (en memoria);
// en la entrega final, por dentro llamará a la API con Retrofit.
// Es un "object" para que todas las pantallas compartan los mismos datos.
object CatalogoRepository {

    private val _categorias = MutableStateFlow(DatosPrueba.categorias)
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    private val _artesanias = MutableStateFlow(DatosPrueba.artesanias)
    val artesanias: StateFlow<List<Artesania>> = _artesanias.asStateFlow()

    fun obtenerArtesania(id: Int): Artesania? {
        return _artesanias.value.find { it.id == id }
    }

    fun obtenerCategoria(id: Int): Categoria? {
        return _categorias.value.find { it.id == id }
    }

    // Lo usa el pago de un pedido. update reemplaza la lista y avisa a todas las pantallas.
    fun descontarStock(artesaniaId: Int, cantidad: Int) {
        _artesanias.update { lista ->
            lista.map { if (it.id == artesaniaId) it.copy(stock = it.stock - cantidad) else it }
        }
    }

    // Lo usa la admin al rechazar un pedido ya pagado: devuelve las unidades al stock.
    fun reponerStock(artesaniaId: Int, cantidad: Int) {
        _artesanias.update { lista ->
            lista.map { if (it.id == artesaniaId) it.copy(stock = it.stock + cantidad) else it }
        }
    }

    // ---------- Admin: artesanías (HU-16) ----------

    // POST /api/admin/artesanias (id 0) o PUT /api/admin/artesanias/{id}.
    // Los campos ya vienen validados por el ViewModel.
    fun guardarArtesania(artesania: Artesania): ResultadoCatalogo {
        if (obtenerCategoria(artesania.categoriaId) == null) {
            return ResultadoCatalogo.Error("La categoría no existe")
        }
        if (artesania.id == 0) {
            val nueva = artesania.copy(id = (_artesanias.value.maxOfOrNull { it.id } ?: 0) + 1)
            _artesanias.update { it + nueva }
            return ResultadoCatalogo.Exito(nueva.id)
        }
        if (obtenerArtesania(artesania.id) == null) {
            return ResultadoCatalogo.Error("Artesanía no encontrada")
        }
        _artesanias.update { lista -> lista.map { if (it.id == artesania.id) artesania else it } }
        return ResultadoCatalogo.Exito(artesania.id)
    }

    // Borrado lógico: inactiva = oculta del catálogo, pero sigue existiendo.
    fun cambiarActivoArtesania(id: Int, activo: Boolean): ResultadoCatalogo {
        if (obtenerArtesania(id) == null) return ResultadoCatalogo.Error("Artesanía no encontrada")
        _artesanias.update { lista -> lista.map { if (it.id == id) it.copy(activo = activo) else it } }
        return ResultadoCatalogo.Exito(id)
    }

    // ---------- Admin: categorías (HU-19) ----------

    // Crear (id 0) o editar. El nombre es obligatorio y no se puede repetir.
    fun guardarCategoria(id: Int, nombre: String, descripcion: String): ResultadoCatalogo {
        val limpio = nombre.trim()
        if (limpio.isEmpty()) return ResultadoCatalogo.Error("El nombre es obligatorio")

        val repetida = _categorias.value.any { it.id != id && it.nombre.equals(limpio, ignoreCase = true) }
        if (repetida) return ResultadoCatalogo.Error("Ya existe una categoría llamada \"$limpio\"")

        if (id == 0) {
            val nueva = Categoria((_categorias.value.maxOfOrNull { it.id } ?: 0) + 1, limpio, descripcion.trim())
            _categorias.update { it + nueva }
            return ResultadoCatalogo.Exito(nueva.id)
        }
        if (obtenerCategoria(id) == null) return ResultadoCatalogo.Error("Categoría no encontrada")
        _categorias.update { lista ->
            lista.map { if (it.id == id) it.copy(nombre = limpio, descripcion = descripcion.trim()) else it }
        }
        return ResultadoCatalogo.Exito(id)
    }

    // No se puede desactivar una categoría que todavía tiene artesanías activas (en el backend, 409).
    fun cambiarActivoCategoria(id: Int, activo: Boolean): ResultadoCatalogo {
        if (obtenerCategoria(id) == null) return ResultadoCatalogo.Error("Categoría no encontrada")

        if (!activo) {
            val activas = artesaniasActivasEn(id)
            if (activas > 0) {
                return ResultadoCatalogo.Error(
                    "No se puede desactivar: tiene $activas artesanía(s) activa(s). Desactívalas o cámbialas de categoría primero."
                )
            }
        }
        _categorias.update { lista -> lista.map { if (it.id == id) it.copy(activo = activo) else it } }
        return ResultadoCatalogo.Exito(id)
    }

    fun artesaniasActivasEn(categoriaId: Int): Int {
        return _artesanias.value.count { it.categoriaId == categoriaId && it.activo }
    }

    // Vuelve a los datos iniciales (lo usan las pruebas unitarias).
    fun reiniciar() {
        _categorias.value = DatosPrueba.categorias
        _artesanias.value = DatosPrueba.artesanias
    }
}
