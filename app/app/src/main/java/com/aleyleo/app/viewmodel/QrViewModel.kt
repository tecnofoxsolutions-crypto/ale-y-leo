package com.aleyleo.app.viewmodel

import androidx.lifecycle.ViewModel
import com.aleyleo.app.repository.CatalogoRepository
import com.aleyleo.app.util.leerIdDeQr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Qué hacer con un QR leído: abrir el detalle de una artesanía o mostrar un error.
sealed class ResultadoQr {
    data class AbrirArtesania(val id: Int) : ResultadoQr()
    data class Error(val mensaje: String) : ResultadoQr()
}

// ViewModel del lector de QR (AP-26), como el QrViewModel visto en clase.
// La cámara la abre la pantalla con Google Code Scanner; aquí solo se interpreta el contenido.
class QrViewModel : ViewModel() {

    // Último contenido leído (útil para depurar o mostrarlo en pantalla).
    private val _contenidoQR = MutableStateFlow("")
    val contenidoQR: StateFlow<String> = _contenidoQR.asStateFlow()

    fun guardarQR(contenido: String): ResultadoQr {
        _contenidoQR.value = contenido
        return interpretarQr(contenido)
    }
}

// Un QR sirve solo si tiene el formato de Ale y Leo y la artesanía existe y está activa.
fun interpretarQr(contenido: String): ResultadoQr {
    val id = leerIdDeQr(contenido)
        ?: return ResultadoQr.Error("Este código QR no es de Ale y Leo")

    val artesania = CatalogoRepository.obtenerArtesania(id)
    val categoria = artesania?.let { CatalogoRepository.obtenerCategoria(it.categoriaId) }
    if (artesania == null || !artesania.activo || categoria?.activo != true) {
        return ResultadoQr.Error("Esta artesanía ya no está disponible")
    }
    return ResultadoQr.AbrirArtesania(id)
}
