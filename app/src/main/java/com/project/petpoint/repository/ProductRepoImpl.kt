package com.project.petpoint.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.petpoint.model.ProductModel
import java.io.InputStream
import java.util.concurrent.Executors

class ProductRepoImpl : ProductRepo {
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    var ref: DatabaseReference = database.getReference("products")


    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dlnlxghqk",
            "api_key" to "429244379134354",
            "api_secret" to "p4NzH01x2uIdfGtYvk0sBiunpSA"
        )
    )

    override fun addProduct(
        model: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        var id = ref.push().key.toString()
        model.productId = id

        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Product added successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun updateProduct(
        model: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(model.productId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Product updated successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun updateProductStock(
        productId: String,
        quantityToSubtract: Int,
        callback: (Boolean, String, Int?) -> Unit
    ) {
        ref.child(productId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val currentStock = snapshot.child("stock").getValue(Int::class.java) ?: 0
                    val newStock = currentStock - quantityToSubtract

                    if (newStock < 0) {
                        callback(false, "Not enough stock available", null)
                        return@addOnSuccessListener
                    }

                    ref.child(productId).child("stock").setValue(newStock)
                        .addOnSuccessListener {
                            callback(true, "Stock updated successfully", newStock)
                        }
                        .addOnFailureListener { e ->
                            callback(false, "Failed to update stock: ${e.message}", null)
                        }
                } else {
                    callback(false, "Product not found", null)
                }
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to fetch product: ${e.message}", null)
            }
    }


    override fun deleteProduct(
        productID: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(productID).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Product deleted successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getProductById(
        productID: String,
        callback: (Boolean, String, ProductModel?) -> Unit
    ) {
        ref.child(productID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(ProductModel::class.java)
                    callback(true, "Product fetched successfully", product)
                } else {
                    callback(false, "Product not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, "Failed to fetch product: ${error.message}", null)
            }
        })
    }

    override fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var allProducts = mutableListOf<ProductModel>()
                    for (data in snapshot.children) {
                        var product = data.getValue(ProductModel::class.java)
                        if (product != null) {
                            allProducts.add(product)
                        }
                    }

                    callback(true, "product fetched", allProducts)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }

        })
    }

    override fun getProductByCategory(
        categoryId: String,
        callback: (Boolean, String, List<ProductModel>?) -> Unit
    ) {
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var allProducts = mutableListOf<ProductModel>()
                        for (data in snapshot.children) {
                            var product = data.getValue(ProductModel::class.java)
                            if (product != null) {
                                allProducts.add(product)
                            }
                        }

                        callback(true, "Product fetched", allProducts)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }


    override fun uploadImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    ) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(
        context: Context,
        uri: Uri
    ): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}

