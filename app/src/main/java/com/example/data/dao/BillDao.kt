package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY createdDate DESC")
    fun getAllBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE tenantId = :tenantId ORDER BY createdDate DESC")
    fun getBillsForTenant(tenantId: Long): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE roomNumber = :roomNumber ORDER BY createdDate DESC")
    fun getBillsForRoom(roomNumber: String): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE id = :billId LIMIT 1")
    fun getBillById(billId: Long): Flow<BillEntity?>

    @Query("SELECT * FROM bills WHERE id = :billId LIMIT 1")
    suspend fun getBillByIdDirect(billId: Long): BillEntity?

    @Query("SELECT * FROM bills WHERE status != 'PAID' ORDER BY dueDate ASC")
    fun getPendingBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE tenantId = :tenantId AND status != 'PAID' ORDER BY dueDate ASC")
    fun getPendingBillsForTenant(tenantId: Long): Flow<List<BillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity): Long

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Delete
    suspend fun deleteBill(bill: BillEntity)

    @Query("DELETE FROM bills WHERE tenantId = :tenantId")
    suspend fun deleteBillsForTenant(tenantId: Long)

    @Query("UPDATE bills SET paidAmount = paidAmount + :amount, status = :status WHERE id = :billId")
    suspend fun recordPaymentOnBill(billId: Long, amount: Double, status: String)
}
