package com.project.petpoint.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.petpoint.model.VetModel

class VetRepoImpl : VetRepo{
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("doctors")

    override fun addDoctor(
        model: VetModel,
        callback: (Boolean, String) -> Unit
    ) {
        val vetId = ref.push().key ?: return
        val vet = model.copy(vetId = vetId)

        ref.child(vetId).setValue(vet)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Doctor added successfully")
                } else {
                    callback(false, it.exception?.message.toString())
                }
            }
    }

    override fun updateDoctor(
        model: VetModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(model.vetId)
            .updateChildren(model.toMap())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Doctor updated successfully")
                } else {
                    callback(false, it.exception?.message.toString())
                }
            }
    }

    override fun deleteDoctor(
        vetId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(vetId)
            .removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Doctor deleted successfully")
                } else {
                    callback(false, it.exception?.message.toString())
                }
            }
    }

    override fun getDoctorById(
        vetId: String,
        callback: (Boolean, String, VetModel?) -> Unit
    ) {
        ref.child(vetId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val vet = snapshot.getValue(VetModel::class.java)
                        callback(true, "Doctor fetched successfully", vet)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun getAllDoctors(
        callback: (Boolean, String, List<VetModel>?) -> Unit
    ) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val doctors = mutableListOf<VetModel>()
                    for (data in snapshot.children) {
                        val vet = data.getValue(VetModel::class.java)
                        if (vet != null) {
                            doctors.add(vet)
                        }
                    }
                    callback(true, "Doctors fetched successfully", doctors)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }
}