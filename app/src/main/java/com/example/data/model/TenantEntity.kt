package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tenants",
    indices = [Index(value = ["roomNumber"], unique = true)]
)
data class TenantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val roomNumber: String, // e.g. "G1", "F1", "S1"
    val floor: String = "Ground Floor", // e.g. "Ground Floor", "First Floor", "Second Floor"
    val tenantName: String,
    val phone: String,
    val monthlyRent: Double,
    val lastMeterReading: Double = 0.0,
    val accessCode: String, // 4-digit code e.g. "1010"
    val active: Boolean = true,
    val joinedDate: Long = System.currentTimeMillis()
)
