package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.LostFoundModel
import com.project.petpoint.repository.LostFoundRepo

class LostFoundViewModel(private val repo: LostFoundRepo) : ViewModel() {

    private val _allReports = MutableLiveData<List<LostFoundModel>?>()
    val allReports get() = _allReports

    private val _filteredReports = MutableLiveData<List<LostFoundModel>?>()
    val filteredReports get() = _filteredReports

    private val _selectedReport = MutableLiveData<LostFoundModel?>()
    val selectedReport get() = _selectedReport

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery get() = _searchQuery

    val filterType = MutableLiveData("All")
    val filterStatus = MutableLiveData("All")

    private val _loading = MutableLiveData<Boolean>()
    val loading get() = _loading

    private val _message = MutableLiveData<String?>()
    val message get() = _message

    fun getAllReports() {
        _loading.value = true
        repo.getAllReports { success, msg, data ->
            _loading.value = false
            if (success) {
                _allReports.value = data
                applyFiltersAndSearch()
            } else {
                _message.value = msg
            }
        }
    }

    fun getReportById(lostId: String) {
        _loading.value = true
        repo.getReportById(lostId) { success, msg, data ->
            _loading.value = false
            if (success) {
                _selectedReport.value = data
            } else {
                _message.value = msg
            }
        }
    }

    fun addReport(item: LostFoundModel, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        _loading.value = true
        repo.addReport(item) { success, msg ->
            _loading.value = false
            callback(success, msg)
            if (success) {
                _message.value = "Report added successfully"
                getAllReports()  // force refresh after add
            } else {
                _message.value = msg
            }
        }
    }

    fun updateReport(item: LostFoundModel, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        if (item.lostId.isBlank()) {
            _message.value = "Cannot update: missing ID"
            callback(false, "Cannot update: missing ID")
            return
        }
        _loading.value = true
        repo.updateReport(item) { success, msg ->
            _loading.value = false
            if (success) {
                _message.value = "Report updated"
                // Refresh the list after updating
                getAllReports()
            } else {
                _message.value = msg
            }
            callback(success, msg)
        }
    }

    fun hideReport(lostId: String, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        _loading.value = true
        repo.hideReport(lostId) { success, msg ->
            _loading.value = false
            callback(success, msg)
            if (success) {
                _message.value = "Report hidden"
                getAllReports()  // force refresh after hide
            } else {
                _message.value = msg
            }
        }
    }

    fun deleteReport(lostId: String, callback: (Boolean, String) -> Unit = { _, _ -> }) {
        _loading.value = true
        repo.deleteReport(lostId) { success, msg ->
            _loading.value = false
            if (success) {
                _message.value = "Report deleted"
                // Refresh the list after deleting
                getAllReports()
            } else {
                _message.value = msg
            }
            callback(success, msg)
        }
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFiltersAndSearch()
    }

    fun setFilterType(type: String) {
        filterType.value = type
        applyFiltersAndSearch()
    }

    private fun applyFiltersAndSearch() {
        val list = _allReports.value ?: emptyList()
        val query = _searchQuery.value.orEmpty().trim().lowercase()
        val type = filterType.value ?: "All"

        val filtered = list.filter { report ->
            val matchesQuery = query.isEmpty() ||
                    report.title.lowercase().contains(query) ||
                    report.description.lowercase().contains(query) ||
                    report.location.lowercase().contains(query) ||
                    report.category.lowercase().contains(query)

            val matchesType = type == "All" || report.type == type
            matchesQuery && matchesType
        }

        _filteredReports.value = filtered
    }

    fun clearMessage() {
        _message.value = null
    }

    fun refreshReports() {
        getAllReports()
    }
}