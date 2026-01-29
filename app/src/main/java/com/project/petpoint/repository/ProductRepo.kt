package com.project.petpoint.repository

import android.content.Context
import android.net.Uri
import com.project.petpoint.model.ProductModel

interface ProductRepo {

    fun addProduct(model: ProductModel,callback:(Boolean,String)->Unit)

    fun updateProduct(model: ProductModel,callback: (Boolean, String) -> Unit)

    fun deleteProduct(productID:String,callback: (Boolean, String) -> Unit)

    fun getProductById(productID:String,callback: (Boolean, String, ProductModel?) -> Unit)

    fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit)

    fun getProductByCategory(categoryId:String,callback: (Boolean, String, List<ProductModel>?) -> Unit)

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context, uri: Uri): String?

    fun updateProductStock(productId: String,quantityToSubtract: Int,callback: (Boolean, String, Int?) -> Unit
    )

}