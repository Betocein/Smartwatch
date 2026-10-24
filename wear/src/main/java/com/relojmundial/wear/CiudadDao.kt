package com.relojmundial.wear

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CiudadDao {

    @Query("SELECT * FROM ciudades ORDER BY id")
    fun observarTodas(): Flow<List<Ciudad>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(ciudad: Ciudad)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarVarias(ciudades: List<Ciudad>)

    @Delete
    suspend fun eliminar(ciudad: Ciudad)

    @Query("DELETE FROM ciudades")
    suspend fun borrarTodas()
}
