package com.project.petpoint.model

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val quantity: Int = 0,
    val totalPrice: Double = 0.0,
    val date: String = ""
)
