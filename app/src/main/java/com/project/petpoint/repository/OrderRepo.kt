package com.project.petpoint.repository

import com.project.petpoint.model.OrderModel

interface OrderRepo {

    fun getOrderHistory(userId: String, callback: (Boolean, String, List<OrderModel>?) -> Unit)

    fun deleteOrderItem(userId: String, orderId: String, callback: (Boolean, String) -> Unit)

    fun clearOrderHistory(userId: String, callback: (Boolean, String) -> Unit)
}
