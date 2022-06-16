package com.example.deliceta

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.deliceta.RecipeProvider.Companion.recipeList
import com.example.deliceta.adapter.RecipeAdapter
import com.example.deliceta.application.App
import com.example.deliceta.database.entities.Entrantes
import com.example.deliceta.databinding.ActivityEntrantesBinding
import com.example.deliceta.databinding.InsertarLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EntrantesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntrantesBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        binding = ActivityEntrantesBinding.inflate(layoutInflater) //
        setContentView(binding.root)

        recipeList.clear()
        giveTheRecipes()
        initRecyclerView()
        setupFloatingActionButton()
        refreshApp()


    }

    fun volcar_en_info_receta(recipe: Recipe)
    {

    }

    fun onItemSelected(recipe: Recipe){
        /*val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle(recipe.recipename)
            setMessage("We have a message")
            show()
        }*/

        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.info_receta, null)
        builder.setView(view) //PASAMOS LA VISTA AL BUILDER
        val dialog = builder.create()

        val info_name = view.findViewById<TextView>(R.id.infoNameReceta)
        info_name.setText(recipe.recipename).toString()
        val info_time = view.findViewById<TextView>(R.id.infoTiempoCoccion)
        info_time.setText(recipe.recipetime).toString()
        val info_ingreds = view.findViewById<TextView>(R.id.infoIngredientes)
        info_ingreds.setText(recipe.recipeingredients).toString()
        val info_descripcion = view.findViewById<TextView>(R.id.infoDescripcion)
        info_descripcion.setText(recipe.recipedescription).toString()
        val info_photo = view.findViewById<ImageView>(R.id.infoPhotoreceta)
        Glide.with(info_photo.context).load(recipe.recipename).into(view.findViewById(R.id.infoPhotoreceta))


        dialog.show()
        val delete = view.findViewById<ImageButton>(R.id.deleteButton)
        delete.setOnClickListener {
            dialog.dismiss()
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.delete, null)
            builder.setView(view) //PASAMOS LA VISTA AL BUILDER
            val dialog = builder.create()
            dialog.show()
            val borrarReceta = view.findViewById<Button>(R.id.borrar)
            val cancelar = view.findViewById<Button>(R.id.cancelar)
            borrarReceta.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        var recipeEntrante =
                            App.getDb().entrantesDao().entrantesPorNombre(recipe.recipename)
                        App.getDb().entrantesDao().delete(recipeEntrante)
                    }
                }
                dialog.dismiss()
            }
            cancelar.setOnClickListener {
                dialog.dismiss()
            }


        }
    }

    private fun refreshApp(){
        val refresh = binding.refresh
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
        binding.recyclerRecipe.layoutManager = LinearLayoutManager(this)
        binding.recyclerRecipe.adapter = RecipeAdapter(RecipeProvider.recipeList, {recipee -> onItemSelected(recipee) })
        //binding.recyclerRecipe.addItemDecoration(decoration)
    }


/*
    fun onItemSelected(recipe : Recipe){
        Toast.makeText(this, recipe.recipename, Toast.LENGTH_SHORT).show()
    }
    */


    /*MOSTRAR U OBTENER DATOS DE LA BBDD*/
    private fun giveTheRecipes() {
        lifecycleScope.launch {
            var entrantes: List<Entrantes> = withContext(Dispatchers.IO) {
                App.getDb().entrantesDao().todosLosEntrantes()
            }
            var i: Int = 0
            while (i < entrantes.size) {
                recipeList.add(
                    i, Recipe(
                        recipename = entrantes[i].nombre,
                        recipetime = entrantes[i].duracion + " minutos",
                        recipeingredients = entrantes[i].ingredientes,
                        recipedescription = entrantes[i].descripcion,
                        recipeurl = entrantes[i].urlphoto
                    )
                )
                i++
            }
        }
    }



    private fun setupFloatingActionButton() {
        binding.nuevaReceta.setOnClickListener {
            Toast.makeText(applicationContext, "Nueva Receta", Toast.LENGTH_SHORT).show()



            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.insertar_layout, null)
            builder.setView(view) //PASAMOS LA VISTA AL BUILDER
            val dialog = builder.create()
            dialog.show()


            val btnInsert: Button = view.findViewById(R.id.insertarDialog)
            btnInsert.setOnClickListener {
                val nombreEntrante: String =
                    view.findViewById<EditText>(R.id.edtNombreReceta).text.toString()
                val tiempoEntrante: String =
                    (view.findViewById<EditText>(R.id.edttiempoDeCoccion).text.toString())
                val ingredientesEntrante: String =
                    view.findViewById<EditText>(R.id.edtingredientes).text.toString()
                val descripcionEntrante: String =
                    view.findViewById<EditText>(R.id.edtDescripcion).text.toString()
                val fotoEntrante: String= view.findViewById<EditText>(R.id.edtphoto).text.toString()
                if ((nombreEntrante != null && tiempoEntrante != null && ingredientesEntrante != null && descripcionEntrante != null && fotoEntrante != null) &&
                    (nombreEntrante != "" && tiempoEntrante != "" && ingredientesEntrante != "" && descripcionEntrante != "" && fotoEntrante != "")) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO)
                        {
                            App.getDb().entrantesDao().save(
                                Entrantes(
                                    nombre = nombreEntrante,
                                    duracion = tiempoEntrante,
                                    ingredientes = ingredientesEntrante,
                                    descripcion = descripcionEntrante,
                                    urlphoto = fotoEntrante
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

            /*lifecycleScope.launch {
                val entrantes: List<Entrantes> = withContext(Dispatchers.IO) {
                    App.getDb().entrantesDao().todosLosEntrantes()
                }


                //binding.nombreReceta.text = entrantes.size.toString()
                val entrante_porID: Entrantes = withContext(Dispatchers.IO) {
                    App.getDb().entrantesDao().entrantesPorId(1)
                }*/
                //binding.textView.text = entrantes.toString()

            }


            /*
    *     /*  INSERTAR REGISTRO */
        val insertarB = binding.insertarButton
        insertarB.setOnClickListener {
            /*DIALOG*/
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(
                R.layout.insertar_layout,
                null
            )
            builder.setView(view) //PASAMOS LA VISTA AL BUILDER
            val dialog = builder.create()
            dialog.show()

            val btnInsert: Button = view.findViewById(R.id.insertarDialog)
            btnInsert.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO)
                    {
                        val animalNombre: String =
                            view.findViewById<EditText>(R.id.nombreAnimal).text.toString()
                        val animalRaza: String =
                            view.findViewById<EditText>(R.id.razaAnimal).text.toString()
                        val propietarioNombre: String =
                            view.findViewById<EditText>(R.id.nombrePropietario).text.toString()
                        App.getDb().pacienteDao().save(
                            Paciente(
                                nombreMascota = animalNombre,
                                razaAnimal = animalRaza,
                                nombrePropietario = propietarioNombre
                            )
                        )
                    }
                }
                Toast.makeText(this, "Registro guardado", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }*/
    }
}