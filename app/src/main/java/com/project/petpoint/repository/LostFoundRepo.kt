package com.project.petpoint.repository

import android.content.Context
import android.net.Uri
import com.project.petpoint.model.LostFoundModel

interface LostFoundRepo {

    fun addReport(item: LostFoundModel, callback: (Boolean, String) -> Unit)

    fun updateReport(item: LostFoundModel, callback: (Boolean, String) -> Unit)

    fun deleteReport(lostId: String, callback: (Boolean, String) -> Unit)

    fun getReportById(lostId: String, callback: (Boolean, String, LostFoundModel?) -> Unit)

    fun getAllReports(callback: (Boolean, String, List<LostFoundModel>?) -> Unit)

    fun getReportsByType(type: String, callback: (Boolean, String, List<LostFoundModel>?) -> Unit)

    fun changeStatus(lostId: String, newType: String, callback: (Boolean, String) -> Unit)

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)
}