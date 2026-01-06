package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.ProductModel
import com.project.petpoint.repository.ProductRepo

class ProductViewModel(val repo : ProductRepo) : ViewModel() {

    fun addProduct(model: ProductModel, callback:(Boolean, String)->Unit){
        repo.addProduct(model,callback)
    }

    fun updateProduct(model: ProductModel,callback: (Boolean, String) -> Unit){
        repo.updateProduct(model,callback)
    }

    fun deleteProduct(productID:String,callback: (Boolean, String) -> Unit){
        repo.deleteProduct(productID,callback)
    }


    private val _allProducts = MutableLiveData<List<ProductModel>?>()
    val allProducts: MutableLiveData<List<ProductModel>?> get() = _allProducts

    // Filtered products based on search
    private val _filteredProducts = MutableLiveData<List<ProductModel>?>()
    val filteredProducts: MutableLiveData<List<ProductModel>?> get() = _filteredProducts

    // Search query
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: MutableLiveData<String> get() = _searchQuery

    // Loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    // Message for Toast
    private val _message = MutableLiveData<String?>()
    val message: MutableLiveData<String?> get() = _message

    // Selected product
    private val _selectedProduct = MutableLiveData<ProductModel?>()
    val selectedProduct: MutableLiveData<ProductModel?> get() = _selectedProduct


    fun getProductById(productID:String){
        _loading.postValue(true)
        repo.getProductById(productID){
                success,msg,data->
            if(success){
                _loading.postValue(false)
                _selectedProduct.postValue(data)
            }
            else{
                _loading.postValue(false)
                _selectedProduct.postValue(null)
            }
        }
    }

    fun getAllProduct(){
        _loading.postValue(true)
        repo.getAllProduct{
                success,msg,data->
            if(success){
                _loading.postValue(false)
                _allProducts.postValue(data)
                _filteredProducts.postValue(data)
            }
            else{
                _loading.postValue(false)
                _allProducts.postValue(null)
                _filteredProducts.postValue(null)
                _message.postValue(msg)
            }
        }
    }


    fun getProductByCategory(categoryId: String) {
        _loading.postValue(true)
        repo.getProductByCategory(categoryId) { success, msg, data ->
            if (success) {
                _loading.postValue(false)
                _filteredProducts.postValue(data)
            } else {
                _loading.postValue(false)
                _filteredProducts.postValue(null)
                _message.postValue(msg)
            }
        }
    }

    fun refreshProducts() {
        getAllProduct()
    }


    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.postValue(query)
        filterProducts(query)
    }

    private fun filterProducts(query: String) {
        val products = _allProducts.value ?: emptyList()

        if (query.isEmpty()) {
            _filteredProducts.postValue(products)
        } else {
            val filtered = products.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true)
            }
            _filteredProducts.postValue(filtered)
        }
    }

    fun onProductClick(product: ProductModel) {
        _selectedProduct.postValue(product)
        _message.postValue("Opening ${product.name}")
        // TODO
    }

    fun addToCart(product: ProductModel) {
        if (product.stock <= 0) {
            _message.postValue("${product.name} is out of stock")
            return
        }
    }

    fun clearMessage() {
        _message.postValue(null)
    }
}