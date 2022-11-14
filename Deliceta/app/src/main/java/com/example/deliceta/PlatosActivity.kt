package com.example.deliceta

import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.deliceta.RecipeProvider.Companion.recipeList
import com.example.deliceta.adapter.RecipeAdapter
import com.example.deliceta.application.App
import com.example.deliceta.database.entities.Platos
import com.example.deliceta.databinding.ActivityPlatosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlatosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlatosBinding
    var textoTiempo:EditText?=null
    var textoTemporizador:TextView?=null
    var btntime:ImageButton?=null

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
        binding.recyclerRecipe2.layoutManager = LinearLayoutManager(this)
        binding.recyclerRecipe2.adapter = RecipeAdapter(recipeList,
            {recipee -> onItemSelected(recipee) },
            {recipee-> onPhotoSelected(recipee)}, {recipee-> onClickFav(recipee)}
        )
    }

    fun onItemSelected(recipe: Recipe) {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.info_receta, null)
        builder.setView(view) //PASAMOS LA VISTA AL BUILDER
        view.background = ColorDrawable(ResourcesCompat.getColor(resources, R.color.magenta, null))
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
        Glide.with(info_photo.context).load(recipe.recipeurl)
            .into(view.findViewById(R.id.infoPhotoreceta))
        dialog.show()

        textoTiempo=view.findViewById(R.id.textotiempo)
        textoTemporizador=view.findViewById(R.id.textocuentaatras)
        btntime = view.findViewById(R.id.timeButton)

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
                        var recipePlato =
                            App.getDb().platosDao().platosPorNombre(recipe.recipename)
                        App.getDb().platosDao().delete(recipePlato)
                    }
                }
                dialog.dismiss()
            }
            cancelar.setOnClickListener {
                dialog.dismiss()
            }

        }
    }

    fun play(view: View){
        if ((!(textoTiempo?.text.toString().isEmpty())) && (textoTiempo?.text.toString().length < 4))
        {
            var tiempoSegundos=textoTiempo?.text.toString().toLong()
            var tiempoMilisegundos=tiempoSegundos*1000*60
            val notificacion= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            var r= RingtoneManager.getRingtone(this@PlatosActivity,notificacion)

            btntime?.setEnabled(false)
            object : CountDownTimer(tiempoMilisegundos,1000) {
                override fun onFinish() {
                    r.play()
                    btntime?.setEnabled(true)
                    Thread.sleep(5000)
                    r.stop()
                }

                override fun onTick(millisUntilFinished: Long) {
                    val tiempoSegundos = (millisUntilFinished / 1000).toInt()
                    textoTemporizador?.text = tiempoSegundos.toString().padStart(2, '0')
                }
            }.start()
        }
    }

    fun onPhotoSelected(recipe: Recipe) {
        Toast.makeText(this, recipe.recipename, Toast.LENGTH_LONG).show()
    }

    fun onClickFav(recipe: Recipe){
        Toast.makeText(this, recipe.recipename,Toast.LENGTH_LONG).show()

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
                        recipefav = platos[i].fav,
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
                                    fav = false,
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

