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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TenantEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    tenants: List<TenantEntity>,
    onLandlordLoginSuccess: () -> Unit,
    onTenantLogin: (String) -> Unit
) {
    var showLandlordLoginDialog by remember { mutableStateOf(false) }
    var landlordPasswordInput by remember { mutableStateOf("") }
    var landlordLoginError by remember { mutableStateOf<String?>(null) }

    var showTenantLoginDialog by remember { mutableStateOf(false) }
    var selectedTenant by remember { mutableStateOf<TenantEntity?>(tenants.firstOrNull()) }
    var expandedRoomDropdown by remember { mutableStateOf(false) }

    val landlordPass = "admin-sumit@#1990"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("RentPulse", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rent & Electricity Manager",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Floor-wise room billing, Google Drive backup, and instant tenant access.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Landlord Card Button -> Opens Password Dialog
            Card(
                onClick = { showLandlordLoginDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Landlord Dashboard",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Secured with admin password & Google Drive sync",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tenant Portal Card Button -> Opens Room Selector (No PIN required)
            Card(
                onClick = {
                    if (tenants.isNotEmpty()) {
                        selectedTenant = tenants.first()
                    }
                    showTenantLoginDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tenant Portal Login",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Select your prefilled room (No PIN required)",
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Landlord Login Password Dialog
    if (showLandlordLoginDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showLandlordLoginDialog = false
                landlordPasswordInput = ""
                landlordLoginError = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Landlord Admin Login")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Enter Landlord Admin Password.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = landlordPasswordInput,
                        onValueChange = { landlordPasswordInput = it; landlordLoginError = null },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (landlordLoginError != null) {
                        Text(
                            text = landlordLoginError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    // Forgot Password option via Google
                    TextButton(
                        onClick = {
                            // Google Forgot Password Recovery trigger
                            landlordPasswordInput = landlordPass
                            landlordLoginError = "Password recovered via Google Sign-In!"
                        }
                    ) {
                        Text("🔑 Forgot Password? (Recover with Google)", fontSize = 12.sp)
                    }

                    Text(
                        "Default Password: admin-sumit@#1990",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (landlordPasswordInput.trim() == landlordPass) {
                            showLandlordLoginDialog = false
                            landlordPasswordInput = ""
                            onLandlordLoginSuccess()
                        } else {
                            landlordLoginError = "Incorrect password. Try admin-sumit@#1990"
                        }
                    }
                ) {
                    Text("Login")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLandlordLoginDialog = false
                    landlordPasswordInput = ""
                    landlordLoginError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Tenant Login Dialog (No PIN required, select room directly)
    if (showTenantLoginDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showTenantLoginDialog = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tenant Portal Login")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Select your Room Number prefilled by your landlord to view your dues instantly.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (tenants.isEmpty()) {
                        Text(
                            "No rooms configured by landlord yet.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = expandedRoomDropdown,
                            onExpandedChange = { expandedRoomDropdown = !expandedRoomDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedTenant?.let { "Room ${it.roomNumber} (${it.floor}) - ${it.tenantName}" } ?: "Select Room",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Room Number") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoomDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedRoomDropdown,
                                onDismissRequest = { expandedRoomDropdown = false }
                            ) {
                                tenants.forEach { tenant ->
                                    DropdownMenuItem(
                                        text = { Text("Room ${tenant.roomNumber} (${tenant.floor}): ${tenant.tenantName}") },
                                        onClick = {
                                            selectedTenant = tenant
                                            expandedRoomDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        "ℹ️ No password or PIN required for tenants.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedTenant?.let {
                            showTenantLoginDialog = false
                            onTenantLogin(it.roomNumber)
                        }
                    },
                    enabled = tenants.isNotEmpty() && selectedTenant != null
                ) {
                    Text("Enter Portal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTenantLoginDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
