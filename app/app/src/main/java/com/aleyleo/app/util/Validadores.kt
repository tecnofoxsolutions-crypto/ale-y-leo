package com.aleyleo.app.util

// Validaciones de formularios. Cada función devuelve el mensaje de error, o null si el dato es válido.
// Las usan Registro (AP-06) y Editar perfil (AP-10), así ambos validan lo mismo (HU-03).

fun validarNombre(nombre: String): String? {
    return when {
        nombre.isBlank() -> "Ingresa tu nombre completo"
        nombre.trim().length < 3 -> "El nombre es muy corto"
        else -> null
    }
}

fun validarEmail(email: String): String? {
    val formato = Regex("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")
    return when {
        email.isBlank() -> "Ingresa tu email"
        !formato.matches(email.trim()) -> "El email no tiene un formato válido"
        else -> null
    }
}

// Celular chileno: 9 dígitos que empiezan con 9, con o sin +56 (ej: +56912345678 o 912345678).
fun validarTelefono(telefono: String): String? {
    val limpio = telefono.replace(" ", "")
    val formato = Regex("^(\\+?56)?9\\d{8}$")
    return when {
        limpio.isBlank() -> "Ingresa tu teléfono"
        !formato.matches(limpio) -> "Usa un celular chileno, por ejemplo +56912345678"
        else -> null
    }
}

fun validarDireccion(direccion: String): String? {
    return if (direccion.isBlank()) "Ingresa tu dirección" else null
}

fun validarComuna(comuna: String): String? {
    return if (comuna.isBlank()) "Ingresa tu comuna" else null
}

fun validarPassword(password: String): String? {
    return if (password.length < 8) "La contraseña debe tener al menos 8 caracteres" else null
}

// RUT chileno con dígito verificador (módulo 11). Acepta "12.345.678-5" o "123456785".
fun validarRut(rut: String): String? {
    val limpio = rut.replace(".", "").replace("-", "").uppercase().trim()
    if (limpio.length < 2) return "Ingresa tu RUT"

    val cuerpo = limpio.dropLast(1)
    val dv = limpio.last()
    if (!cuerpo.all { it.isDigit() }) return "El RUT no tiene un formato válido"

    return if (calcularDigitoVerificador(cuerpo) == dv) null else "El RUT no es válido"
}

// Multiplica los dígitos de derecha a izquierda por 2, 3, 4, 5, 6, 7, 2, 3... y aplica 11 - (suma % 11).
fun calcularDigitoVerificador(cuerpo: String): Char {
    var suma = 0
    var multiplicador = 2
    for (digito in cuerpo.reversed()) {
        suma += digito.digitToInt() * multiplicador
        multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
    }
    return when (val resultado = 11 - (suma % 11)) {
        11 -> '0'
        10 -> 'K'
        else -> resultado.digitToChar()
    }
}
