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
import com.example.deliceta.RecipeProvider.Companion.recipeList
import com.example.deliceta.adapter.RecipeAdapter
import com.example.deliceta.application.App
import com.example.deliceta.database.entities.Entrantes
import com.example.deliceta.database.entities.Platos
import com.example.deliceta.databinding.ActivityPlatosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        binding = ActivityPlatosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeList.clear()
        giveTheRecipes()
        initRecyclerView()
        setupFloatingActionButton()
        refreshApp()
    }

    private fun refreshApp(){
        val refresh = binding.refresh2
        refresh.setOnRefreshListener{
            recipeList.clear()
            giveTheRecipes()
            initRecyclerView()
            refresh.isRefreshing = false

        }
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(this)
        //val decoration = DividerItemDecoration(this, manager.orientation)
        binding.recyclerRecipe2.layoutManager = LinearLayoutManager(this)
        binding.recyclerRecipe2.adapter = RecipeAdapter(recipeList, {recipee -> onItemSelected(recipee) })
        //binding.recyclerRecipe.addItemDecoration(decoration)
    }

    fun onItemSelected(recipe: Recipe){
        Toast.makeText(this, recipe.recipetime, Toast.LENGTH_SHORT)
            .show()
    }

    private fun giveTheRecipes() {
        lifecycleScope.launch {
            var platos: List<Platos> = withContext(Dispatchers.IO) {
                App.getDb().platosDao().todosLosPlatos()
            }
            var i: Int = 0
            while (i < platos.size) {
                recipeList.add(
                    i, Recipe(
                        recipename = platos[i].nombre,
                        recipetime = platos[i].duracion + " minutos",
                        recipeingredients = platos[i].ingredientes,
                        recipedescription = platos[i].descripcion,
                        recipeurl = platos[i].urlphoto
                    )
                )
                i++
            }
        }
    }


    private fun setupFloatingActionButton() {
        binding.nuevaReceta2.setOnClickListener {
            Toast.makeText(applicationContext, "Nueva Receta", Toast.LENGTH_SHORT).show()


            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.insertar_layout, null)
            builder.setView(view) //PASAMOS LA VISTA AL BUILDER
            val dialog = builder.create()
            dialog.show()


            val btnInsert: Button = view.findViewById(R.id.insertarDialog)
            btnInsert.setOnClickListener {
                val nombrePlato: String =
                    view.findViewById<EditText>(R.id.edtNombreReceta).text.toString()
                val tiempoPlato: String =
                    (view.findViewById<EditText>(R.id.edttiempoDeCoccion).text.toString())
                val ingredientesPlato: String =
                    view.findViewById<EditText>(R.id.edtingredientes).text.toString()
                val descripcionPlato: String =
                    view.findViewById<EditText>(R.id.edtDescripcion).text.toString()
                val fotoPlato: String= view.findViewById<EditText>(R.id.edtphoto).text.toString()
                if ((nombrePlato != null && tiempoPlato != null && ingredientesPlato != null && descripcionPlato != null && fotoPlato != null) &&
                    (nombrePlato != "" && tiempoPlato != "" && ingredientesPlato != "" && descripcionPlato != "" && fotoPlato != "")) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO)
                        {
                            App.getDb().platosDao().save(
                                Platos(
                                    nombre = nombrePlato,
                                    duracion = tiempoPlato,
                                    ingredientes = ingredientesPlato,
                                    descripcion = descripcionPlato,
                                    urlphoto = fotoPlato
                                )
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

