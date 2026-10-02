package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TenantEntity
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantListScreen(
    tenants: List<TenantEntity>,
    onBack: () -> Unit,
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
    var showAddDialog by remember { mutableStateOf(false) }

    val floors = listOf("Ground Floor", "First Floor", "Second Floor", "Third Floor", "Fourth Floor", "Penthouse")
    var selectedFloor by remember { mutableStateOf(floors[0]) }
    var expandedFloor by remember { mutableStateOf(false) }

    var roomNumber by remember { mutableStateOf("") } // e.g. G1, F1, S1
    var tenantName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var monthlyRent by remember { mutableStateOf("") }
    var meterReading by remember { mutableStateOf("") }
    var accessCode by remember { mutableStateOf("") }
    var dialogError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rooms & Tenants Directory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Tenant", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        if (tenants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No tenants found. Tap + to add a room.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tenants) { tenant ->
                    TenantCard(tenant = tenant)
                }
            }
        }
    }

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
                            floors.forEach { floorName ->
                                DropdownMenuItem(
                                    text = { Text(floorName) },
                                    onClick = {
                                        selectedFloor = floorName
                                        expandedFloor = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it; dialogError = null },
                        label = { Text("Room No / Code (e.g. G1, F2, S1)") },
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
                        label = { Text("Initial Electricity Meter Reading") },
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
fun TenantCard(tenant: TenantEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = tenant.roomNumber,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
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
                    Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PIN: ${tenant.accessCode} | Meter: ${tenant.lastMeterReading.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    }
}
