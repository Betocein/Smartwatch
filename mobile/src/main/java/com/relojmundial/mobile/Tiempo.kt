package com.relojmundial.mobile

import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val LOCALE_ES = Locale.forLanguageTag("es-MX")

/** Devuelve la hora actual y se actualiza cada segundo. */
@androidx.compose.runtime.Composable
fun rememberAhora(): State<Instant> =
    produceState(initialValue = Instant.now()) {
        while (true) {
            value = Instant.now()
            delay(1000)
        }
    }

fun hora(zona: String, ahora: Instant, conSegundos: Boolean = true): String {
    val patron = if (conSegundos) "HH:mm:ss" else "HH:mm"
    return DateTimeFormatter.ofPattern(patron).format(ahora.atZone(ZoneId.of(zona)))
}

fun fecha(zona: String, ahora: Instant): String =
    DateTimeFormatter.ofPattern("EEE d 'de' MMM", LOCALE_ES).format(ahora.atZone(ZoneId.of(zona)))

/** Diferencia de horas entre la ciudad y la hora del telefono. */
fun diferencia(zona: String, ahora: Instant): String {
    val destino = ZoneId.of(zona).rules.getOffset(ahora).totalSeconds
    val local = ZoneId.systemDefault().rules.getOffset(ahora).totalSeconds
    val horas = (destino - local) / 3600.0
    return when {
        horas == 0.0 -> "Misma hora"
        horas == horas.toInt().toDouble() -> {
            val signo = if (horas > 0) "+" else ""
            "$signo${horas.toInt()} h"
        }
        else -> String.format(Locale.US, "%+.1f h", horas)
    }
}
