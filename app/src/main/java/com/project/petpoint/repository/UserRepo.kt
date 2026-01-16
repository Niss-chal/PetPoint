package com.project.petpoint.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.project.petpoint.model.UserModel


interface UserRepo{

    fun login(
        email: String, password: String,
        callback: (Boolean, String)->Unit
    )

    fun updateProfile(
        userId: String, model: UserModel,
        callback: (Boolean,String) -> Unit
    )

    fun forgotPassword(
        email: String,
        callback: (Boolean,String) -> Unit
    )

    fun logout(
        callback: (Boolean, String) -> Unit
    )

    fun deleteAccount(
        userId: String, callback: (Boolean, String) -> Unit
    )

    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun getCurrentUser() : FirebaseUser?

    fun getAllUser(
        callback: (Boolean, String, List<UserModel>?) -> Unit
    )

    fun getUserById(
        userId: String,callback: (Boolean, String, UserModel?) -> Unit
    )

    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun uploadProfileImage(context: Context,
                           imageUri: Uri,
                           callback: (String?) -> Unit)

    fun updateProfileImage(
        userId: String,
        imageUrl: String
    )
}