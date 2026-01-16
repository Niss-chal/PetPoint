package com.project.petpoint.model

data class LostFoundModel(
    val lostId: String = "",
    val type: String = "",
    val status: String = "Pending",
    val title: String = "",
    val category: String = "",
    val description: String = "",
    val location: String = "",
    val date: String = "",
    val reportedBy: String = "",
    val reportedByName: String? = null,
    val imageUrl: String = "",
    val contactInfo: String = "",
    val isVisible: Boolean = true
) {

    fun toMap(): Map<String, Any?> = mapOf(
        "lostId" to lostId,
        "type" to type,
        "status" to status,
        "title" to title,
        "category" to category,
        "description" to description,
        "location" to location,
        "date" to date,
        "reportedBy" to reportedBy,
        "reportedByName" to reportedByName,
        "imageUrl" to imageUrl,
        "contactInfo" to contactInfo,
        "isVisible" to isVisible
    )
}