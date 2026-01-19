package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.petpoint.model.LostFoundModel
import com.project.petpoint.repository.LostFoundRepo
import com.project.petpoint.utils.AuthUtils
import kotlinx.coroutines.launch

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

    // Admin status (checked once on init)
    private val _isAdmin = MutableLiveData<Boolean>(false)
    val isAdmin: LiveData<Boolean> get() = _isAdmin

    init {
        viewModelScope.launch {
            _isAdmin.value = AuthUtils.isCurrentUserAdmin()
        }
    }

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
                _allReports.value = if (includeHidden || _isAdmin.value == true) data else visibleOnly
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
                _message.value = msg ?: "Report not found"
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
                getAllReports(includeHidden = true)
            } else {
                _message.value = msg ?: "Failed to add report"
            }
        }
    }

    fun updateReport(item: LostFoundModel, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        _loading.value = true
        repo.updateReport(item) { success, msg ->
            _loading.value = false
            onResult(success, msg)

            if (success) {
                _message.value = "Report updated successfully"
                getAllReports(includeHidden = true)
            } else {
                _message.value = msg ?: "Failed to update report"
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
        println("DEBUG VM: Calling hideReport for $lostId")

        repo.hideReport(lostId) { success, msg ->
            _loading.value = false
            onResult(success, msg)

            println("DEBUG VM: hideReport callback - success=$success, msg=$msg")

            if (success) {
                _message.value = "Report hidden successfully"
                getAllReports(includeHidden = true)  // force refresh
            } else {
                _message.value = msg ?: "Failed to hide report"
            }
        }
    }

    fun unhideReport(lostId: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        if (lostId.isBlank()) {
            _message.value = "Invalid report ID"
            onResult(false, "Invalid report ID")
            return
        }

        _loading.value = true
        repo.unhideReport(lostId) { success, msg ->
            _loading.value = false
            onResult(success, msg)

            if (success) {
                _message.value = "Report restored successfully"
                getAllReports(includeHidden = true)  // refresh management view
            } else {
                _message.value = msg ?: "Failed to restore report"
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
            val matchesSearch = query.isEmpty() ||
                    report.title.lowercase().contains(query) ||
                    report.description.lowercase().contains(query) ||
                    report.location.lowercase().contains(query) ||
                    report.category.lowercase().contains(query)

            val matchesType = selectedType == "All" ||
                    report.type.equals(selectedType, ignoreCase = true)

            matchesSearch && matchesType
        }

        _filteredReports.value = filteredList
    }

    fun clearMessage() {
        _message.value = null
    }

    // Convenience methods
    fun refreshPublicReports() {
        getAllReports(includeHidden = false)
    }

    fun refreshAllReports() {
        getAllReports(includeHidden = true)
    }
}