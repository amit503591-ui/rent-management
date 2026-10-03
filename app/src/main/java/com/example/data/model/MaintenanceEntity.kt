package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_requests")
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tenantId: Long,
    val roomNumber: String,
    val tenantName: String,
    val issueTitle: String,
    val issueDescription: String,
    val category: String, // e.g., "Electrical", "Plumbing", "Appliance"
    val status: String = "PENDING", // PENDING, IN_PROGRESS, RESOLVED
    val priority: String = "Normal", // Normal, Urgent
    val requestDate: Long = System.currentTimeMillis(),
    val resolvedDate: Long? = null
)
