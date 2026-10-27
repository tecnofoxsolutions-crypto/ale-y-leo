package com.aleyleo.api.artesania

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArtesaniaRepository : JpaRepository<Artesania, Long> {

    // GET /api/artesanias?categoriaId=&q= (HU-04 y HU-05).
    // Solo artesanías activas de categorías activas. Los dos filtros son opcionales (null = sin filtro).
    // El texto busca en nombre, descripción o materiales, sin distinguir mayúsculas.
    // "join fetch" trae la categoría en la misma consulta (evita una consulta extra por artesanía).
    @Query(
        """
        SELECT a FROM Artesania a JOIN FETCH a.categoria c
        WHERE a.activo = true AND c.activo = true
          AND (:categoriaId IS NULL OR c.id = :categoriaId)
          AND (:texto IS NULL
               OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(a.materialesTecnica) LIKE LOWER(CONCAT('%', :texto, '%')))
        ORDER BY a.nombre
        """
    )
    fun buscarActivas(
        @Param("categoriaId") categoriaId: Long?,
        @Param("texto") texto: String?
    ): List<Artesania>

    // GET /api/admin/artesanias: todas, también las inactivas (HU-16).
    @Query("SELECT a FROM Artesania a JOIN FETCH a.categoria ORDER BY a.nombre")
    fun findAllConCategoria(): List<Artesania>

    // Regla 409 de HU-19: no se desactiva una categoría con artesanías activas.
    fun countByCategoriaIdAndActivoTrue(categoriaId: Long): Long
}
