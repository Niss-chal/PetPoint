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


    private val _products = MutableLiveData<ProductModel?>()
    val products : MutableLiveData<ProductModel?> get() = _products

    private val _allProducts = MutableLiveData<List<ProductModel>?>()
    val allProducts : MutableLiveData<List<ProductModel>?> get() = _allProducts

    private val _loading = MutableLiveData<Boolean>()
    val loading : MutableLiveData<Boolean> get()=_loading


    fun getProductById(productID:String){
        _loading.postValue(true)
        repo.getProductById(productID){
                success,msg,data->
            if(success){
                _loading.postValue(false)
                _products.postValue(data)
            }
            else{
                _loading.postValue(false)
                _products.postValue(null)
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
            }
            else{
                _loading.postValue(false)
                _allProducts.postValue(null)
            }
        }
    }


    private val _allProductsCategory = MutableLiveData<List<ProductModel>?>()
    val allProductsCategory : MutableLiveData<List<ProductModel>?> get() = _allProductsCategory

    fun getProductByCategory(categoryId:String){
        repo.getProductByCategory(categoryId){
                success,msg,data->
            if(success){
                _allProductsCategory.postValue(data)
            }
        }
    }


    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

}

