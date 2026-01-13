package com.project.petpoint.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.petpoint.model.VetModel

class VetRepoImpl : VetRepo {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("doctors")

    override fun addDoctor(model: VetModel, callback: (Boolean, String) -> Unit) {
        val vetId = ref.push().key ?: run {
            callback(false, "Failed to generate ID")
            return
        }
        val vet = model.copy(vetId = vetId)

        ref.child(vetId).setValue(vet)
            .addOnSuccessListener {
                callback(true, "Doctor added successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to add doctor")
            }
    }

    override fun updateDoctor(model: VetModel, callback: (Boolean, String) -> Unit) {
        if (model.vetId.isBlank()) {
            callback(false, "Invalid doctor ID")
            return
        }

        ref.child(model.vetId)
            .updateChildren(model.toMap())
            .addOnSuccessListener {
                callback(true, "Doctor updated successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update")
            }
    }

    override fun deleteDoctor(vetId: String, callback: (Boolean, String) -> Unit) {
        if (vetId.isBlank()) {
            callback(false, "Invalid doctor ID")
            return
        }

        ref.child(vetId)
            .removeValue()
            .addOnSuccessListener {
                callback(true, "Doctor deleted successfully")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to delete")
            }
    }

    override fun getDoctorById(vetId: String, callback: (Boolean, String, VetModel?) -> Unit) {
        if (vetId.isBlank()) {
            callback(false, "Invalid doctor ID", null)
            return
        }

        ref.child(vetId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    val vet = snapshot.getValue(VetModel::class.java)
                    callback(true, "Doctor fetched", vet)
                } else {
                    callback(false, "Doctor not found", null)
                }
            } else {
                callback(false, task.exception?.message ?: "Failed to fetch doctor", null)
            }
        }
    }

    override fun getAllDoctors(callback: (Boolean, String, List<VetModel>?) -> Unit) {
        ref.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val doctors = task.result.children.mapNotNull {
                    it.getValue(VetModel::class.java)
                }
                callback(true, "Success", doctors)
            } else {
                callback(false, task.exception?.message ?: "Failed to load doctors", null)
            }
        }
    }
}