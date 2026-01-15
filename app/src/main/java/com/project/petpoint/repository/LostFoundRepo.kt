package com.project.petpoint.repository

import com.project.petpoint.model.LostFoundModel

interface LostFoundRepo {
    fun addReport(item: LostFoundModel, callback: (Boolean, String) -> Unit)

    fun updateReport(item: LostFoundModel, callback: (Boolean, String) -> Unit)

    fun deleteReport(lostId: String, callback: (Boolean, String) -> Unit)

    fun getReportById(lostId: String, callback: (Boolean, String, LostFoundModel?) -> Unit)

    fun getAllReports(callback: (Boolean, String, List<LostFoundModel>?) -> Unit)

    // Optional: filter by type ("Lost" / "Found")
    fun getReportsByType(type: String, callback: (Boolean, String, List<LostFoundModel>?) -> Unit)

    fun uploadImage(context: android.content.Context, imageUri: android.net.Uri, callback: (String?) -> Unit)
}