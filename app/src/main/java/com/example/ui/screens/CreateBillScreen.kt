package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TenantEntity
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBillScreen(
    tenants: List<TenantEntity>,
    onBack: () -> Unit,
    onSubmitBill: (
        tenantId: Long,
        monthYear: String,
        rentAmount: Double,
        startReading: Double,
        endReading: Double,
        additionalCharges: Double,
        additionalChargesNote: String,
        dueDate: Long,
        notes: String
    ) -> Unit
) {
    var selectedTenant by remember { mutableStateOf<TenantEntity?>(tenants.firstOrNull()) }
    var expandedTenant by remember { mutableStateOf(false) }

    var monthYear by remember { mutableStateOf("October 2026") }
    var rentAmount by remember { mutableStateOf(selectedTenant?.monthlyRent?.toString() ?: "10000") }
    var startReading by remember { mutableStateOf(selectedTenant?.lastMeterReading?.toString() ?: "0") }
    var endReading by remember { mutableStateOf("") }
    var additionalCharges by remember { mutableStateOf("0") }
    var additionalNotes by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Update rent & start reading when tenant changes
    fun onTenantSelected(tenant: TenantEntity) {
        selectedTenant = tenant
        rentAmount = tenant.monthlyRent.toString()
        startReading = tenant.lastMeterReading.toString()
    }

    val startVal = startReading.toDoubleOrNull() ?: 0.0
    val endVal = endReading.toDoubleOrNull() ?: startVal
    val units = (endVal - startVal).coerceAtLeast(0.0)
    val electricityAmt = units * 11.0
    val rentVal = rentAmount.toDoubleOrNull() ?: 0.0
    val addVal = additionalCharges.toDoubleOrNull() ?: 0.0
    val totalAmount = rentVal + electricityAmt + addVal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generate New Bill", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tenant Selector
            ExposedDropdownMenuBox(
                expanded = expandedTenant,
                onExpandedChange = { expandedTenant = !expandedTenant }
            ) {
                OutlinedTextField(
                    value = selectedTenant?.let { "Room ${it.roomNumber} - ${it.tenantName}" } ?: "Select Tenant",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Tenant & Room") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTenant) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTenant,
                    onDismissRequest = { expandedTenant = false }
                ) {
                    tenants.forEach { tenant ->
                        DropdownMenuItem(
                            text = { Text("Room ${tenant.roomNumber}: ${tenant.tenantName}") },
                            onClick = {
                                onTenantSelected(tenant)
                                expandedTenant = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = monthYear,
                onValueChange = { monthYear = it },
                label = { Text("Billing Month / Period (e.g. October 2026)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rentAmount,
                onValueChange = { rentAmount = it },
                label = { Text("Base Monthly Rent (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Electricity Calculation Section (₹11 per unit)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Electricity Bill Calculator (₹11 / Unit)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = startReading,
                            onValueChange = { startReading = it },
                            label = { Text("Start Reading") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endReading,
                            onValueChange = { endReading = it },
                            label = { Text("End Reading") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Live calculation breakdown
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Units Consumed:", fontSize = 13.sp)
                            Text("${units.toInt()} units", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rate per unit:", fontSize = 13.sp)
                            Text("₹11.00", fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Electricity Total:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(FormatUtils.formatCurrency(electricityAmt), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = additionalCharges,
                    onValueChange = { additionalCharges = it },
                    label = { Text("Other Charges (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = additionalNotes,
                    onValueChange = { additionalNotes = it },
                    label = { Text("Reason (e.g. Maintenance)") },
                    singleLine = true,
                    modifier = Modifier.weight(1.5f)
                )
            }

            // Total Amount Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Invoice Amount:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        FormatUtils.formatCurrency(totalAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (selectedTenant == null) {
                        errorMessage = "Please select a tenant"
                        return@Button
                    }
                    if (endVal < startVal) {
                        errorMessage = "End reading cannot be less than start reading"
                        return@Button
                    }
                    val dueTimestamp = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000L) // 7 days later
                    onSubmitBill(
                        selectedTenant!!.id,
                        monthYear.trim(),
                        rentVal,
                        startVal,
                        endVal,
                        addVal,
                        additionalNotes.trim(),
                        dueTimestamp,
                        notes.trim()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generate & Save Bill", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
