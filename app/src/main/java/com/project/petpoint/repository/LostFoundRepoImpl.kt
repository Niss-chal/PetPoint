package com.project.petpoint.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.database.*
import com.project.petpoint.model.LostFoundModel
import java.io.InputStream
import java.util.concurrent.Executors

class LostFoundRepoImpl : LostFoundRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("lostfound")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dlnlxghqk",
            "api_key" to "429244379134354",
            "api_secret" to "p4NzH01x2uIdfGtYvk0sBiunpSA"
        )
    )

    override fun addReport(item: LostFoundModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key ?: return callback(false, "Failed to generate ID")
        val newItem = item.copy(lostId = id, isVisible = true)  // Force visible

        ref.child(id).setValue(newItem).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Report added successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error")
            }
        }
    }

    override fun updateReport(item: LostFoundModel, callback: (Boolean, String) -> Unit) {
        if (item.lostId.isBlank()) {
            callback(false, "Invalid report ID")
            return
        }
        ref.child(item.lostId).updateChildren(item.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Report updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error")
            }
        }
    }


    override fun getReportById(lostId: String, callback: (Boolean, String, LostFoundModel?) -> Unit) {
        ref.child(lostId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val item = snapshot.getValue(LostFoundModel::class.java)
                    if (item != null) {
                        callback(true, "Report found", item)
                    } else {
                        callback(false, "Failed to parse report", null)
                    }
                } else {
                    callback(false, "Report not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getAllReports(callback: (Boolean, String, List<LostFoundModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<LostFoundModel>()
                for (child in snapshot.children) {
                    val item = child.getValue(LostFoundModel::class.java)
                    item?.let { items.add(it) }  // return ALL items - filtering in ViewModel
                }
                callback(true, "Success", items)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getReportsByType(type: String, callback: (Boolean, String, List<LostFoundModel>?) -> Unit) {
        ref.orderByChild("type")
            .equalTo(type)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<LostFoundModel>()
                    for (child in snapshot.children) {
                        val item = child.getValue(LostFoundModel::class.java)
                        if (item != null && item.isVisible) items.add(item)
                    }
                    callback(true, "Reports fetched", items)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, null)
                }
            })
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri) ?: "lostfound_${System.currentTimeMillis()}"

                fileName = fileName.substringBeforeLast(".")

                val response = cloudinary.uploader().upload(
                    inputStream,
                    ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var url = response["url"] as? String
                url = url?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post { callback(url) }
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post { callback(null) }
            }
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = it.getString(index)
            }
        }
        return name
    }

    override fun hideReport(lostId: String, callback: (Boolean, String) -> Unit) {
        ref.child(lostId)
            .child("isVisible")
            .setValue(false)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Report hidden successfully")
                } else {
                    callback(false, it.exception?.message ?: "Failed to hide report")
                }
            }
    }
}