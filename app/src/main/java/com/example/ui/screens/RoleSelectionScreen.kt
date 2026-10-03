package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TenantEntity
import com.example.util.AppLanguage
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    tenants: List<TenantEntity>,
    language: AppLanguage = AppLanguage.HI,
    onToggleLanguage: () -> Unit = {},
    onLandlordLoginSuccess: () -> Unit,
    onTenantLogin: (String) -> Unit
) {
    val str = AppStrings.get(language)
    var showLandlordLoginDialog by remember { mutableStateOf(false) }
    var landlordPasswordInput by remember { mutableStateOf("") }
    var landlordLoginError by remember { mutableStateOf<String?>(null) }

    var showTenantLoginDialog by remember { mutableStateOf(false) }
    var selectedTenant by remember { mutableStateOf<TenantEntity?>(tenants.firstOrNull()) }
    var expandedRoomDropdown by remember { mutableStateOf(false) }
    var enteredRoomPin by remember { mutableStateOf("") }
    var tenantLoginError by remember { mutableStateOf<String?>(null) }

    val landlordPass = "admin-sumit@#1990"
    val landlordPassSimple = "9413"

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
                        Text(str.appTitle, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                },
                actions = {
                    // Language Switcher Toggle
                    FilledTonalButton(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = "Language", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = str.switchToOtherLang,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
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
                text = if (language == AppLanguage.HI) "कमरा किराया एवं बिजली बिल प्रबंधन" else "Rent & Electricity Manager",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == AppLanguage.HI)
                    "मंजिल अनुसार कमरे, कैमरा मीटर रीडिंग, व्हाट्सएप बिल और आसान भुगतान।"
                else
                    "Floor-wise room billing, camera meter readings, and instant tenant access.",
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
                            text = str.landlordPortal,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = str.landlordLoginDesc,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                    Text("➔", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tenant Card Button -> Opens Tenant Login Dialog
            Card(
                onClick = { showTenantLoginDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
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
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = str.tenantPortal,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = str.tenantLoginDesc,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                    Text("➔", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Landlord Login PIN Dialog
    if (showLandlordLoginDialog) {
        AlertDialog(
            onDismissRequest = {
                showLandlordLoginDialog = false
                landlordPasswordInput = ""
                landlordLoginError = null
            },
            icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(str.landlordPortal, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (language == AppLanguage.HI)
                            "मकान मालिक सुरक्षा पिन दर्ज करें (डिफ़ॉल्ट: 9413):"
                        else
                            "Enter Landlord Security PIN (Default: 9413):",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = landlordPasswordInput,
                        onValueChange = {
                            landlordPasswordInput = it
                            landlordLoginError = null
                        },
                        label = { Text("PIN / Passcode") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (landlordLoginError != null) {
                        Text(
                            text = landlordLoginError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (landlordPasswordInput == landlordPass || landlordPasswordInput == landlordPassSimple) {
                            showLandlordLoginDialog = false
                            landlordPasswordInput = ""
                            landlordLoginError = null
                            onLandlordLoginSuccess()
                        } else {
                            landlordLoginError = if (language == AppLanguage.HI) "गलत पिन! कृपया 9413 दर्ज करें।" else "Incorrect PIN! Please enter 9413."
                        }
                    }
                ) {
                    Text(str.loginButton)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLandlordLoginDialog = false
                        landlordPasswordInput = ""
                        landlordLoginError = null
                    }
                ) {
                    Text(str.cancel)
                }
            }
        )
    }

    // Tenant Login Dialog
    if (showTenantLoginDialog) {
        AlertDialog(
            onDismissRequest = {
                showTenantLoginDialog = false
                enteredRoomPin = ""
                tenantLoginError = null
            },
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
            title = { Text(str.tenantPortal, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (tenants.isEmpty()) {
                        Text(
                            if (language == AppLanguage.HI)
                                "कोई कमरा पंजीकृत नहीं है। कृपया पहले मकान मालिक से संपर्क करें।"
                            else
                                "No rooms registered yet. Please have the landlord add your room first.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Room Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedRoomDropdown,
                            onExpandedChange = { expandedRoomDropdown = !expandedRoomDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedTenant?.let { "Room ${it.roomNumber} - ${it.tenantName}" } ?: str.selectRoom,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(str.selectRoom) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoomDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedRoomDropdown,
                                onDismissRequest = { expandedRoomDropdown = false }
                            ) {
                                tenants.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text("Room ${t.roomNumber} (${t.tenantName})") },
                                        onClick = {
                                            selectedTenant = t
                                            expandedRoomDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = enteredRoomPin,
                            onValueChange = {
                                enteredRoomPin = it
                                tenantLoginError = null
                            },
                            label = { Text(str.enterRoomPin) },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (tenantLoginError != null) {
                            Text(
                                text = tenantLoginError!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (tenants.isNotEmpty()) {
                    Button(
                        onClick = {
                            val tenant = selectedTenant
                            if (tenant == null) {
                                tenantLoginError = if (language == AppLanguage.HI) "कृपया कमरा चुनें" else "Please select a room"
                            } else if (enteredRoomPin.trim() != tenant.accessCode.trim()) {
                                tenantLoginError = if (language == AppLanguage.HI) "गलत 4-अंकीय पिन! (डिफ़ॉल्ट: ${tenant.accessCode})" else "Incorrect PIN! (Default: ${tenant.accessCode})"
                            } else {
                                showTenantLoginDialog = false
                                onTenantLogin(tenant.roomNumber)
                            }
                        }
                    ) {
                        Text(str.loginButton)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTenantLoginDialog = false
                        enteredRoomPin = ""
                        tenantLoginError = null
                    }
                ) {
                    Text(str.cancel)
                }
            }
        )
    }
}
