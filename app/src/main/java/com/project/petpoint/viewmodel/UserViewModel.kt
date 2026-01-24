package com.project.petpoint.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.project.petpoint.model.UserModel
import com.project.petpoint.repository.UserRepo

class UserViewModel(val repo: UserRepo) : ViewModel() {

    // LiveData
    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _allUsers = MutableLiveData<List<UserModel>?>()
    val allUsers: MutableLiveData<List<UserModel>?> get() = _allUsers

    private val _users = MutableLiveData<UserModel?>()
    val users: MutableLiveData<UserModel?> get() = _users

    // Authentication
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repo.login(email, password, callback)
    }

    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        repo.register(email, password, callback)
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repo.logout { success, message ->
            if (success) {
                _users.postValue(null)
                _allUsers.postValue(null)
            }
            callback(success, message)
        }
    }


    fun forgotPassword(email: String, callback: (Boolean, String) -> Unit) {
        repo.forgotPassword(email, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repo.getCurrentUser()
    }

    // User Management
    fun getUserById(userId: String) {
        _loading.postValue(true)
        repo.getUserById(userId) { success, _, data ->
            if (success) {
                _loading.postValue(false)
                _users.postValue(data)
            }
        }
    }

    fun getAllUser() {
        _loading.postValue(true)
        repo.getAllUser { success, _, data ->
            if (success) {
                _loading.postValue(false)
                _allUsers.postValue(data)
            }
        }
    }

    fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.addUserToDatabase(userId, model, callback)
    }

    fun updateProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        repo.updateProfile(userId, model, callback)
    }

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            callback(false, "No user logged in")
            return
        }

        // Delete from Realtime Database
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .removeValue()
            .addOnCompleteListener { dbTask ->
                // Delete from Firebase Authentication
                currentUser.delete()
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            callback(true, "Account deleted successfully")
                        } else {
                            callback(false, "Error: ${authTask.exception?.message}")
                        }
                    }
            }
    }

    // Profile Image
    fun uploadProfileImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        repo.uploadProfileImage(context, imageUri, callback)
    }

    fun updateProfileImage(userId: String, imageUrl: String) {
        repo.updateProfileImage(userId, imageUrl)
    }
}