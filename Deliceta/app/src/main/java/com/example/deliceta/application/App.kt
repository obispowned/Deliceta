package com.example.deliceta.application
import android.app.Application
import com.example.deliceta.database.appDatabase

class App : Application() {

    companion object{
        private var db: appDatabase.AppDatabase? =null
        public fun getDb(): appDatabase.AppDatabase {
            return db!!
        }
    }
    override fun onCreate(){
        super.onCreate()
        db = appDatabase.AppDatabase.getDB(applicationContext)
    }
}