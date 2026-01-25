package com.project.petpoint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.LostFoundModel
import com.project.petpoint.model.ProductModel
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.LostFoundRepo
import com.project.petpoint.repository.ProductRepo
import com.project.petpoint.repository.VetRepo

class HomeViewModel(
    private val productRepo: ProductRepo,
    private val vetRepo: VetRepo,
    private val lostFoundRepo: LostFoundRepo
) : ViewModel() {

    private val _totalProducts = MutableLiveData<Int>(0)
    val totalProducts: LiveData<Int> = _totalProducts

    private val _activeDoctors = MutableLiveData<Int>(0)
    val activeDoctors: LiveData<Int> = _activeDoctors

    private val _lostFoundPets = MutableLiveData<Int>(0)
    val lostFoundPets: LiveData<Int> = _lostFoundPets

    private val _recentProducts = MutableLiveData<List<ProductModel>>(emptyList())
    val recentProducts: LiveData<List<ProductModel>> = _recentProducts

    private val _recentVets = MutableLiveData<List<VetModel>>(emptyList())
    val recentVets: LiveData<List<VetModel>> = _recentVets

    private val _recentReports = MutableLiveData<List<LostFoundModel>>(emptyList())
    val recentReports: LiveData<List<LostFoundModel>> = _recentReports

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Load all overview data at once
     */
    fun loadOverviewData() {
        _loading.value = true
        _errorMessage.value = null

        // Counter to track completion of all requests
        var completedRequests = 0
        val totalRequests = 3

        fun checkCompletion() {
            completedRequests++
            if (completedRequests >= totalRequests) {
                _loading.value = false
            }
        }

        // Fetch total products and recent products
        productRepo.getAllProduct { success, message, data ->
            if (success) {
                _totalProducts.value = data?.size ?: 0
                // Get last 3 products added
                _recentProducts.value = data?.takeLast(3) ?: emptyList()
            } else {
                _errorMessage.value = message
            }
            checkCompletion()
        }

        // Fetch active doctors and recent vets
        vetRepo.getAllDoctors { success, message, data ->
            if (success) {
                _activeDoctors.value = data?.size ?: 0
                // Get last 3 vets added
                _recentVets.value = data?.takeLast(3) ?: emptyList()
            } else {
                _errorMessage.value = message
            }
            checkCompletion()
        }

        // Fetch lost & found pets
        lostFoundRepo.getAllReports { success, message, data ->
            if (success) {
                val visibleReports = data?.filter { it.isVisible } ?: emptyList()
                _lostFoundPets.value = visibleReports.size
                // Get last 3 reports
                _recentReports.value = visibleReports.takeLast(3)
            } else {
                _errorMessage.value = message
            }
            checkCompletion()
        }
    }

    /**
     * Refresh the overview data
     */
    fun refreshData() {
        loadOverviewData()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}