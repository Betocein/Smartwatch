package com.relojmundial.wear

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tabla "ciudades": cada fila es una ciudad favorita con su zona horaria. */
@Entity(tableName = "ciudades", indices = [Index(value = ["zona"], unique = true)])
data class Ciudad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val zona: String
)

/** Ciudad disponible en el catalogo para agregar. */
data class CiudadCatalogo(val nombre: String, val zona: String)

val CATALOGO = listOf(
    CiudadCatalogo("Ciudad de México", "America/Mexico_City"),
    CiudadCatalogo("Hermosillo", "America/Hermosillo"),
    CiudadCatalogo("Tijuana", "America/Tijuana"),
    CiudadCatalogo("Nueva York", "America/New_York"),
    CiudadCatalogo("Los Ángeles", "America/Los_Angeles"),
    CiudadCatalogo("Toronto", "America/Toronto"),
    CiudadCatalogo("Honolulu", "Pacific/Honolulu"),
    CiudadCatalogo("Bogotá", "America/Bogota"),
    CiudadCatalogo("Lima", "America/Lima"),
    CiudadCatalogo("Santiago", "America/Santiago"),
    CiudadCatalogo("Buenos Aires", "America/Argentina/Buenos_Aires"),
    CiudadCatalogo("São Paulo", "America/Sao_Paulo"),
    CiudadCatalogo("Londres", "Europe/London"),
    CiudadCatalogo("Madrid", "Europe/Madrid"),
    CiudadCatalogo("París", "Europe/Paris"),
    CiudadCatalogo("Berlín", "Europe/Berlin"),
    CiudadCatalogo("Roma", "Europe/Rome"),
    CiudadCatalogo("Moscú", "Europe/Moscow"),
    CiudadCatalogo("El Cairo", "Africa/Cairo"),
    CiudadCatalogo("Johannesburgo", "Africa/Johannesburg"),
    CiudadCatalogo("Dubái", "Asia/Dubai"),
    CiudadCatalogo("Nueva Delhi", "Asia/Kolkata"),
    CiudadCatalogo("Bangkok", "Asia/Bangkok"),
    CiudadCatalogo("Pekín", "Asia/Shanghai"),
    CiudadCatalogo("Tokio", "Asia/Tokyo"),
    CiudadCatalogo("Seúl", "Asia/Seoul"),
    CiudadCatalogo("Sídney", "Australia/Sydney"),
    CiudadCatalogo("Auckland", "Pacific/Auckland")
)
