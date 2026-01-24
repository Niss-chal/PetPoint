package com.project.petpoint.model

data class UserModel(
    var userId: String = "",
    var name: String = "",
    var address: String = "",
    var email: String = "",
    var phonenumber: String = "",
    var role: String = "buyer",
    var profileImage: String? = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "name" to name,
            "address" to address,
            "email" to email,
            "phonenumber" to phonenumber,
            "role" to role,
            "profileImage" to profileImage
        )
    }
}
