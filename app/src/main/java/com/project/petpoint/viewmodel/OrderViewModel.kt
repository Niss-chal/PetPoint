package com.project.petpoint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.OrderModel
import com.project.petpoint.repository.OrderRepo

class OrderViewModel(private val repo: OrderRepo) : ViewModel() {

    private val _orders = MutableLiveData<List<OrderModel>>(emptyList())
    val orders: LiveData<List<OrderModel>> get() = _orders

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    // For viewing order history
    fun getOrderHistory(userId: String) {
        _loading.postValue(true)
        repo.getOrderHistory(userId) { success, msg, data ->
            _loading.postValue(false)
            if (success) {
                _orders.postValue(data ?: emptyList())
            } else {
                _message.postValue(msg)
            }
        }
    }

    // For deleting individual order items
    fun deleteOrderItem(userId: String, orderId: String, callback: (Boolean) -> Unit) {
        _loading.postValue(true)
        repo.deleteOrderItem(userId, orderId) { success, msg ->
            _loading.postValue(false)
            if (success) {
                // Remove from local list
                val updatedList = _orders.value?.filter { it.orderId != orderId } ?: emptyList()
                _orders.postValue(updatedList)
                _message.postValue("Order item deleted")
                callback(true)
            } else {
                _message.postValue(msg)
                callback(false)
            }
        }
    }

    // For clearing entire order history
    fun clearOrderHistory(userId: String, callback: (Boolean) -> Unit) {
        _loading.postValue(true)
        repo.clearOrderHistory(userId) { success, msg ->
            _loading.postValue(false)
            if (success) {
                _orders.postValue(emptyList())
                _message.postValue("Order history cleared")
                callback(true)
            } else {
                _message.postValue(msg)
                callback(false)
            }
        }
    }

    fun clearMessage() {
        _message.postValue(null)
    }
}