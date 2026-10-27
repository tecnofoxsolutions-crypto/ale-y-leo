package com.aleyleo.api.categoria

import com.aleyleo.api.categoria.dto.CategoriaRequest
import com.aleyleo.api.categoria.dto.CategoriaResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

// Endpoint 6 (público): las categorías que ve el cliente.
@RestController
@RequestMapping("/api/categorias")
class CategoriaController(private val categoriaService: CategoriaService) {

    @GetMapping
    fun listar(): List<CategoriaResponse> = categoriaService.listarActivas()
}

// Endpoints 14 a 17 (solo ADMIN, lo exige SecurityConfig por la ruta /api/admin/**).
@RestController
@RequestMapping("/api/admin/categorias")
class AdminCategoriaController(private val categoriaService: CategoriaService) {

    @GetMapping
    fun listar(): List<CategoriaResponse> = categoriaService.listarTodas()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody datos: CategoriaRequest): CategoriaResponse = categoriaService.crear(datos)

    @PutMapping("/{id}")
    fun editar(@PathVariable id: Long, @Valid @RequestBody datos: CategoriaRequest): CategoriaResponse =
        categoriaService.editar(id, datos)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: sin cuerpo
    fun desactivar(@PathVariable id: Long) = categoriaService.desactivar(id)
}
