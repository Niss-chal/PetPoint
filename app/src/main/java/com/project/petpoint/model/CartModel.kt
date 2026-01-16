package com.project.petpoint.model

data class CartModel(
    val cartItemId: String = "",
    val productId: String = "",
    val userId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    var quantity: Int = 1,
    val maxStock: Int = 10,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "cartItemId" to cartItemId,
            "productId" to productId,
            "userId" to userId,
            "name" to name,
            "price" to price,
            "imageUrl" to imageUrl,
            "quantity" to quantity,
            "maxStock" to maxStock,
            "addedAt" to addedAt
        )
    }

    fun getTotalPrice(): Double = price * quantity

    fun canIncreaseQuantity(): Boolean = quantity < maxStock

    fun canDecreaseQuantity(): Boolean = quantity > 1
}
