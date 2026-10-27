package com.aleyleo.api.artesania

import com.aleyleo.api.artesania.dto.ArtesaniaRequest
import com.aleyleo.api.artesania.dto.ArtesaniaResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

// Endpoints 7 y 8 (públicos): el catálogo del cliente.
// Ejemplo: GET /api/artesanias?categoriaId=2&q=paila
@RestController
@RequestMapping("/api/artesanias")
class ArtesaniaController(private val artesaniaService: ArtesaniaService) {

    @GetMapping
    fun buscar(
        @RequestParam(required = false) categoriaId: Long?,
        @RequestParam(required = false) q: String?
    ): List<ArtesaniaResponse> = artesaniaService.buscar(categoriaId, q)

    @GetMapping("/{id}")
    fun obtener(@PathVariable id: Long): ArtesaniaResponse = artesaniaService.obtenerPublica(id)
}

// Endpoints 18 a 21 (solo ADMIN).
@RestController
@RequestMapping("/api/admin/artesanias")
class AdminArtesaniaController(private val artesaniaService: ArtesaniaService) {

    @GetMapping
    fun listar(): List<ArtesaniaResponse> = artesaniaService.listarTodas()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody datos: ArtesaniaRequest): ArtesaniaResponse = artesaniaService.crear(datos)

    @PutMapping("/{id}")
    fun editar(@PathVariable id: Long, @Valid @RequestBody datos: ArtesaniaRequest): ArtesaniaResponse =
        artesaniaService.editar(id, datos)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun desactivar(@PathVariable id: Long) = artesaniaService.desactivar(id)

    // Endpoint 22: multipart/form-data con el campo "archivo" (JPG o PNG, máximo 5 MB).
    @PostMapping("/{id}/imagen")
    fun subirImagen(@PathVariable id: Long, @RequestPart("archivo") archivo: MultipartFile): ArtesaniaResponse =
        artesaniaService.subirImagen(id, archivo)
}
