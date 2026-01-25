package com.project.petpoint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.CartModel
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.CartRepo

class CartViewModel(private val repo: CartRepo) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartModel>>(emptyList())
    val cartItems: LiveData<List<CartModel>> get() = _cartItems

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    private val _totalPrice = MutableLiveData(0.0)
    val totalPrice: LiveData<Double> get() = _totalPrice

    private val _totalItems = MutableLiveData(0)
    val totalItems: LiveData<Int> get() = _totalItems

    fun addToCart(product: ProductModel?, userId: String, quantity: Int = 1, callback: (Boolean, String) -> Unit) {
        if (product.stock <= 0) {
            _message.postValue("${product.name} is out of stock")
            callback(false, "${product.name} is out of stock")
            return
        }

        _loading.postValue(true)
        repo.addToCart(product, userId, quantity) { success, msg ->
            _loading.postValue(false)
            _message.postValue(msg)
            if (success) getCartItems(userId) { _, _, _ -> }
            callback(success, msg)
        }
    }

    fun getCartItems(userId: String, callback: (Boolean, String, List<CartModel>) -> Unit) {
        _loading.postValue(true)
        repo.getCartItems(userId) { success, msg, data ->
            _loading.postValue(false)
            val safeList = data ?: emptyList()
            _cartItems.postValue(safeList)
            calculateTotals(safeList)
            if (!success) _message.postValue(msg)
            callback(success, msg, safeList)
        }
    }

    fun updateCartItemQuantity(cartId: String, quantity: Int, callback: (Boolean, String) -> Unit) {
        repo.updateCartItemQuantity(cartId, quantity) { success, msg ->
            if (success) {
                val updatedList = _cartItems.value?.map { if (it.cartItemId == cartId) it.copy(quantity = quantity) else it } ?: emptyList()
                _cartItems.postValue(updatedList)
                calculateTotals(updatedList)
            } else {
                _message.postValue(msg)
            }
            callback(success, msg)
        }
    }

    fun removeFromCart(cartId: String, callback: (Boolean, String) -> Unit) {
        _loading.postValue(true)
        repo.removeFromCart(cartId) { success, msg ->
            _loading.postValue(false)
            if (success) {
                _message.postValue("Item removed from cart")
                val updatedList = _cartItems.value?.filter { it.cartItemId != cartId } ?: emptyList()
                _cartItems.postValue(updatedList)
                calculateTotals(updatedList)
            } else {
                _message.postValue(msg)
            }
            callback(success, msg)
        }
    }

    fun clearCart(userId: String, callback: (Boolean, String) -> Unit) {
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

    fun increaseQuantity(cartItem: CartModel, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        if (cartItem.canIncreaseQuantity()) {
            updateCartItemQuantity(cartItem.cartItemId, cartItem.quantity + 1, callback)
        } else {
            _message.postValue("Maximum stock limit reached")
            callback(false, "Maximum stock limit reached")
        }
    }

    fun decreaseQuantity(cartItem: CartModel, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        if (cartItem.canDecreaseQuantity()) {
            updateCartItemQuantity(cartItem.cartItemId, cartItem.quantity - 1, callback)
        } else {
            callback(false, "Minimum quantity is 1")
        }
    }

    private fun calculateTotals(items: List<CartModel>) {
        _totalPrice.postValue(items.sumOf { it.getTotalPrice() })
        _totalItems.postValue(items.sumOf { it.quantity })
    }

    fun clearMessage() {
        _message.postValue(null)
    }
}
