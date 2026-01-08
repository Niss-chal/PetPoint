package com.project.petpoint.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.petpoint.model.CartModel
import com.project.petpoint.model.ProductModel

class CartRepoImpl : CartRepo {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var ref: DatabaseReference = database.getReference("cart")

    override fun addToCart(
        product: ProductModel,
        userId: String,
        quantity: Int,
        callback: (Boolean, String) -> Unit
    ) {
        ref.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var existingCartItem: CartModel? = null
                var existingKey: String? = null

                // Check if product already in cart
                for (childSnapshot in snapshot.children) {
                    val cartItem = childSnapshot.getValue(CartModel::class.java)
                    if (cartItem?.productId == product.productId) {
                        existingCartItem = cartItem
                        existingKey = childSnapshot.key
                        break
                    }
                }

                if (existingCartItem != null && existingKey != null) {
                    // Product exists, update quantity
                    val newQuantity = existingCartItem.quantity + quantity
                    if (newQuantity <= existingCartItem.maxStock) {
                        ref.child(existingKey).child("quantity").setValue(newQuantity)
                            .addOnSuccessListener {
                                callback(true, "Cart updated successfully")
                            }
                            .addOnFailureListener { e ->
                                callback(false, "Failed to update cart: ${e.message}")
                            }
                    } else {
                        callback(false, "Cannot add more items. Stock limit reached")
                    }
                } else {
                    // Product not in cart, add new item
                    val cartItemId = ref.push().key ?: return
                    val cartItem = CartModel(
                        cartItemId = cartItemId,
                        productId = product.productId,
                        userId = userId,
                        name = product.name,
                        price = product.price,
                        imageUrl = product.imageUrl,
                        quantity = quantity,
                        maxStock = product.stock,
                        addedAt = System.currentTimeMillis()
                    )

                    ref.child(cartItemId).setValue(cartItem.toMap())
                        .addOnSuccessListener {
                            callback(true, "Added to cart successfully")
                        }
                        .addOnFailureListener { e ->
                            callback(false, "Failed to add to cart: ${e.message}")
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Failed to check cart: ${error.message}")
            }
        })
    }

    override fun getCartItems(
        userId: String,
        callback: (Boolean, String, List<CartModel>?) -> Unit
    ) {
        ref.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = mutableListOf<CartModel>()

                for (childSnapshot in snapshot.children) {
                    try {
                        val cartItem = childSnapshot.getValue(CartModel::class.java)
                        if (cartItem != null) {
                            cartItems.add(cartItem)
                        }
                    } catch (e: Exception) {
                        // Skip invalid items
                    }
                }

                // Sort by addedAt in descending order (most recent first)
                cartItems.sortByDescending { it.addedAt }
                callback(true, "Cart items fetched successfully", cartItems)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Failed to fetch cart items: ${error.message}", null)
            }
        })
    }

    override fun updateCartItemQuantity(
        cartId: String,
        quantity: Int,
        callback: (Boolean, String) -> Unit
    ) {
        if (quantity < 1) {
            callback(false, "Quantity must be at least 1")
            return
        }

        ref.child(cartId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val cartItem = snapshot.getValue(CartModel::class.java)
                    if (cartItem != null) {
                        if (quantity <= cartItem.maxStock) {
                            ref.child(cartId).child("quantity").setValue(quantity)
                                .addOnSuccessListener {
                                    callback(true, "Quantity updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    callback(false, "Failed to update quantity: ${e.message}")
                                }
                        } else {
                            callback(false, "Quantity exceeds available stock")
                        }
                    } else {
                        callback(false, "Invalid cart item")
                    }
                } else {
                    callback(false, "Cart item not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Failed to check cart item: ${error.message}")
            }
        })
    }

    override fun removeFromCart(
        cartId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(cartId).removeValue()
            .addOnSuccessListener {
                callback(true, "Item removed from cart")
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to remove item: ${e.message}")
            }
    }

    override fun clearCart(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    callback(true, "Cart is already empty")
                    return
                }

                val updates = mutableMapOf<String, Any?>()
                for (childSnapshot in snapshot.children) {
                    updates[childSnapshot.key!!] = null
                }

                ref.updateChildren(updates)
                    .addOnSuccessListener {
                        callback(true, "Cart cleared successfully")
                    }
                    .addOnFailureListener { e ->
                        callback(false, "Failed to clear cart: ${e.message}")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Failed to fetch cart items: ${error.message}")
            }
        })
    }

    override fun getCartItemCount(
        userId: String,
        callback: (Int) -> Unit
    ) {
        ref.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalCount = 0

                for (childSnapshot in snapshot.children) {
                    val cartItem = childSnapshot.getValue(CartModel::class.java)
                    totalCount += cartItem?.quantity ?: 0
                }

                callback(totalCount)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(0)
            }
        })
    }
}