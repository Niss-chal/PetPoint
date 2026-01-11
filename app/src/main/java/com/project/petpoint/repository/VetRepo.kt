package com.project.petpoint.repository

import android.content.Context
import android.net.Uri
import com.project.petpoint.model.VetModel

interface VetRepo {
    fun addDoctor(
        model: VetModel,
        callback: (Boolean, String) -> Unit
    )

    fun updateDoctor(
        model: VetModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteDoctor(
        vetId: String,
        callback: (Boolean, String) -> Unit
    )

    fun getDoctorById(
        vetId: String,
        callback: (Boolean, String, VetModel?) -> Unit
    )

    fun getAllDoctors(
        callback: (Boolean, String, List<VetModel>?) -> Unit
    )
}