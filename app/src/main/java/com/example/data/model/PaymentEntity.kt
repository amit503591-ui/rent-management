package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["billId"]),
        Index(value = ["tenantId"])
    ]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val billId: Long,
    val tenantId: Long,
    val roomNumber: String,
    val tenantName: String,
    val amountPaid: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val paymentMode: String = "UPI", // UPI, Cash, Bank Transfer, Cheque
    val transactionRef: String = "",
    val receiptNumber: String
)
