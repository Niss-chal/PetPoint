package com.project.petpoint.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.VetRepo

class VetViewModel(private val repo: VetRepo) : ViewModel() {

    private val _allDoctors = MutableLiveData<List<VetModel>>(emptyList())
    val allDoctors: LiveData<List<VetModel>> = _allDoctors

    private val _selectedDoctor = MutableLiveData<VetModel?>()
    val selectedDoctor: LiveData<VetModel?> = _selectedDoctor

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun getAllDoctors() {
        _loading.value = true
        repo.getAllDoctors { success, message, data ->
            _loading.value = false
            if (success) {
                _allDoctors.value = data ?: emptyList()
                _errorMessage.value = null
            } else {
                _errorMessage.value = message
            }
        }
    }

    fun getDoctorById(vetId: String) {
        _loading.value = true
        repo.getDoctorById(vetId) { success, message, data ->
            _loading.value = false
            if (success) {
                _selectedDoctor.value = data
                _errorMessage.value = null
            } else {
                _selectedDoctor.value = null
                _errorMessage.value = message
            }
        }
    }

    fun addDoctor(model: VetModel, onResult: (Boolean, String) -> Unit) {
        if (!validateVetModel(model)) {
            onResult(false, "Please fill all required fields correctly")
            return
        }
        repo.addDoctor(model) { success, message ->
            if (success) getAllDoctors() // refresh list
            onResult(success, message)
        }
    }

    fun updateDoctor(model: VetModel, onResult: (Boolean, String) -> Unit) {
        if (!validateVetModel(model) || model.vetId.isBlank()) {
            onResult(false, "Invalid or incomplete doctor data")
            return
        }
        repo.updateDoctor(model) { success, message ->
            if (success) getAllDoctors() // refresh list
            onResult(success, message)
        }
    }

    fun deleteDoctor(vetId: String, onResult: (Boolean, String) -> Unit) {
        repo.deleteDoctor(vetId, onResult)
    }

    private fun validateVetModel(model: VetModel): Boolean {
        return model.name.trim().isNotEmpty() &&
                model.specialization.trim().isNotEmpty() &&
                model.email.trim().isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(model.email).matches() &&
                model.phonenumber.trim().length >= 8
    }

    fun clearSelectedDoctor() {
        _selectedDoctor.value = null
    }
}