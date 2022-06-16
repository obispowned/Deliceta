
package com.example.deliceta.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName="entrantes")
data class Entrantes (
    @PrimaryKey(autoGenerate = true)
    val id: Long=0, //columnas
    val nombre:String,
    val duracion:String,
    val ingredientes:String,
    val descripcion:String,
    val urlphoto:String
    )

@Entity (tableName="platos")
data class Platos (
    @PrimaryKey(autoGenerate = true)
    val id: Long=0, //columnas
    val nombre:String,
    val duracion:String,
    val ingredientes:String,
    val descripcion:String,
    val urlphoto:String
)

@Entity (tableName="postres")
data class Postres (
    @PrimaryKey(autoGenerate = true)
    val id: Long=0, //columnas
    val nombre:String,
    val duracion:String,
    val ingredientes:String,
    val descripcion:String,
    val urlphoto:String
)
