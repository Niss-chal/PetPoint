package com.project.petpoint.repository

import com.project.petpoint.model.ProductModel

interface ProductRepo {

    fun addProduct(model: ProductModel,callback:(Boolean,String)->Unit)

    fun updateProduct(model: ProductModel,callback: (Boolean, String) -> Unit)

    fun deleteProduct(productID:String,callback: (Boolean, String) -> Unit)

    fun getProductById(productID:String,callback: (Boolean, String, ProductModel?) -> Unit)

    fun getAllProduct(callback: (Boolean, String, List<ProductModel>?) -> Unit)

    fun getProductByCategory(categoryId:String,callback: (Boolean, String, List<ProductModel>?) -> Unit)

}