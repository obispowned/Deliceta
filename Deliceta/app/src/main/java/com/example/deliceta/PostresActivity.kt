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
    var textoTiempo:EditText?=null
    var textoTemporizador: TextView?=null
    var btntime: ImageButton?=null

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

    /*
CUANDO SE HACE CLICK A UN ITEM
*/
    fun onItemSelected(recipe: Recipe){
        /*SE CARGA EL CUADRO DE DIALOGO CON INFORMACION DE LA RECETA*/
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.info_receta, null)
        builder.setView(view) //pasamos la vista al builder
        view.background = ColorDrawable(ResourcesCompat.getColor(resources, R.color.naranjaClaro, null))
        val dialog = builder.create()
        /*Se insertan los datos en las id del layout*/
        val info_name = view.findViewById<TextView>(R.id.infoNameReceta)
        info_name.setText(recipe.recipename).toString()
        val info_time = view.findViewById<TextView>(R.id.infoTiempoCoccion)
        info_time.setText(recipe.recipetime).toString()
        val info_ingreds = view.findViewById<TextView>(R.id.infoIngredientes)
        info_ingreds.setText(recipe.recipeingredients).toString()
        val info_descripcion = view.findViewById<TextView>(R.id.infoDescripcion)
        info_descripcion.setText(recipe.recipedescription).toString()
        val info_photo = view.findViewById<ImageView>(R.id.infoPhotoreceta)
        Glide.with(info_photo.context).load(recipe.recipeurl).into(view.findViewById(R.id.infoPhotoreceta))
        /*SE MUESTRA EL CUADRO DE DIALOGO CON LOS DATOS YA CARGADOS*/
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

    /*
    CUANDO EL IMAGEBUTTON DEL TEMPORIZADOR SEA PULSADO, OCURRIRÁ LA FUNCION PLAY()
    */
    fun play(view: View){
        if ((!(textoTiempo?.text.toString().isEmpty())) && (textoTiempo?.text.toString().length < 4))
        {
            var tiempoSegundos=textoTiempo?.text.toString().toLong()
            var tiempoMilisegundos=tiempoSegundos*1000*60
            val notificacion= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            var r= RingtoneManager.getRingtone(this@PostresActivity,notificacion)

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

    private fun initRecyclerView() {
         binding.recyclerRecipe3.layoutManager = LinearLayoutManager(this)
         binding.recyclerRecipe3.adapter = RecipeAdapter(RecipeProvider.recipeList,
             {recipee -> onItemSelected(recipee) },
             {recipee-> onPhotoSelected(recipee)},
             {recipee-> onClickFav(recipee)})
    }

    fun onClickFav(recipe: Recipe){
        Toast.makeText(this, recipe.recipename,Toast.LENGTH_LONG).show()
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
                        recipefav = postres[i].fav,
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
                                    fav = false,
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
