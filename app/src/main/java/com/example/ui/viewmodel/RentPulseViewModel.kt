package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RentPulseDatabase
import com.example.data.RentPulseRepository
import com.example.data.model.BillEntity
import com.example.data.model.MaintenanceEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TenantEntity
import com.example.util.BackupHelper
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppRole {
    ROLE_SELECT,
    LANDLORD,
    TENANT
}

class RentPulseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RentPulseRepository

    init {
        val db = RentPulseDatabase.getDatabase(application, viewModelScope)
        repository = RentPulseRepository(db.tenantDao(), db.billDao(), db.paymentDao(), db.maintenanceDao())
    }

    // Role state
    private val _appRole = MutableStateFlow(AppRole.ROLE_SELECT)
    val appRole: StateFlow<AppRole> = _appRole.asStateFlow()

    // Logged in tenant for tenant portal
    private val _currentTenant = MutableStateFlow<TenantEntity?>(null)
    val currentTenant: StateFlow<TenantEntity?> = _currentTenant.asStateFlow()

    // Language state (English / हिन्दी)
    private val _appLanguage = MutableStateFlow(com.example.util.AppLanguage.HI)
    val appLanguage: StateFlow<com.example.util.AppLanguage> = _appLanguage.asStateFlow()

    fun toggleLanguage() {
        _appLanguage.value = if (_appLanguage.value == com.example.util.AppLanguage.EN) com.example.util.AppLanguage.HI else com.example.util.AppLanguage.EN
    }

    fun setLanguage(lang: com.example.util.AppLanguage) {
        _appLanguage.value = lang
    }

    // UI Feedback messages
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Data streams
    val tenants: StateFlow<List<TenantEntity>> = repository.allTenants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBills: StateFlow<List<BillEntity>> = repository.allBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingBills: StateFlow<List<BillEntity>> = repository.pendingBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMaintenanceRequests: StateFlow<List<MaintenanceEntity>> = repository.allMaintenanceRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tenant-specific streams
    private val _tenantBills = MutableStateFlow<List<BillEntity>>(emptyList())
    val tenantBills: StateFlow<List<BillEntity>> = _tenantBills.asStateFlow()

    private val _tenantPayments = MutableStateFlow<List<PaymentEntity>>(emptyList())
    val tenantPayments: StateFlow<List<PaymentEntity>> = _tenantPayments.asStateFlow()

    private val _tenantMaintenance = MutableStateFlow<List<MaintenanceEntity>>(emptyList())
    val tenantMaintenance: StateFlow<List<MaintenanceEntity>> = _tenantMaintenance.asStateFlow()

    fun setRole(role: AppRole) {
        _appRole.value = role
        if (role == AppRole.ROLE_SELECT) {
            _currentTenant.value = null
        }
    }

    fun loginTenantByRoom(roomNumber: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val tenant = repository.getTenantByRoom(roomNumber)
            if (tenant != null) {
                _currentTenant.value = tenant
                _appRole.value = AppRole.TENANT
                loadTenantSpecificData(tenant.id)
                onResult(true, "Welcome Room ${tenant.roomNumber} portal!")
            } else {
                onResult(false, "Room not found")
            }
        }
    }

    private fun loadTenantSpecificData(tenantId: Long) {
        viewModelScope.launch {
            repository.getBillsForTenant(tenantId).collect { bills ->
                _tenantBills.value = bills
            }
        }
        viewModelScope.launch {
            repository.getPaymentsForTenant(tenantId).collect { payments ->
                _tenantPayments.value = payments
            }
        }
        viewModelScope.launch {
            repository.getMaintenanceForTenant(tenantId).collect { requests ->
                _tenantMaintenance.value = requests
            }
        }
    }

    fun addTenant(
        roomNumber: String,
        floor: String,
        tenantName: String,
        phone: String,
        monthlyRent: Double,
        lastMeterReading: Double,
        accessCode: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val tenant = TenantEntity(
                    roomNumber = roomNumber.trim(),
                    floor = floor.trim(),
                    tenantName = tenantName.trim(),
                    phone = phone.trim(),
                    monthlyRent = monthlyRent,
                    lastMeterReading = lastMeterReading,
                    accessCode = accessCode.trim()
                )
                repository.insertTenant(tenant)
                onComplete(true, "Room & Tenant added successfully!")
            } catch (e: Exception) {
                onComplete(false, "Error: Room number might already exist.")
            }
        }
    }

    fun deleteTenant(
        tenantId: Long,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.deleteTenantCascade(tenantId)
                if (_currentTenant.value?.id == tenantId) {
                    _currentTenant.value = null
                }
                onComplete(true, "Tenant and records deleted successfully!")
            } catch (e: Exception) {
                onComplete(false, "Failed to delete tenant: ${e.localizedMessage}")
            }
        }
    }

    fun updateTenantDetails(
        tenant: TenantEntity,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateTenant(tenant)
                if (_currentTenant.value?.id == tenant.id) {
                    _currentTenant.value = tenant
                }
                onComplete(true, "Tenant updated successfully!")
            } catch (e: Exception) {
                onComplete(false, "Failed to update: ${e.localizedMessage}")
            }
        }
    }

    fun createBill(
        tenantId: Long,
        monthYear: String,
        rentAmount: Double,
        startMeterReading: Double,
        endMeterReading: Double,
        additionalCharges: Double,
        additionalChargesNote: String,
        dueDate: Long,
        notes: String,
        onComplete: (Boolean, String, BillEntity?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val tenantObj = tenants.value.find { it.id == tenantId }
                if (tenantObj == null) {
                    onComplete(false, "Tenant not found", null)
                    return@launch
                }

                val units = (endMeterReading - startMeterReading).coerceAtLeast(0.0)
                val rate = 11.0 // Fixed Rs. 11 per unit
                val electricityAmt = units * rate
                val total = rentAmount + electricityAmt + additionalCharges

                val bill = BillEntity(
                    tenantId = tenantId,
                    roomNumber = tenantObj.roomNumber,
                    tenantName = tenantObj.tenantName,
                    tenantPhone = tenantObj.phone,
                    monthYear = monthYear,
                    rentAmount = rentAmount,
                    startMeterReading = startMeterReading,
                    endMeterReading = endMeterReading,
                    unitsConsumed = units,
                    electricityRate = rate,
                    electricityAmount = electricityAmt,
                    additionalCharges = additionalCharges,
                    additionalChargesNote = additionalChargesNote,
                    totalAmount = total,
                    paidAmount = 0.0,
                    dueDate = dueDate,
                    status = "PENDING",
                    notes = notes
                )

                val billId = repository.insertBill(bill)
                val createdBill = bill.copy(id = billId)

                repository.updateTenant(tenantObj.copy(lastMeterReading = endMeterReading))

                onComplete(true, "Bill generated successfully!", createdBill)
            } catch (e: Exception) {
                onComplete(false, "Failed to create bill: ${e.localizedMessage}", null)
            }
        }
    }

    fun recordPayment(
        billId: Long,
        tenantId: Long,
        roomNumber: String,
        tenantName: String,
        amount: Double,
        mode: String,
        ref: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val paymentId = repository.recordPayment(
                    billId = billId,
                    tenantId = tenantId,
                    roomNumber = roomNumber,
                    tenantName = tenantName,
                    amountPaid = amount,
                    paymentMode = mode,
                    transactionRef = ref
                )
                if (paymentId != -1L) {
                    onComplete(true, "Payment recorded successfully!")
                    if (_currentTenant.value?.id == tenantId) {
                        loadTenantSpecificData(tenantId)
                    }
                } else {
                    onComplete(false, "Failed to record payment.")
                }
            } catch (e: Exception) {
                onComplete(false, "Error recording payment: ${e.localizedMessage}")
            }
        }
    }

    fun logMaintenance(
        tenantId: Long,
        roomNumber: String,
        tenantName: String,
        title: String,
        description: String,
        category: String,
        priority: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val req = MaintenanceEntity(
                    tenantId = tenantId,
                    roomNumber = roomNumber,
                    tenantName = tenantName,
                    issueTitle = title,
                    issueDescription = description,
                    category = category,
                    priority = priority,
                    status = "PENDING",
                    requestDate = System.currentTimeMillis()
                )
                repository.insertMaintenance(req)
                onComplete(true, "Maintenance request logged successfully!")
                if (_currentTenant.value?.id == tenantId) {
                    loadTenantSpecificData(tenantId)
                }
            } catch (e: Exception) {
                onComplete(false, "Failed to log maintenance: ${e.localizedMessage}")
            }
        }
    }

    fun resolveMaintenance(requestId: Long, tenantId: Long?) {
        viewModelScope.launch {
            repository.updateMaintenanceStatus(requestId, "RESOLVED", System.currentTimeMillis())
            _toastMessage.value = "Maintenance request marked as resolved!"
            if (tenantId != null && _currentTenant.value?.id == tenantId) {
                loadTenantSpecificData(tenantId)
            }
        }
    }

    fun backupToGoogleDrive(context: android.content.Context) {
        try {
            val tList = tenants.value
            val bList = allBills.value
            val pList = allPayments.value
            BackupHelper.shareBackupToGoogleDrive(context, tList, bList, pList)
            _toastMessage.value = "Backup exported for Google Drive!"
        } catch (e: Exception) {
            _toastMessage.value = "Backup failed: ${e.localizedMessage}"
        }
    }

    fun broadcastReminders(context: android.content.Context) {
        val pending = pendingBills.value
        if (pending.isEmpty()) {
            _toastMessage.value = "No pending dues to broadcast!"
            return
        }
        val first = pending.first()
        val msg = NotificationHelper.generateBillMessage(first)
        NotificationHelper.openWhatsApp(context, first.tenantPhone, msg)
        _toastMessage.value = "Sending WhatsApp reminder to Room ${first.roomNumber} (${first.tenantName}, Phone: ${first.tenantPhone})"
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }
}
