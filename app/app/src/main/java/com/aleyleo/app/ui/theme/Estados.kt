package com.aleyleo.app.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Paid
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.aleyleo.app.model.EstadoPedido

// Colores, ícono y texto de cada estado del pedido (concepto, sección 3.2).
// El estado nunca depende solo del color: siempre lleva ícono y texto.
// Los íconos vienen de material-icons-extended.
data class EstiloEstado(
    val texto: String,
    val color: Color,
    val fondo: Color,
    val icono: ImageVector,
)

fun EstadoPedido.estilo(): EstiloEstado = when (this) {
    EstadoPedido.GENERADA -> EstiloEstado("Generada", Color(0xFF5C544D), Color(0xFFECE7E1), Icons.AutoMirrored.Rounded.ReceiptLong)
    EstadoPedido.PAGADA -> EstiloEstado("Pagada", Color(0xFF1F4E8C), Color(0xFFDCE6F3), Icons.Rounded.Paid)
    EstadoPedido.RECIBIDA -> EstiloEstado("Recibida", Color(0xFF245E66), Color(0xFFD7EBEC), Icons.Rounded.Inventory2)
    EstadoPedido.EN_PREPARACION -> EstiloEstado("En preparación", Color(0xFF7A5A1C), Color(0xFFF3E6C8), Icons.Rounded.Handyman)
    EstadoPedido.FINALIZADA -> EstiloEstado("Finalizada", Color(0xFF2E6B3A), Color(0xFFDCEFDD), Icons.Rounded.CheckCircle)
    EstadoPedido.CANCELADA -> EstiloEstado("Cancelada", Color(0xFF6B6560), Color(0xFFE7E3DF), Icons.Rounded.Cancel)
    EstadoPedido.RECHAZADA -> EstiloEstado("Rechazada", Color(0xFF9C2A20), Color(0xFFF7DCD8), Icons.Rounded.Block)
}
