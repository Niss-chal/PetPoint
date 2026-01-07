package com.project.petpoint.repository

import com.project.petpoint.model.CartModel
import com.project.petpoint.model.ProductModel

interface CartRepo {
    fun addToCart(
        product: ProductModel,
        userId: String,
        quantity: Int = 1,
        callback: (Boolean, String) -> Unit
    )

    fun getCartItems(
        userId: String,
        callback: (Boolean, String, List<CartModel>?) -> Unit
    )

    fun updateCartItemQuantity(
        cartId: String,
        quantity: Int,
        callback: (Boolean, String) -> Unit
    )

    fun removeFromCart(
        cartId: String,
        callback: (Boolean, String) -> Unit
    )

    fun clearCart(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    fun getCartItemCount(
        userId: String,
        callback: (Int) -> Unit
    )
}