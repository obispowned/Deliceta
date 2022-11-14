package com.example.deliceta.database.dao

import androidx.room.*
import com.example.deliceta.Recipe
import com.example.deliceta.database.entities.Entrantes
import androidx.room.*

@Dao
interface EntrantesDao {
    @Query("SELECT * from entrantes")
    fun todosLosEntrantes():List<Entrantes>
    @Query("SELECT * from entrantes WHERE id = :idplato")
    fun entrantesPorId(idplato:Long): Entrantes
    @Query("SELECT * from entrantes WHERE nombre = :nombreplato")
    fun entrantesPorNombre(nombreplato:String): Entrantes
    @Insert
    fun save(entrantes: Entrantes)
    @Update
    fun update(entrantes: Entrantes)
    @Delete
    fun delete(entrantes: Entrantes)
}