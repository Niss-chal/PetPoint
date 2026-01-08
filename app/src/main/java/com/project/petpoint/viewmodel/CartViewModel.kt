package com.project.petpoint.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.CartModel
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.CartRepo

class CartViewModel(private val repo: CartRepo) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartModel>?>()
    val cartItems: MutableLiveData<List<CartModel>?> get() = _cartItems

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _message = MutableLiveData<String?>()
    val message: MutableLiveData<String?> get() = _message

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: MutableLiveData<Double> get() = _totalPrice

    private val _totalItems = MutableLiveData<Int>()
    val totalItems: MutableLiveData<Int> get() = _totalItems

    fun addToCart(
        product: ProductModel,
        userId: String,
        quantity: Int = 1,
        callback: (Boolean, String) -> Unit
    ) {
        if (product.stock <= 0) {
            _message.postValue("${product.name} is out of stock")
            callback(false, "${product.name} is out of stock")
            return
        }

        _loading.postValue(true)

        repo.addToCart(product, userId, quantity) { success, msg ->
            _loading.postValue(false)
            _message.postValue(msg)
            if (success) {
                getCartItems(userId) { _, _, _ -> }
            }
            callback(success, msg)
        }
    }

    fun getCartItems(
        userId: String,
        callback: (Boolean, String, List<CartModel>?) -> Unit
    ) {
        _loading.postValue(true)
        repo.getCartItems(userId) { success, msg, data ->
            _loading.postValue(false)
            if (success) {
                _cartItems.postValue(data)
                calculateTotals(data)
            } else {
                _cartItems.postValue(null)
                _message.postValue(msg)
            }
            callback(success, msg, data)
        }
    }

    fun updateCartItemQuantity(
        cartId: String,
        quantity: Int,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateCartItemQuantity(cartId, quantity) { success, msg ->
            if (success) {
                // Update local list
                val updatedList = _cartItems.value?.map { item ->
                    if (item.cartItemId == cartId) {
                        item.copy(quantity = quantity)
                    } else {
                        item
                    }
                }
                _cartItems.postValue(updatedList)
                calculateTotals(updatedList)
            } else {
                _message.postValue(msg)
            }
            callback(success, msg)
        }
    }

    fun removeFromCart(
        cartId: String,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.postValue(true)
        repo.removeFromCart(cartId) { success, msg ->
            _loading.postValue(false)
            if (success) {
                _message.postValue("Item removed from cart")
                // Update local list
                val updatedList = _cartItems.value?.filter { it.cartItemId != cartId }
                _cartItems.postValue(updatedList)
                calculateTotals(updatedList)
            } else {
                _message.postValue(msg)
            }
            callback(success, msg)
        }
    }

    fun clearCart(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.postValue(true)
        repo.clearCart(userId) { success, msg ->
            _loading.postValue(false)
            if (success) {
                _cartItems.postValue(emptyList())
                _totalPrice.postValue(0.0)
                _totalItems.postValue(0)
                _message.postValue("Cart cleared")
            } else {
                _message.postValue(msg)
            }
            callback(success, msg)
        }
    }

    fun getCartItemCount(
        userId: String,
        callback: (Int) -> Unit
    ) {
        repo.getCartItemCount(userId) { count ->
            _totalItems.postValue(count)
            callback(count)
        }
    }

    /**
     * Increase quantity helper
     */
    fun increaseQuantity(cartItem: CartModel, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        if (cartItem.canIncreaseQuantity()) {
            updateCartItemQuantity(cartItem.cartItemId, cartItem.quantity + 1, callback)
        } else {
            _message.postValue("Maximum stock limit reached")
            callback(false, "Maximum stock limit reached")
        }
    }

    /**
     * Decrease quantity helper
     */
    fun decreaseQuantity(cartItem: CartModel, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        if (cartItem.canDecreaseQuantity()) {
            updateCartItemQuantity(cartItem.cartItemId, cartItem.quantity - 1, callback)
        } else {
            callback(false, "Minimum quantity is 1")
        }
    }

    /**
     * Calculate totals
     */
    private fun calculateTotals(items: List<CartModel>?) {
        val total = items?.sumOf { it.getTotalPrice() } ?: 0.0
        val count = items?.sumOf { it.quantity } ?: 0

        _totalPrice.postValue(total)
        _totalItems.postValue(count)
    }

    /**
     * Clear message
     */
    fun clearMessage() {
        _message.postValue(null)
    }
}