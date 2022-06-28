package com.example.deliceta.database.dao

import android.net.Uri
import androidx.room.*
import com.example.deliceta.database.entities.Entrantes


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