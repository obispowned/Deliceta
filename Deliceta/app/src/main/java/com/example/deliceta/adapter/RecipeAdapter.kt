package com.example.deliceta.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.deliceta.R
import com.example.deliceta.Recipe


class RecipeAdapter(private val recipeList:List<Recipe>, private val onClickListener:(Recipe) -> Unit, private val onClickListener_photo:(Recipe) -> Unit) : RecyclerView.Adapter<RecipeViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        //esta funcion pasa atributos y los pintara al contexto
        val context = parent.context
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        //pasa por cada item y va llamando al render pasandole ese item
        val item = recipeList[position]
        holder.render(item, onClickListener, onClickListener_photo)

    }


    override fun getItemCount(): Int {
        return recipeList.size
    }


    interface OnLongClickInterface {
        fun onLongClick(itemView: View, position: Int)
    }



}