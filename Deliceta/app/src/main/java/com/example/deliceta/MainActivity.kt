package com.example.deliceta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.deliceta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding //esta es una clase que nos lleva directamenter al layyout

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) //
        setContentView(binding.root)

        binding.btnentrantes.setOnClickListener{
            Toast.makeText(this, "ENTRANTES", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, EntrantesActivity::class.java))
        }

        binding.btnplatos.setOnClickListener{
            Toast.makeText(this, "PLATOS", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, PlatosActivity::class.java))
        }

        binding.btnpostres.setOnClickListener{
            Toast.makeText(this, "POSTRES", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, PostresActivity::class.java))
        }

    }
}