package com.example.deliceta.database.dao

import androidx.room.*
import com.example.deliceta.database.entities.Postres

@Dao
interface PostresDao {
    @Query("SELECT * from postres")
    fun todosLosPostres():List<Postres>
    @Query("SELECT * from postres WHERE id = :idpostre")
    fun postresPorId(idpostre:Long): Postres
    @Insert
    fun save(postres: Postres)
    @Update
    fun update(postres: Postres)
    @Delete
    fun delete(postres: Postres)
}