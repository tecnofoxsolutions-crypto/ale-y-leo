package com.aleyleo.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.aleyleo.app.model.Artesania
import com.aleyleo.app.model.Categoria
import com.aleyleo.app.model.ResultadoCatalogo
import com.aleyleo.app.repository.CatalogoRepository
import com.aleyleo.app.util.tamanoArchivo
import com.aleyleo.app.util.validarImagen
import com.aleyleo.app.util.validarObligatorio
import com.aleyleo.app.util.validarPrecio
import com.aleyleo.app.util.validarStock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Formulario de P18. Precio y stock se guardan como texto mientras se escriben.
data class FormularioProducto(
    val id: Int = 0, // 0 = producto nuevo
    val nombre: String = "",
    val categoriaId: Int? = null,
    val descripcion: String = "",
    val materiales: String = "",
    val precio: String = "",
    val stock: String = "",
    val imagenUrl: String = "",
    val activo: Boolean = true,
    val errorNombre: String? = null,
    val errorCategoria: String? = null,
    val errorDescripcion: String? = null,
    val errorMateriales: String? = null,
    val errorPrecio: String? = null,
    val errorStock: String? = null,
    val errorImagen: String? = null,
    val errorGeneral: String? = null
) {
    val hayErrores: Boolean
        get() = listOf(errorNombre, errorCategoria, errorDescripcion, errorMateriales, errorPrecio, errorStock)
            .any { it != null }
}

// ViewModel de P18 Admin: formulario de producto (HU-16).
// AndroidViewModel porque necesita el contentResolver para revisar la imagen elegida.
class FormProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CatalogoRepository

    private val _formulario = MutableStateFlow(FormularioProducto())
    val formulario: StateFlow<FormularioProducto> = _formulario.asStateFlow()

    val categorias: StateFlow<List<Categoria>> = repository.categorias

    private var cargado = false

    // Llena el formulario con la artesanía a editar. Solo la primera vez, para no
    // borrar lo que la admin escribió si la pantalla se vuelve a dibujar.
    fun cargar(artesaniaId: Int) {
        if (cargado) return
        cargado = true
        val artesania = repository.obtenerArtesania(artesaniaId) ?: return
        _formulario.value = FormularioProducto(
            id = artesania.id,
            nombre = artesania.nombre,
            categoriaId = artesania.categoriaId,
            descripcion = artesania.descripcion,
            materiales = artesania.materialesTecnica,
            precio = artesania.precioBase.toString(),
            stock = artesania.stock.toString(),
            imagenUrl = artesania.imagenUrl,
            activo = artesania.activo
        )
    }

    fun cambiarNombre(texto: String) = _formulario.update { it.copy(nombre = texto, errorNombre = null) }
    fun cambiarCategoria(id: Int) = _formulario.update { it.copy(categoriaId = id, errorCategoria = null) }
    fun cambiarDescripcion(texto: String) = _formulario.update { it.copy(descripcion = texto, errorDescripcion = null) }
    fun cambiarMateriales(texto: String) = _formulario.update { it.copy(materiales = texto, errorMateriales = null) }
    fun cambiarPrecio(texto: String) = _formulario.update { it.copy(precio = texto.filter(Char::isDigit), errorPrecio = null) }
    fun cambiarStock(texto: String) = _formulario.update { it.copy(stock = texto.filter(Char::isDigit), errorStock = null) }

    // La imagen elegida en la galería: solo se acepta JPG o PNG de hasta 5 MB.
    fun elegirImagen(uri: Uri) {
        val contexto = getApplication<Application>()
        val tipo = contexto.contentResolver.getType(uri)
        val error = validarImagen(tipo, tamanoArchivo(contexto, uri))
        _formulario.update {
            if (error == null) it.copy(imagenUrl = uri.toString(), errorImagen = null)
            else it.copy(errorImagen = error) // se mantiene la imagen anterior
        }
    }

    // Valida y guarda. Devuelve true si se guardó (la pantalla vuelve a la lista).
    fun guardar(): Boolean {
        val f = validarProducto(_formulario.value)
        _formulario.value = f
        if (f.hayErrores) return false

        val artesania = Artesania(
            id = f.id,
            categoriaId = f.categoriaId!!,
            nombre = f.nombre.trim(),
            descripcion = f.descripcion.trim(),
            materialesTecnica = f.materiales.trim(),
            precioBase = f.precio.toInt(),
            stock = f.stock.toInt(),
            imagenUrl = f.imagenUrl,
            activo = f.activo
        )
        return when (val resultado = repository.guardarArtesania(artesania)) {
            is ResultadoCatalogo.Exito -> true
            is ResultadoCatalogo.Error -> {
                _formulario.update { it.copy(errorGeneral = resultado.mensaje) }
                false
            }
        }
    }

    // Desactivar la oculta del catálogo sin borrarla; reactivar la vuelve a mostrar.
    fun cambiarActivo() {
        val f = _formulario.value
        if (f.id == 0) return
        if (repository.cambiarActivoArtesania(f.id, !f.activo) is ResultadoCatalogo.Exito) {
            _formulario.update { it.copy(activo = !f.activo) }
        }
    }
}

// Reglas de HU-16: nombre, categoría, descripción y materiales obligatorios; precio > 0; stock >= 0.
fun validarProducto(f: FormularioProducto): FormularioProducto {
    return f.copy(
        errorNombre = validarObligatorio(f.nombre, "El nombre es obligatorio"),
        errorCategoria = if (f.categoriaId == null) "Elige una categoría" else null,
        errorDescripcion = validarObligatorio(f.descripcion, "La descripción es obligatoria"),
        errorMateriales = validarObligatorio(f.materiales, "Indica los materiales o la técnica"),
        errorPrecio = validarPrecio(f.precio),
        errorStock = validarStock(f.stock),
        errorGeneral = null
    )
}
