package com.project.petpoint.model

import com.google.android.gms.common.util.CollectionUtils.mapOf

data class VetModel(
    val vetId: String = "",
    val name: String = "",
    val specialization: String = "",
    val email: String = "",
    val phonenumber: String = "",
    val schedule: String = "",
    val address: String = "",
){
    fun toMap(): Map<String, Any?> = mapOf(
            "vetId" to vetId,
            "name" to name,
            "specialization" to specialization,
            "email" to email,
            "phonenumber" to phonenumber,
            "schedule" to schedule,
            "address" to address,
        )
    }
