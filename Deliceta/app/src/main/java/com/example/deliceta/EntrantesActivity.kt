package com.example.deliceta


import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.deliceta.RecipeProvider.Companion.recipeList
import com.example.deliceta.adapter.RecipeAdapter
import com.example.deliceta.application.App
import com.example.deliceta.database.entities.Entrantes
import com.example.deliceta.databinding.ActivityEntrantesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EntrantesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntrantesBinding
    var textoTiempo:EditText?=null
    var textoTemporizador:TextView?=null
    var btntime:ImageButton?=null

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

    /*
    CUANDO SE HACE CLICK EN LA FOTO DE UN ITEM
    */
    fun onPhotoSelected(recipe: Recipe) {
        Toast.makeText(this, recipe.recipename, Toast.LENGTH_LONG).show()
    }

    /*
    CUANDO SE HACE CLICK A UN ITEM
    */
    fun onItemSelected(recipe: Recipe){
        /*SE CARGA EL CUADRO DE DIALOGO CON INFORMACION DE LA RECETA*/
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.info_receta, null)
        builder.setView(view) //pasamos la vista al builder
        view.background = ColorDrawable(ResourcesCompat.getColor(resources, R.color.turquesaOscuro, null))
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
       CUANDO EL IMAGEBUTTON DE la foto SEA PULSADO, OCURRIRÁ LA FUNCION camera()
       */
    fun camera(view: View){
        Toast.makeText(this, "CAMBIAR FOTO", Toast.LENGTH_LONG).show()
    }

    /*
    CUANDO EL IMAGEBUTTON DEL TEMPORIZADOR SEA PULSADO, OCURRIRÁ LA FUNCION PLAY()
    */
    fun play(view: View){
        if ((!(textoTiempo?.text.toString().isEmpty())) && (textoTiempo?.text.toString().length < 4))
        {
            var tiempoSegundos=textoTiempo?.text.toString().toLong()
            var tiempoMilisegundos=tiempoSegundos*1000*60
            val notificacion=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            var r=RingtoneManager.getRingtone(this@EntrantesActivity,notificacion)

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

    /*
    BOTON DE SWIPE-DOWN PARA ACTUALIZAR LA LISTA DE ELEMENTOS
    */
    private fun refreshApp(){
        val refresh = binding.refresh
        refresh.setOnRefreshListener{
            recipeList.clear()
            giveTheRecipes()
            initRecyclerView()
            refresh.isRefreshing = false
        }
    }

    /*
    INICIALIZACION RECYCLERVIEW
    */
    private fun initRecyclerView() {
        binding.recyclerRecipe.layoutManager = LinearLayoutManager(this)
        binding.recyclerRecipe.adapter = RecipeAdapter(RecipeProvider.recipeList, {recipee -> onItemSelected(recipee) }, {recipee-> onPhotoSelected(recipee)})
    }

    /*
    OBTENER DATOS DE LA BBDD EN LA LISTA MUTABLE
    */
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

    /*
    BOTON FLOTANTE PARA AGREGAR UNA NUEVA RECETA
    */
    private fun setupFloatingActionButton() {
        binding.nuevaReceta.setOnClickListener {
            Toast.makeText(applicationContext, "Nueva Receta", Toast.LENGTH_SHORT).show()
            /*FORMULARIO DE NUEVA RECETA*/
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.insertar_layout, null)
            builder.setView(view) //PASAMOS LA VISTA AL BUILDER
            val dialog = builder.create()
            dialog.show()
            /*AL PULSAR A INSERTAR, SE VALIDAN E INTRODUCEN LOS DATOS EN LA BBDD USANDO SUBPROCESOS */
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
                    (nombreEntrante != "" && tiempoEntrante != "" && ingredientesEntrante != "" && descripcionEntrante != "" && fotoEntrante != "" && tiempoEntrante.toInt() > 0)) {
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
                    Toast.makeText(this, "No dejes espacios en blanco ni pongas tiempos negativos", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }
    }
}