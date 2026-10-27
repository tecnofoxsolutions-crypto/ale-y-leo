package com.aleyleo.api.common

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

// Validaciones chilenas que usan los DTO (las mismas reglas que la app, util/Validadores.kt).

// ---------- RUT ----------

// "18.432.567-5", "184325675" y "18432567-5" son el mismo RUT: se deja solo cuerpo + dígito.
fun limpiarRut(rut: String): String = rut.replace(".", "").replace("-", "").trim().uppercase()

// Dígito verificador con módulo 11: se multiplica cada dígito (de derecha a izquierda) por 2, 3, 4, 5, 6, 7, 2, 3...
fun calcularDigitoVerificador(cuerpo: String): Char {
    var suma = 0
    var factor = 2
    for (digito in cuerpo.reversed()) {
        suma += digito.digitToInt() * factor
        factor = if (factor == 7) 2 else factor + 1
    }
    return when (val resto = 11 - suma % 11) {
        11 -> '0'
        10 -> 'K'
        else -> resto.digitToChar()
    }
}

fun rutValido(rut: String?): Boolean {
    val limpio = limpiarRut(rut ?: return false)
    if (limpio.length < 8 || limpio.length > 9) return false
    val cuerpo = limpio.dropLast(1)
    if (!cuerpo.all { it.isDigit() }) return false
    return calcularDigitoVerificador(cuerpo) == limpio.last()
}

// Formato con que se guarda: "18.432.567-5".
fun formatearRut(rut: String): String {
    val limpio = limpiarRut(rut)
    val cuerpo = limpio.dropLast(1).reversed().chunked(3).joinToString(".").reversed()
    return "$cuerpo-${limpio.last()}"
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [RutValidator::class])
annotation class RutValido(
    val message: String = "El RUT no es válido",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class RutValidator : ConstraintValidator<RutValido, String> {
    // null lo revisa @NotBlank; aquí solo se valida el dígito verificador.
    override fun isValid(valor: String?, context: ConstraintValidatorContext) = valor.isNullOrBlank() || rutValido(valor)
}

// ---------- Teléfono ----------

// Celular chileno: 9 dígitos que parten con 9, con o sin +56 y con o sin espacios.
fun normalizarTelefono(telefono: String): String {
    val digitos = telefono.replace(" ", "").removePrefix("+").removePrefix("56")
    return "+56$digitos"
}

fun telefonoValido(telefono: String?): Boolean =
    telefono != null && Regex("^(\\+?56)?9\\d{8}$").matches(telefono.replace(" ", ""))

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TelefonoValidator::class])
annotation class TelefonoChileno(
    val message: String = "Ingresa un celular chileno, por ejemplo +56 9 1234 5678",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class TelefonoValidator : ConstraintValidator<TelefonoChileno, String> {
    override fun isValid(valor: String?, context: ConstraintValidatorContext) = valor.isNullOrBlank() || telefonoValido(valor)
}
