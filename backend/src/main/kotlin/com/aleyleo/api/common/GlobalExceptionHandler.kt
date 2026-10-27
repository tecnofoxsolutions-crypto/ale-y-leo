package com.aleyleo.api.common

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

// Atrapa las excepciones de todos los controllers y responde siempre con un ApiError (sección 6.2).
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    // 400: un DTO con @Valid no pasó las validaciones (@NotBlank, @Email, @Positive...).
    // Devuelve qué campo falló y por qué.
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacion(e: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val campos = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Valor inválido") }
        return responder(HttpStatus.BAD_REQUEST, "VALIDACION", "Hay campos con errores", campos)
    }

    // 400: el JSON viene mal formado o con un tipo incorrecto (por ejemplo, texto en un número).
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun jsonInvalido(e: HttpMessageNotReadableException): ResponseEntity<ApiError> =
        responder(HttpStatus.BAD_REQUEST, "JSON_INVALIDO", "El cuerpo de la petición no es un JSON válido")

    // 400: un parámetro de la URL con un tipo incorrecto (por ejemplo, /api/artesanias/abc).
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun parametroInvalido(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiError> =
        responder(HttpStatus.BAD_REQUEST, "PARAMETRO_INVALIDO", "El parámetro '${e.name}' no es válido")

    // 404: la ruta no existe (por ejemplo, una URL mal escrita).
    @ExceptionHandler(NoResourceFoundException::class)
    fun rutaInexistente(e: NoResourceFoundException): ResponseEntity<ApiError> =
        responder(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", "La ruta no existe")

    @ExceptionHandler(NoEncontradoException::class)
    fun noEncontrado(e: NoEncontradoException): ResponseEntity<ApiError> =
        responder(HttpStatus.NOT_FOUND, "NO_ENCONTRADO", e.message ?: "Recurso no encontrado")

    @ExceptionHandler(AccesoDenegadoException::class)
    fun accesoDenegado(e: AccesoDenegadoException): ResponseEntity<ApiError> =
        responder(HttpStatus.FORBIDDEN, "ACCESO_DENEGADO", e.message ?: "No tienes acceso a este recurso")

    // 409: email o RUT repetido, transición de estado no permitida, stock insuficiente...
    @ExceptionHandler(ConflictoException::class)
    fun conflicto(e: ConflictoException): ResponseEntity<ApiError> =
        responder(HttpStatus.CONFLICT, e.codigo, e.message ?: "Conflicto con el estado actual")

    // 500: cualquier otro error. Se registra en el log, pero al cliente no se le muestran detalles internos.
    @ExceptionHandler(Exception::class)
    fun inesperado(e: Exception): ResponseEntity<ApiError> {
        log.error("Error no controlado", e)
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", "Ocurrió un error inesperado")
    }

    private fun responder(
        status: HttpStatus,
        error: String,
        mensaje: String,
        campos: Map<String, String>? = null
    ): ResponseEntity<ApiError> =
        ResponseEntity.status(status).body(ApiError(status.value(), error, mensaje, campos))
}
