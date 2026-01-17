package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.petpoint.model.LostFoundModel
import com.project.petpoint.repository.LostFoundRepo

class LostFoundViewModel(private val repo: LostFoundRepo) : ViewModel() {

    // Main data sources
    private val _allReports = MutableLiveData<List<LostFoundModel>?>()
    val allReports: LiveData<List<LostFoundModel>?> get() = _allReports

    private val _filteredReports = MutableLiveData<List<LostFoundModel>?>()
    val filteredReports: LiveData<List<LostFoundModel>?> get() = _filteredReports

    private val _selectedReport = MutableLiveData<LostFoundModel?>()
    val selectedReport: LiveData<LostFoundModel?> get() = _selectedReport

    // UI state
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    val filterType = MutableLiveData("All")

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> get() = _message

    /**
     * Load all reports
     * @param includeHidden - true for management/admin view, false for public users
     */
    fun getAllReports(includeHidden: Boolean = false) {
        _loading.value = true

        repo.getAllReports { success, message, data ->
            _loading.value = false

            if (success && data != null) {
                val visibleOnly = data.filter { it.isVisible }
                _allReports.value = if (includeHidden) data else visibleOnly
                applyFiltersAndSearch()
            } else {
                _message.value = message ?: "Failed to load reports"
            }
        }
    }

    fun getReportById(lostId: String) {
        if (lostId.isBlank()) return

        _loading.value = true
        repo.getReportById(lostId) { success, msg, report ->
            _loading.value = false
            if (success) {
                _selectedReport.value = report
            } else {
                _message.value = msg
            }
        }
    }

    fun addReport(item: LostFoundModel, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        _loading.value = true
        repo.addReport(item) { success, msg ->
            _loading.value = false
            onResult(success, msg)

            if (success) {
                _message.value = "Report added successfully"
                // Refresh management view style (show all including newly added)
                getAllReports(includeHidden = true)
            } else {
                _message.value = msg
            }
        }
    }

    fun updateReport(item: LostFoundModel, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        if (item.lostId.isBlank()) {
            _message.value = "Cannot update: missing report ID"
            onResult(false, "Missing report ID")
            return
        }

        _loading.value = true
        repo.updateReport(item) { success, msg ->
            _loading.value = false
            onResult(success, msg)

            if (success) {
                _message.value = "Report updated successfully"
                getAllReports(includeHidden = true)
            } else {
                _message.value = msg
            }
        }
    }

    fun hideReport(lostId: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        if (lostId.isBlank()) {
            _message.value = "Invalid report ID"
            onResult(false, "Invalid report ID")
            return
        }

        _loading.value = true
        repo.hideReport(lostId) { success, msg ->
            _loading.value = false
            onResult(success, msg)

            if (success) {
                _message.value = "Report hidden successfully"
                // Most important line - FORCE new fetch instead of relying on cache
                getAllReports(includeHidden = true)   // management
                // OR if you want to be extra sure:
                // _allReports.value = null
                // getAllReports(includeHidden = false)  // for public too
            } else {
                _message.value = msg
            }
        }
    }

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadImage(context, imageUri, callback)
    }

    // Search & Filter handling
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFiltersAndSearch()
    }

    fun setFilterType(type: String) {
        filterType.value = type
        applyFiltersAndSearch()
    }

    private fun applyFiltersAndSearch() {
        val currentList = _allReports.value ?: emptyList()
        val query = _searchQuery.value?.trim()?.lowercase() ?: ""
        val selectedType = filterType.value ?: "All"

        val filteredList = currentList.filter { report ->
            // Search matching
            val matchesSearch = query.isEmpty() ||
                    report.title.lowercase().contains(query) ||
                    report.description.lowercase().contains(query) ||
                    report.location.lowercase().contains(query) ||
                    report.category.lowercase().contains(query)

            // Type filter matching
            val matchesType = selectedType == "All" ||
                    report.type.equals(selectedType, ignoreCase = true)

            matchesSearch && matchesType
        }

        _filteredReports.value = filteredList
    }

    fun clearMessage() {
        _message.value = null
    }

    // Convenience method for public screens
    fun refreshPublicReports() {
        getAllReports(includeHidden = false)
    }

    // Convenience method for management/admin screens
    fun refreshAllReports() {
        getAllReports(includeHidden = true)
    }
}