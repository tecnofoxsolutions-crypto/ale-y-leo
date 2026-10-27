package com.aleyleo.api.common

// Excepciones de negocio. Los Services las lanzan y GlobalExceptionHandler las convierte
// en la respuesta HTTP correcta, así los controllers no tienen try/catch.

// 404: el recurso no existe o está inactivo.
class NoEncontradoException(mensaje: String) : RuntimeException(mensaje)

// 409: choca con el estado actual de los datos.
// "codigo" es el valor de "error" en el ApiError, por ejemplo EMAIL_REGISTRADO o TRANSICION_INVALIDA.
open class ConflictoException(val codigo: String, mensaje: String) : RuntimeException(mensaje)

// 409 al generar o pagar un pedido sin stock suficiente (HU-10 y HU-11).
class StockInsuficienteException(nombreArtesania: String) :
    ConflictoException("STOCK_INSUFICIENTE", "No hay stock suficiente de '$nombreArtesania'")

// 400: la imagen subida no es JPG o PNG, está vacía o pesa más de 5 MB (BE-12).
class ArchivoInvalidoException(mensaje: String) : RuntimeException(mensaje)

// 401 en el login: un solo mensaje, sin decir si falló el email o la contraseña (HU-02).
class CredencialesInvalidasException : RuntimeException("Email o contraseña incorrectos")

// 403: el usuario no puede acceder a este recurso (por ejemplo, el pedido de otro cliente).
class AccesoDenegadoException(mensaje: String) : RuntimeException(mensaje)
