package com.aleyleo.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

// BE-02 y BE-03: al arrancar, Hibernate (ddl-auto=update) crea las 5 tablas con sus FK.
// Se revisa directamente en information_schema de MySQL.
@SpringBootTest
class EsquemaTest {

    @Autowired
    lateinit var jdbc: JdbcTemplate

    @Test
    fun existenLas5Tablas() {
        val tablas = jdbc.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()",
            String::class.java
        ).map { it.lowercase() }

        listOf("usuario", "categoria", "artesania", "pedido", "detalle_pedido").forEach {
            assertTrue(it in tablas, "Falta la tabla $it")
        }
    }

    @Test
    fun existenLas4ClavesForaneas() {
        // Cada fila: "tabla.columna -> tabla_referenciada"
        val fks = jdbc.queryForList(
            """
            SELECT CONCAT(table_name, '.', column_name, ' -> ', referenced_table_name)
            FROM information_schema.key_column_usage
            WHERE table_schema = DATABASE() AND referenced_table_name IS NOT NULL
            """.trimIndent(),
            String::class.java
        ).map { it.lowercase() }.toSet()

        assertEquals(
            setOf(
                "artesania.categoria_id -> categoria",
                "pedido.usuario_id -> usuario",
                "detalle_pedido.pedido_id -> pedido",
                "detalle_pedido.artesania_id -> artesania"
            ),
            fks
        )
    }
}
