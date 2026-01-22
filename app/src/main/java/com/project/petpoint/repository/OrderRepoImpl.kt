package com.project.petpoint.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.petpoint.model.OrderModel

class OrderRepoImpl : OrderRepo {
    private val database = FirebaseDatabase.getInstance().getReference("order_history")

    override fun getOrderHistory(userId: String, callback: (Boolean, String, List<OrderModel>?) -> Unit) {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<OrderModel>()
                for (childSnapshot in snapshot.children) {
                    val order = childSnapshot.getValue(OrderModel::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }
                callback(true, "Orders fetched successfully", orders)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun clearOrderHistory(userId: String, callback: (Boolean, String) -> Unit) {
        database.child(userId).removeValue()
            .addOnSuccessListener {
                callback(true, "Order history cleared")
            }
            .addOnFailureListener { error ->
                callback(false, error.message ?: "Failed to clear history")
            }
    }
}