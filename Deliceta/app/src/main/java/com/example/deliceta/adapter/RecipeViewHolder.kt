package com.example.deliceta.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliceta.EntrantesActivity
import com.example.deliceta.R
import com.example.deliceta.Recipe
import com.example.deliceta.databinding.ItemRecipeBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class RecipeViewHolder(view:View): RecyclerView.ViewHolder(view) {

    val binding = ItemRecipeBinding.bind(view)


    fun render(recipeModel: Recipe, onClickListener:(Recipe) -> Unit){
        binding.nombreReceta.text = recipeModel.recipename
        binding.tiempoCoccion.text = recipeModel.recipetime
        binding.ingredientes.text = recipeModel.recipeingredients
        binding.descripcion.text = recipeModel.recipedescription
        Glide.with(binding.photoreceta.context).load(recipeModel.recipeurl).into(binding.photoreceta)

        itemView.setOnClickListener { onClickListener(recipeModel) }
    /*
        binding.photoreceta.setOnClickListener{

            Toast.makeText(binding.photoreceta.context, recipeModel.recipename, Toast.LENGTH_SHORT).show()
        }



        itemView.setOnClickListener{
            Toast.makeText(binding.photoreceta.context, recipeModel.recipetime, Toast.LENGTH_SHORT)
                .show()
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.insertar_layout, null)
            builder.setView(view) //PASAMOS LA VISTA AL BUILDER
            val dialog = builder.create()
            dialog.show()
        }*/
    }
}
