package com.project.petpoint.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

object AuthUtils {

    private const val ADMINS_PATH = "admins"

    /**
     * Checks if current user is admin by looking up in /admins node
     * Real apps should cache this result (DataStore/SharedPreferences)
     */
    suspend fun isCurrentUserAdmin(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser ?: return false
        val uid = user.uid

        return try {
            val snapshot = FirebaseDatabase.getInstance()
                .getReference(ADMINS_PATH)
                .child(uid)
                .get()
                .await()

            snapshot.exists() && snapshot.getValue(Boolean::class.java) == true
        } catch (e: Exception) {
            false
        }
    }

    fun isOwnerOfReport(reportReportedByUid: String?): Boolean {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        return currentUid == reportReportedByUid
    }

    fun canManageReport(reportReportedByUid: String?): Boolean {
        // Note: this is only for UI logic – real protection comes from rules
        return isOwnerOfReport(reportReportedByUid)
        // isAdmin is checked async → use isCurrentUserAdmin() where needed
    }
}