package com.example.deliceta

data class Recipe (
    val recipename: String,
    val recipetime:String,
    val recipeingredients:String,
    val recipedescription: String,
    var recipefav: Boolean,
    val recipeurl:String)
