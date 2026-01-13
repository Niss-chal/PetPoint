package com.project.petpoint.model

data class ProductModel(
    var productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    var categoryId: String = "",
    val imageUrl: String = "",
    var stock : Int = 0,


){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "imageUrl" to imageUrl,
            "categoryId" to categoryId,
            "productId" to productId,
            "name" to name,
            "price" to price,
            "description" to description,
            "stock" to stock,



        )
    }

}