package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    // Admin role check
    private val _isAdmin = MutableLiveData<Boolean>()
    val isAdmin: LiveData<Boolean> get() = _isAdmin

    /**
     * Check if current user is admin
     */
    fun checkAdminStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _isAdmin.value = false
            return
        }

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(currentUser.uid)
            .child("role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.getValue(String::class.java)
                    _isAdmin.value = role == "admin"
                }

                override fun onCancelled(error: DatabaseError) {
                    _isAdmin.value = false
                }
            })
    }

    /**
     * Check if user can manage a specific report
     * @return true if user is owner OR admin
     */
    fun canManageReport(report: LostFoundModel): Boolean {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        val isOwner = report.reportedBy == currentUid
        val adminStatus = _isAdmin.value ?: false
        return isOwner || adminStatus
    }

    /**
     * Load all reports
     */
    fun getAllReports() {
        _loading.value = true

        repo.getAllReports { success, message, data ->
            _loading.value = false

            if (success && data != null) {
                _allReports.value = data
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
                refreshReports()
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
                refreshReports()
            } else {
                _message.value = msg
            }
        }
    }

    fun deleteReport(lostId: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        if (lostId.isBlank()) {
            _message.value = "Invalid report ID"
            onResult(false, "Invalid report ID")
            return
        }

        _loading.value = true
        repo.deleteReport(lostId) { success, msg ->
            _loading.value = false
            onResult(success, msg)

            if (success) {
                _message.value = "Report deleted successfully"
                refreshReports()
            } else {
                _message.value = msg
            }
        }
    }

    fun changeStatus(lostId: String, newType: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        if (lostId.isBlank()) {
            _message.value = "Invalid report ID"
            onResult(false, "Invalid report ID")
            return
        }

        // Prevent going back to Lost from Found or Rescued
        if (newType.equals("Lost", ignoreCase = true)) {
            _message.value = "Cannot change a Found or Rescued report back to Lost"
            onResult(false, "Cannot change a Found or Rescued report back to Lost")
            return
        }

        _loading.value = true
        repo.changeStatus(lostId, newType) { success, msg ->
            _loading.value = false
            onResult(success, msg)

            if (success) {
                _message.value = "Status changed to $newType"
                refreshReports()
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

    fun refreshReports() {
        _allReports.value = null
        getAllReports()
    }
}