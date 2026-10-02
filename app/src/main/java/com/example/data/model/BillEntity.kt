package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bills",
    foreignKeys = [
        ForeignKey(
            entity = TenantEntity::class,
            parentColumns = ["id"],
            childColumns = ["tenantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tenantId"]),
        Index(value = ["roomNumber"])
    ]
)
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tenantId: Long,
    val roomNumber: String,
    val tenantName: String,
    val tenantPhone: String,
    val monthYear: String, // e.g. "October 2026"
    val rentAmount: Double,
    val startMeterReading: Double,
    val endMeterReading: Double,
    val unitsConsumed: Double, // endMeterReading - startMeterReading
    val electricityRate: Double = 11.0, // Rs. 11 per unit
    val electricityAmount: Double, // unitsConsumed * electricityRate
    val additionalCharges: Double = 0.0,
    val additionalChargesNote: String = "",
    val totalAmount: Double, // rentAmount + electricityAmount + additionalCharges
    val paidAmount: Double = 0.0,
    val dueDate: Long,
    val createdDate: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, PARTIALLY_PAID, PAID, OVERDUE
    val notes: String = ""
) {
    val remainingBalance: Double
        get() = (totalAmount - paidAmount).coerceAtLeast(0.0)

    val isFullyPaid: Boolean
        get() = remainingBalance <= 0.001
}
