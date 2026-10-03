package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.TenantEntity
import com.example.util.FormatUtils
import com.example.util.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantListScreen(
    tenants: List<TenantEntity>,
    language: com.example.util.AppLanguage = com.example.util.AppLanguage.HI,
    onBack: () -> Unit,
    onTenantClick: (TenantEntity) -> Unit,
    onDeleteTenant: (TenantEntity) -> Unit,
    onAddTenant: (
        roomNumber: String,
        floor: String,
        tenantName: String,
        phone: String,
        monthlyRent: Double,
        lastMeterReading: Double,
        accessCode: String,
        onComplete: (Boolean, String) -> Unit
    ) -> Unit
) {
    val context = LocalContext.current
    val str = com.example.util.AppStrings.get(language)
    val isHindi = language == com.example.util.AppLanguage.HI
    var showAddDialog by remember { mutableStateOf(false) }
    var tenantToDelete by remember { mutableStateOf<TenantEntity?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    val floors = if (isHindi)
        listOf("सभी मंजिलें", "Ground Floor", "First Floor", "Second Floor", "Third Floor", "Fourth Floor", "Penthouse")
    else
        listOf("All Floors", "Ground Floor", "First Floor", "Second Floor", "Third Floor", "Fourth Floor", "Penthouse")
    var selectedFloorFilter by remember { mutableStateOf(floors[0]) }

    val filteredTenants = tenants.filter { tenant ->
        val matchesQuery = tenant.roomNumber.contains(searchQuery, ignoreCase = true) ||
                tenant.tenantName.contains(searchQuery, ignoreCase = true) ||
                tenant.phone.contains(searchQuery)
        val matchesFloor = selectedFloorFilter == floors[0] || tenant.floor.equals(selectedFloorFilter, ignoreCase = true)
        matchesQuery && matchesFloor
    }

    // Add Tenant Form State
    val addFloors = listOf("Ground Floor", "First Floor", "Second Floor", "Third Floor", "Fourth Floor", "Penthouse")
    var selectedFloor by remember { mutableStateOf(addFloors[0]) }
    var expandedFloor by remember { mutableStateOf(false) }

    var roomNumber by remember { mutableStateOf("") }
    var tenantName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var monthlyRent by remember { mutableStateOf("") }
    var meterReading by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }
    var dialogError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Rooms & Tenants", fontWeight = FontWeight.Bold)
                        Text(
                            "${tenants.size} Rooms Registered",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    roomNumber = ""
                    tenantName = ""
                    phone = ""
                    monthlyRent = ""
                    meterReading = ""
                    accessCode = "1010"
                    dialogError = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tenant", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                placeholder = { Text("Search room number, tenant name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Floor Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                floors.forEach { floor ->
                    FilterChip(
                        selected = selectedFloorFilter == floor,
                        onClick = { selectedFloorFilter = floor },
                        label = { Text(floor) }
                    )
                }
            }

            if (filteredTenants.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isNotEmpty() || selectedFloorFilter != "All Floors")
                                "No tenants match the filter."
                            else
                                "No rooms or tenants registered yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Room & Tenant")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTenants, key = { it.id }) { tenant ->
                        TenantCard(
                            tenant = tenant,
                            onClick = { onTenantClick(tenant) },
                            onDelete = { tenantToDelete = tenant },
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tenant.phone}"))
                                context.startActivity(intent)
                            },
                            onWhatsApp = {
                                NotificationHelper.openWhatsApp(
                                    context,
                                    tenant.phone,
                                    "Hello ${tenant.tenantName} (Room ${tenant.roomNumber}), this is your landlord regarding RentPulse."
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Delete Tenant Confirmation Dialog
    tenantToDelete?.let { tenant ->
        AlertDialog(
            onDismissRequest = { tenantToDelete = null },
            title = { Text("Delete Room & Tenant?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to remove Room ${tenant.roomNumber} (${tenant.tenantName})?\n\n" +
                            "This will permanently delete this tenant, their bills, receipts, and maintenance history from the database."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTenant(tenant)
                        tenantToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Permanently", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { tenantToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Tenant Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                dialogError = null
            },
            title = { Text("Add Room & Tenant by Floor", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Floor Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedFloor,
                        onExpandedChange = { expandedFloor = !expandedFloor }
                    ) {
                        OutlinedTextField(
                            value = selectedFloor,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Floor") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFloor) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedFloor,
                            onDismissRequest = { expandedFloor = false }
                        ) {
                            addFloors.forEach { floorName ->
                                DropdownMenuItem(
                                    text = { Text(floorName) },
                                    onClick = {
                                        selectedFloor = floorName
                                        expandedFloor = false
                                        // Suggest prefix
                                        if (roomNumber.isBlank()) {
                                            roomNumber = when (floorName) {
                                                "Ground Floor" -> "G"
                                                "First Floor" -> "F"
                                                "Second Floor" -> "S"
                                                "Third Floor" -> "T"
                                                "Fourth Floor" -> "4"
                                                else -> "P"
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it; dialogError = null },
                        label = { Text("Room No / Code (e.g. G1, F2, 101)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tenantName,
                        onValueChange = { tenantName = it },
                        label = { Text("Tenant Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number (+91...)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = monthlyRent,
                        onValueChange = { monthlyRent = it },
                        label = { Text("Monthly Rent (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = meterReading,
                        onValueChange = { meterReading = it },
                        label = { Text("Initial Electricity Meter Reading (units)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accessCode,
                        onValueChange = { accessCode = it },
                        label = { Text("4-Digit Tenant Login PIN (e.g. 1010)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (dialogError != null) {
                        Text(dialogError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rent = monthlyRent.toDoubleOrNull() ?: 0.0
                        val meter = meterReading.toDoubleOrNull() ?: 0.0
                        if (roomNumber.isBlank() || tenantName.isBlank() || accessCode.isBlank()) {
                            dialogError = "Please fill all required fields"
                        } else {
                            onAddTenant(
                                roomNumber.trim(),
                                selectedFloor,
                                tenantName.trim(),
                                phone.trim(),
                                rent,
                                meter,
                                accessCode.trim()
                            ) { success, msg ->
                                if (success) {
                                    showAddDialog = false
                                    roomNumber = ""
                                    tenantName = ""
                                    phone = ""
                                    monthlyRent = ""
                                    meterReading = ""
                                    accessCode = ""
                                } else {
                                    dialogError = msg
                                }
                            }
                        }
                    }
                ) {
                    Text("Save Room & Tenant")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; dialogError = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TenantCard(
    tenant: TenantEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tenant.roomNumber,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tenant.tenantName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📍 ${tenant.floor}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VpnKey,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "PIN: ${tenant.accessCode} | Meter: ${tenant.lastMeterReading.toInt()}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = FormatUtils.formatCurrency(tenant.monthlyRent),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Rent/mo",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Call, WhatsApp, History, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Call Button
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Call Tenant",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    // WhatsApp Button
                    IconButton(
                        onClick = onWhatsApp,
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "WhatsApp Tenant",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "View History ➔",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Room & Tenant",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
    }
}
