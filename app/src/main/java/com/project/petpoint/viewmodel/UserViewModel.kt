package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.petpoint.model.UserModel
import com.project.petpoint.repository.UserRepo

class UserViewModel(val repo: UserRepo) : ViewModel() {

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.login(email, password, callback)
    }

    fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateProfile(userId, model, callback)
    }

    fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.forgotPassword(email, callback)
    }

    fun logout(
        callback: (Boolean, String) -> Unit
    ) {
        repo.logout(callback)
    }

    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.deleteAccount(userId, callback)
    }

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        repo.register(email, password, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean>
        get() = _loading

    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers: MutableLiveData<List<UserModel>?>
        get() = _allUsers

    fun getAllUser() {
        _loading.postValue(true)
        repo.getAllUser { success, _, data ->
            if (success) {
                _loading.postValue(false)
                _allUsers.postValue(data)
            }
        }
    }

    private val _users = MutableLiveData<UserModel?>()
    val users: MutableLiveData<UserModel?>
        get() = _users

    fun getUserById(
        userId: String
    ) {
        _loading.postValue(true)
        repo.getUserById(userId) { success, _, data ->
            if (success) {
                _loading.postValue(false)
                _users.postValue(data)
            }
        }
    }

    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addUserToDatabase(userId, model, callback)
    }

    // Upload image to Cloudinary
    fun uploadProfileImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    ) {
        repo.uploadProfileImage(context, imageUri, callback)
    }
}
