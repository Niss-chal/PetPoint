package com.project.petpoint.model

data class UserModel(
    val userId: String = "",
    val name: String = "",
    val address: String = "",
    val email: String = "",
    val phonenumber: String = "",

){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "userId" to userId,
            "name" to name,
            "address" to address,
            "email" to email,
            "phonenumber" to phonenumber,
        )
    }
}
