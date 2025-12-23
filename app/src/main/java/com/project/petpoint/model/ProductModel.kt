package com.project.petpoint.model

data class ProductModel(
    var productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    var categoryId: String = "",
){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "name" to name,
            "price" to price,
            "description" to description
        )
    }

}