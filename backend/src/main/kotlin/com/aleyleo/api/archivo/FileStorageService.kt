package com.aleyleo.api.archivo

import com.aleyleo.api.common.ArchivoInvalidoException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// BE-12: guarda las imágenes en la carpeta "uploads" y devuelve su URL pública (/uploads/...).
// WebConfig sirve esa carpeta en GET /uploads/** (endpoint 27).
@Service
class FileStorageService(@Value("\${almacenamiento.carpeta}") carpeta: String) {

    val raiz: Path = Paths.get(carpeta).toAbsolutePath().normalize()

    init {
        Files.createDirectories(raiz)
    }

    // Guarda la imagen como "<nombreBase>.jpg" o ".png" (por ejemplo art-6.jpg) y reemplaza la anterior.
    fun guardarImagen(archivo: MultipartFile, nombreBase: String): String {
        if (archivo.isEmpty) throw ArchivoInvalidoException("El archivo está vacío")
        if (archivo.size > MAX_BYTES) throw ArchivoInvalidoException("La imagen pesa más de 5 MB")
        return guardarBytes(archivo.bytes, nombreBase)
    }

    fun guardarBytes(bytes: ByteArray, nombreBase: String): String {
        // Se revisa el contenido real del archivo, no solo su nombre: un .jpg renombrado no pasa.
        val extension = extensionDeImagen(bytes) ?: throw ArchivoInvalidoException("La imagen debe ser JPG o PNG")

        EXTENSIONES.forEach { Files.deleteIfExists(raiz.resolve("$nombreBase.$it")) }
        val nombre = "$nombreBase.$extension"
        Files.write(raiz.resolve(nombre), bytes)
        return "/uploads/$nombre"
    }

    companion object {
        const val MAX_BYTES = 5L * 1024 * 1024 // 5 MB
        private val EXTENSIONES = listOf("jpg", "png")

        // Los primeros bytes ("firma") dicen el formato: JPG empieza con FF D8 FF y PNG con 89 50 4E 47.
        fun extensionDeImagen(bytes: ByteArray): String? = when {
            bytes.size >= 3 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() && bytes[2] == 0xFF.toByte() -> "jpg"
            bytes.size >= 4 && bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() &&
                bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte() -> "png"
            else -> null
        }
    }
}
