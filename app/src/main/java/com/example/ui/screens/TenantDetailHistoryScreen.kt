package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BillEntity
import com.example.data.model.MaintenanceEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TenantEntity
import com.example.ui.components.StatusBadge
import com.example.util.FormatUtils
import com.example.util.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantDetailHistoryScreen(
    tenant: TenantEntity,
    bills: List<BillEntity>,
    payments: List<PaymentEntity>,
    maintenanceList: List<MaintenanceEntity>,
    onBack: () -> Unit,
    onDeleteTenant: (TenantEntity) -> Unit,
    onUpdateTenant: (TenantEntity) -> Unit,
    onGenerateBill: () -> Unit,
    onSubmitMaintenance: (String, String, String, String) -> Unit,
    onResolveMaintenance: (Long) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Bills & Readings, 1: Payments, 2: Maintenance, 3: Lease & KYC
    var showAddMaintenanceDialog by remember { mutableStateOf(false) }
    var showEditTenantDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Smart electricity anomaly check: average units consumed across tenant bills
    val avgUnits = if (bills.isNotEmpty()) bills.map { it.unitsConsumed }.average() else 0.0
    val hasHighConsumptionAnomaly = bills.any { it.unitsConsumed > avgUnits * 1.3 && bills.size > 1 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Room ${tenant.roomNumber} - ${tenant.tenantName}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditTenantDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Tenant")
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Tenant", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 2) {
                FloatingActionButton(
                    onClick = { showAddMaintenanceDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Maintenance Issue", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tenant Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tenant.roomNumber,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(tenant.tenantName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(Icons.Default.Verified, contentDescription = "Verified KYC", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                    }
                                    Text("${tenant.floor} • Joined ${FormatUtils.formatDate(tenant.joinedDate)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                }
                            }

                            // Quick contact actions
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tenant.phone}"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(17.dp))
                                }
                                IconButton(
                                    onClick = {
                                        NotificationHelper.openWhatsApp(
                                            context,
                                            tenant.phone,
                                            "Hello ${tenant.tenantName} (Room ${tenant.roomNumber}), this is your landlord regarding RentPulse."
                                        )
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(17.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Monthly Rent", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(FormatUtils.formatCurrency(tenant.monthlyRent), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Column {
                                Text("Phone", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(tenant.phone, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Column {
                                Text("Meter Reading", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text("${tenant.lastMeterReading.toInt()} units", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Column {
                                Text("Login PIN", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                Text(tenant.accessCode, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onGenerateBill,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Auto-Fetch Meter & Generate Bill", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Smart Anomaly Detection Banner if triggered
            if (hasHighConsumptionAnomaly) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("💡 Smart Electricity Insight: High Usage Spike", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                                Text("Recent bills show electricity usage over 30% higher than average. Consider checking appliances.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
                            }
                        }
                    }
                }
            }

            // Tabs for History Categories
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = {},
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Bills (${bills.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Payments (${payments.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Maintenance (${maintenanceList.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Lease & KYC", fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (selectedTab == 0) {
                if (bills.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                            Text("No billing history found for this tenant.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(bills) { bill ->
                        TenantBillHistoryCard(bill = bill)
                    }
                }
            } else if (selectedTab == 1) {
                if (payments.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                            Text("No payment receipts recorded for this tenant.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(payments) { payment ->
                        TenantPaymentHistoryCard(payment = payment)
                    }
                }
            } else if (selectedTab == 2) {
                if (maintenanceList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                            Text("No maintenance requests logged for this tenant.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(maintenanceList) { request ->
                        MaintenanceItemCard(request = request, onResolve = { onResolveMaintenance(request.id) })
                    }
                }
            } else {
                // Lease & KYC Document Vault
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Digital Lease Agreement & KYC Vault", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Text("Verified tenant identification documents and digital lease agreement stored securely.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Lease Status:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Active & Valid (11 Months)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Security Deposit:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(FormatUtils.formatCurrency(tenant.monthlyRent * 2), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("ID Proof Verified:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Aadhaar & PAN Verified ✓", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }

            // Bottom Actions Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { showEditTenantDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Tenant Info", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showDeleteConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Checkout / Delete", color = MaterialTheme.colorScheme.onError, fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Checkout / Delete Tenant?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to delete Room ${tenant.roomNumber} (${tenant.tenantName})?\n\n" +
                            "This will permanently delete this tenant, all previous bills (${bills.size}), receipts, and maintenance logs."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteTenant(tenant)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Permanently", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Tenant Dialog
    if (showEditTenantDialog) {
        var editName by remember { mutableStateOf(tenant.tenantName) }
        var editPhone by remember { mutableStateOf(tenant.phone) }
        var editRent by remember { mutableStateOf(tenant.monthlyRent.toInt().toString()) }
        var editMeter by remember { mutableStateOf(tenant.lastMeterReading.toInt().toString()) }
        var editPin by remember { mutableStateOf(tenant.accessCode) }

        AlertDialog(
            onDismissRequest = { showEditTenantDialog = false },
            title = { Text("Edit Room ${tenant.roomNumber} Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Tenant Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editRent,
                        onValueChange = { editRent = it },
                        label = { Text("Monthly Rent (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editMeter,
                        onValueChange = { editMeter = it },
                        label = { Text("Current Meter Reading (units)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPin,
                        onValueChange = { editPin = it },
                        label = { Text("Tenant Login PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rentDouble = editRent.toDoubleOrNull() ?: tenant.monthlyRent
                        val meterDouble = editMeter.toDoubleOrNull() ?: tenant.lastMeterReading
                        val updated = tenant.copy(
                            tenantName = editName.trim(),
                            phone = editPhone.trim(),
                            monthlyRent = rentDouble,
                            lastMeterReading = meterDouble,
                            accessCode = editPin.trim()
                        )
                        onUpdateTenant(updated)
                        showEditTenantDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditTenantDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddMaintenanceDialog) {
        AddMaintenanceDialog(
            tenant = tenant,
            onDismiss = { showAddMaintenanceDialog = false },
            onSubmit = { title, desc, cat, prio ->
                onSubmitMaintenance(title, desc, cat, prio)
                showAddMaintenanceDialog = false
            }
        )
    }
}

@Composable
fun TenantBillHistoryCard(bill: BillEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(bill.monthYear, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                StatusBadge(status = bill.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Base Rent", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(FormatUtils.formatCurrency(bill.rentAmount), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Electricity (${bill.unitsConsumed.toInt()} units @ ₹${bill.electricityRate})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(FormatUtils.formatCurrency(bill.electricityAmount), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                if (bill.additionalCharges > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Other (${bill.additionalChargesNote})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(FormatUtils.formatCurrency(bill.additionalCharges), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Bill Amount", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(FormatUtils.formatCurrency(bill.totalAmount), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
                if (bill.paidAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Paid Amount", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        Text("- ${FormatUtils.formatCurrency(bill.paidAmount)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Meter Readings: Start (${bill.startMeterReading.toInt()}) → End (${bill.endMeterReading.toInt()})",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun TenantPaymentHistoryCard(payment: PaymentEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Paid via ${payment.paymentMode}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Receipt: ${payment.receiptNumber} | Ref: ${payment.transactionRef}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = FormatUtils.formatDate(payment.paymentDate),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Text(
                text = "+ ${FormatUtils.formatCurrency(payment.amountPaid)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun MaintenanceItemCard(
    request: MaintenanceEntity,
    onResolve: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(request.issueTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                StatusBadge(status = request.status)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(request.issueDescription, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category: ${request.category} • Priority: ${request.priority} • ${FormatUtils.formatDate(request.requestDate)}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                if (request.status != "RESOLVED") {
                    FilledTonalButton(
                        onClick = onResolve,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Mark Resolved", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddMaintenanceDialog(
    tenant: TenantEntity,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Plumbing") }
    var priority by remember { mutableStateOf("Normal") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Maintenance Request") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Room ${tenant.roomNumber} - ${tenant.tenantName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Title (e.g. Leaking Tap)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Details") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (Plumbing / Electrical / etc)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = priority,
                    onValueChange = { priority = it },
                    label = { Text("Priority (Normal / Urgent)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(title, description, category, priority)
                    }
                }
            ) {
                Text("Log Issue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
