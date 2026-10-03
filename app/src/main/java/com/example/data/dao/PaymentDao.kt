package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE tenantId = :tenantId ORDER BY paymentDate DESC")
    fun getPaymentsForTenant(tenantId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE billId = :billId ORDER BY paymentDate DESC")
    fun getPaymentsForBill(billId: Long): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Query("DELETE FROM payments WHERE tenantId = :tenantId")
    suspend fun deletePaymentsForTenant(tenantId: Long)
}
