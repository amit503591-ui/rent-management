package com.example.data

import com.example.data.dao.BillDao
import com.example.data.dao.MaintenanceDao
import com.example.data.dao.PaymentDao
import com.example.data.dao.TenantDao
import com.example.data.model.BillEntity
import com.example.data.model.MaintenanceEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TenantEntity
import kotlinx.coroutines.flow.Flow

class RentPulseRepository(
    private val tenantDao: TenantDao,
    private val billDao: BillDao,
    private val paymentDao: PaymentDao,
    private val maintenanceDao: MaintenanceDao
) {
    val allTenants: Flow<List<TenantEntity>> = tenantDao.getAllActiveTenants()
    val allBills: Flow<List<BillEntity>> = billDao.getAllBills()
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    val pendingBills: Flow<List<BillEntity>> = billDao.getPendingBills()
    val allMaintenanceRequests: Flow<List<MaintenanceEntity>> = maintenanceDao.getAllMaintenanceRequests()

    fun getTenantById(id: Long): Flow<TenantEntity?> = tenantDao.getTenantById(id)
    fun getBillsForTenant(tenantId: Long): Flow<List<BillEntity>> = billDao.getBillsForTenant(tenantId)
    fun getPendingBillsForTenant(tenantId: Long): Flow<List<BillEntity>> = billDao.getPendingBillsForTenant(tenantId)
    fun getPaymentsForTenant(tenantId: Long): Flow<List<PaymentEntity>> = paymentDao.getPaymentsForTenant(tenantId)
    fun getMaintenanceForTenant(tenantId: Long): Flow<List<MaintenanceEntity>> = maintenanceDao.getMaintenanceForTenant(tenantId)

    suspend fun getTenantByRoom(roomNumber: String): TenantEntity? {
        return tenantDao.getTenantByRoom(roomNumber.trim())
    }

    suspend fun authenticateTenant(roomNumber: String, code: String): TenantEntity? {
        return tenantDao.authenticateTenant(roomNumber.trim(), code.trim())
    }

    suspend fun insertTenant(tenant: TenantEntity): Long = tenantDao.insertTenant(tenant)
    suspend fun updateTenant(tenant: TenantEntity) = tenantDao.updateTenant(tenant)
    suspend fun deleteTenant(tenant: TenantEntity) = tenantDao.deleteTenant(tenant)

    suspend fun deleteTenantCascade(tenantId: Long) {
        billDao.deleteBillsForTenant(tenantId)
        paymentDao.deletePaymentsForTenant(tenantId)
        maintenanceDao.deleteMaintenanceForTenant(tenantId)
        tenantDao.deleteTenantById(tenantId)
    }

    suspend fun insertBill(bill: BillEntity): Long = billDao.insertBill(bill)
    suspend fun updateBill(bill: BillEntity) = billDao.updateBill(bill)

    suspend fun insertMaintenance(request: MaintenanceEntity): Long = maintenanceDao.insertRequest(request)
    suspend fun updateMaintenanceStatus(requestId: Long, status: String, resolvedDate: Long?) =
        maintenanceDao.updateStatus(requestId, status, resolvedDate)

    suspend fun recordPayment(
        billId: Long,
        tenantId: Long,
        roomNumber: String,
        tenantName: String,
        amountPaid: Double,
        paymentMode: String,
        transactionRef: String
    ): Long {
        val bill = billDao.getBillByIdDirect(billId) ?: return -1L
        val newPaid = bill.paidAmount + amountPaid
        val newStatus = if (newPaid >= bill.totalAmount - 0.01) "PAID" else "PARTIALLY_PAID"

        billDao.recordPaymentOnBill(billId, amountPaid, newStatus)

        val receiptNo = "REC-${roomNumber}-${System.currentTimeMillis().toString().takeLast(4)}"
        val payment = PaymentEntity(
            billId = billId,
            tenantId = tenantId,
            roomNumber = roomNumber,
            tenantName = tenantName,
            amountPaid = amountPaid,
            paymentMode = paymentMode,
            transactionRef = transactionRef,
            receiptNumber = receiptNo
        )
        return paymentDao.insertPayment(payment)
    }
}
