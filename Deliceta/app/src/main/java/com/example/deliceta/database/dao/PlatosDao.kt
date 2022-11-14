package com.example.deliceta.database.dao

import androidx.room.*
import com.example.deliceta.database.entities.Platos

@Dao
interface PlatosDao {
    @Query("SELECT * from platos")
    fun todosLosPlatos():List<Platos>
    @Query("SELECT * from platos WHERE id = :idplato")
    fun platosPorId(idplato:Long): Platos
    @Query("SELECT * from platos WHERE nombre = :nombreplato")
    fun platosPorNombre(nombreplato:String): Platos
    @Insert
    fun save(platos: Platos)
    @Update
    fun update(platos: Platos)
    @Delete
    fun delete(platos: Platos)
}