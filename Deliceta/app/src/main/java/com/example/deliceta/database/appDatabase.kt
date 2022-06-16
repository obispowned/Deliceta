package com.example.deliceta.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.deliceta.database.dao.EntrantesDao
import com.example.deliceta.database.dao.PlatosDao
import com.example.deliceta.database.dao.PostresDao
import com.example.deliceta.database.entities.Entrantes
import com.example.deliceta.database.entities.Platos
import com.example.deliceta.database.entities.Postres

class appDatabase {

    @Database(entities = [Entrantes::class, Platos::class, Postres::class], version = 1, exportSchema=false)//crea las tablas
    abstract class AppDatabase : RoomDatabase() {
        abstract fun entrantesDao(): EntrantesDao //Esto nos devolvera los Dao. Gracias a esto, cuando pongamos personaDao., IntelliSense nos autocompletaria con las funciones de nuestro DAO
        abstract fun platosDao(): PlatosDao
        abstract fun postresDao(): PostresDao

        companion object{
            private var db: AppDatabase? = null
            fun getDB(context: Context):AppDatabase{ //comprueba si db es null, si no es null la inicializa
                if (db==null){
                    db = Room.databaseBuilder( //nos crea la base de datos con builder
                        context, //el contexto de nuestra app, el parametro
                        AppDatabase::class.java, "database-name" //la clase encargada d juntar los entities y los DAO
                    ).build() //todo esto creara el objeto de la BDD para poderla utilizar

                }
                return db!! //doble exclamacion porque nunca va a ser nullo
            }
        }

    }
}