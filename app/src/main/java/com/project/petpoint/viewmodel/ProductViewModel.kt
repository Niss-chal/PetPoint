package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
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


    private val _allProducts = MutableLiveData<List<ProductModel>>(emptyList())
    val allProducts: LiveData<List<ProductModel>> get() = _allProducts

    private val _filteredProducts = MutableLiveData<List<ProductModel>>(emptyList())
    val filteredProducts: LiveData<List<ProductModel>> get() = _filteredProducts


    // Search query
    private val _searchQuery = MutableLiveData<String>("")
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

    // Track current category
    private var currentCategory = "All"

    private var cachedProducts: List<ProductModel> = emptyList()



    fun getProductById(productID:String){
        _loading.postValue(true)
        repo.getProductById(productID){
                success,msg,data->
            _loading.postValue(false)
            if(success){
                _selectedProduct.postValue(data)
            }
            else{
                _selectedProduct.postValue(null)
                _message.postValue(msg)
            }
        }
    }

    fun getAllProduct() {
        _loading.postValue(true)
        repo.getAllProduct { success, msg, data ->
            _loading.postValue(false)

            if (success) {
                val safeData = data ?: emptyList()
                cachedProducts = safeData
                _allProducts.postValue(safeData)
                currentCategory = "All"
                _searchQuery.postValue("")
                _filteredProducts.value = safeData

            } else {
                _allProducts.postValue(emptyList())
                _filteredProducts.postValue(emptyList())
                _message.postValue(msg)
            }
        }
    }



    fun filterByCategory(category: String) {
        currentCategory = category
        applyFilters()
    }



    fun refreshProducts() {
        getAllProduct()
    }


    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilters()
    }



    private fun applyFilters(query: String = _searchQuery.value ?: "") {
        val allProducts = cachedProducts

        // Category filter
        val categoryFiltered = if (currentCategory == "All") {
            allProducts
        } else {
            allProducts.filter {
                it.categoryId.equals(currentCategory, ignoreCase = true)
            }
        }

        // Search filter
        val finalFiltered = if (query.isBlank()) {
            categoryFiltered
        } else {
            categoryFiltered.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }

        _filteredProducts.value = finalFiltered
    }



    fun updateProductStock(
        productId: String,
        quantityToSubtract: Int,
        callback: (Boolean, String, Int?) -> Unit
    ) {
        _loading.postValue(true)
        repo.updateProductStock(productId, quantityToSubtract) { success, msg, newStock ->
            _loading.postValue(false)
            if (!success) {
                _message.postValue(msg)
            }
            callback(success, msg, newStock)
        }
    }


    fun clearMessage() {
        _message.postValue(null)
    }
}