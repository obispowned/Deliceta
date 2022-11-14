package com.example.deliceta.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliceta.Recipe
import com.example.deliceta.databinding.ItemRecipeBinding

class RecipeViewHolder(view:View): RecyclerView.ViewHolder(view) {

    val binding = ItemRecipeBinding.bind(view)


    fun render(
        recipeModel: Recipe,
        onClickListener: (Recipe) -> Unit,
        onClickListener_photo: (Recipe) -> Unit,
        onClickFav: (Recipe) -> Unit
    ){
        if (recipeModel.recipename.length > 20)
            binding.nombreReceta.text = recipeModel.recipename.substring(0, 20) + "..."
        else
            binding.nombreReceta.text = recipeModel.recipename
        binding.tiempoCoccion.text = recipeModel.recipetime
        if (recipeModel.recipeingredients.length > 20)
            binding.ingredientes.text = recipeModel.recipeingredients.substring(0, 20) + "..."
        else
            binding.ingredientes.text = recipeModel.recipeingredients
        if (recipeModel.recipedescription.length > 50)
            binding.descripcion.text = recipeModel.recipedescription.substring(0, 25) + "\n" + recipeModel.recipedescription.substring(25, 50) + "..."
        else if (recipeModel.recipedescription.length > 25)
            binding.descripcion.text = recipeModel.recipedescription.substring(0, 25) + "\n" + recipeModel.recipedescription.substring(25)
        else
            binding.descripcion.text = recipeModel.recipedescription
        Glide.with(binding.photoreceta.context).load(recipeModel.recipeurl).into(binding.photoreceta)
        binding.FavInfoRecipe.setChecked(recipeModel.recipefav)
        itemView.setOnClickListener { onClickListener(recipeModel) }
        binding.photoreceta.setOnClickListener { onClickListener_photo(recipeModel) }
        binding.FavInfoRecipe.setOnClickListener{ onClickFav(recipeModel) }
    }
}
