package com.project.petpoint.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.VetModel
import com.project.petpoint.repository.VetRepo

class VetViewModel(val repo: VetRepo) : ViewModel() {

    fun addDoctor(model: VetModel, callback: (Boolean, String) -> Unit) {
        repo.addDoctor(model, callback)
    }

    fun updateDoctor(model: VetModel, callback: (Boolean, String) -> Unit) {
        repo.updateDoctor(model, callback)
    }

    fun deleteDoctor(vetId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteDoctor(vetId, callback)
    }

    private val _doctor = MutableLiveData<VetModel?>()
    val doctor: MutableLiveData<VetModel?> get() = _doctor

    private val _allDoctors = MutableLiveData<List<VetModel>>(emptyList())
    val allDoctors: MutableLiveData<List<VetModel>> get() = _allDoctors

    fun getDoctorById(vetId: String) {
        repo.getDoctorById(vetId) { success, msg, data ->
            if (success) {
                _doctor.postValue(data)
            }
        }
    }

    fun getAllDoctors() {
        repo.getAllDoctors { success, msg, data ->
            if (success) {
                _allDoctors.postValue(data)
            }
        }
    }
}