package com.aleyleo.api.categoria

import org.springframework.data.jpa.repository.JpaRepository

interface CategoriaRepository : JpaRepository<Categoria, Long> {

    // GET /api/categorias: solo las activas, para el cliente.
    fun findByActivoTrueOrderByNombreAsc(): List<Categoria>

    // GET /api/admin/categorias: todas, también las inactivas.
    fun findAllByOrderByNombreAsc(): List<Categoria>

    // El nombre es único: al crear se revisa contra todas...
    fun existsByNombreIgnoreCase(nombre: String): Boolean

    // ...y al editar, contra todas menos la misma categoría.
    fun existsByNombreIgnoreCaseAndIdNot(nombre: String, id: Long): Boolean
}
