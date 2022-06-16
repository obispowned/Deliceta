package com.example.deliceta

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliceta.adapter.RecipeAdapter
import com.example.deliceta.application.App
import com.example.deliceta.database.entities.Platos
import com.example.deliceta.database.entities.Postres
import com.example.deliceta.databinding.ActivityEntrantesBinding
import com.example.deliceta.databinding.ActivityPlatosBinding
import com.example.deliceta.databinding.ActivityPostresBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        binding = ActivityPostresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RecipeProvider.recipeList.clear()
        giveTheRecipes()
        initRecyclerView()
        setupFloatingActionButton()
        refreshApp()
    }

    private fun refreshApp() {
        val refresh = binding.refresh3
        refresh.setOnRefreshListener {
            RecipeProvider.recipeList.clear()
            giveTheRecipes()
            initRecyclerView()
            refresh.isRefreshing = false
        }
    }

    fun onItemSelected(recipe: Recipe){
        Toast.makeText(this, recipe.recipetime, Toast.LENGTH_SHORT)
            .show()
    }

    private fun initRecyclerView() {
         val manager = LinearLayoutManager(this)
         //val decoration = DividerItemDecoration(this, manager.orientation)
         binding.recyclerRecipe3.layoutManager = LinearLayoutManager(this)
         binding.recyclerRecipe3.adapter = RecipeAdapter(RecipeProvider.recipeList, {recipee -> onItemSelected(recipee) })
         //binding.recyclerRecipe.addItemDecoration(decoration)
    }

    private fun giveTheRecipes() {
        lifecycleScope.launch {
            var postres: List<Postres> = withContext(Dispatchers.IO) {
                App.getDb().postresDao().todosLosPostres()
            }
            var i: Int = 0
            while (i < postres.size) {
                RecipeProvider.recipeList.add(
                    i, Recipe(
                        recipename = postres[i].nombre,
                        recipetime = postres[i].duracion + " minutos",
                        recipeingredients = postres[i].ingredientes,
                        recipedescription = postres[i].descripcion,
                        recipeurl = postres[i].urlphoto)
                )
                i++
            }
        }
    }

    private fun setupFloatingActionButton() {
        binding.nuevaReceta3.setOnClickListener {
            Toast.makeText(applicationContext, "Nueva Receta", Toast.LENGTH_SHORT).show()

            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.insertar_layout, null)
            builder.setView(view) //PASAMOS LA VISTA AL BUILDER
            val dialog = builder.create()
            dialog.show()

            val btnInsert: Button = view.findViewById(R.id.insertarDialog)
            btnInsert.setOnClickListener {
                val nombrePostre: String =
                    view.findViewById<EditText>(R.id.edtNombreReceta).text.toString()
                val tiempoPostre: String =
                    (view.findViewById<EditText>(R.id.edttiempoDeCoccion).text.toString())
                val ingredientesPostre: String =
                    view.findViewById<EditText>(R.id.edtingredientes).text.toString()
                val descripcionPostre: String =
                    view.findViewById<EditText>(R.id.edtDescripcion).text.toString()
                val fotoPostre: String= view.findViewById<EditText>(R.id.edtphoto).text.toString()
                if ((nombrePostre != null && tiempoPostre != null && ingredientesPostre != null && descripcionPostre != null && fotoPostre != null) &&
                    (nombrePostre != "" && tiempoPostre != "" && ingredientesPostre != "" && descripcionPostre != "" && fotoPostre != "")) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO)
                        {
                            App.getDb().postresDao().save(
                                Postres(
                                    nombre = nombrePostre,
                                    duracion = tiempoPostre,
                                    ingredientes = ingredientesPostre,
                                    descripcion = descripcionPostre,
                                    urlphoto = fotoPostre)
                            )
                        }
                    }
                    Toast.makeText(this, "Registro guardado", Toast.LENGTH_LONG).show()
                }
                else
                    Toast.makeText(this, "No dejes espacios en blanco", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }
    }

}
