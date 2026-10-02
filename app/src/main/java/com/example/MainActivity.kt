package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.model.BillEntity
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CreateBillScreen
import com.example.ui.screens.LandlordDashboardScreen
import com.example.ui.screens.RecordPaymentDialog
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.screens.TenantListScreen
import com.example.ui.screens.TenantPortalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppRole
import com.example.ui.viewmodel.RentPulseViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: RentPulseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RentPulseApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun RentPulseApp(viewModel: RentPulseViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val appRole by viewModel.appRole.collectAsState()
    val currentTenant by viewModel.currentTenant.collectAsState()
    val tenants by viewModel.tenants.collectAsState()
    val bills by viewModel.allBills.collectAsState()
    val payments by viewModel.allPayments.collectAsState()
    val tenantBills by viewModel.tenantBills.collectAsState()
    val tenantPayments by viewModel.tenantPayments.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var currentScreen by remember { mutableStateOf("dashboard") } // "dashboard", "tenants", "create_bill", "analytics"
    var billToPay by remember { mutableStateOf<BillEntity?>(null) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    when (appRole) {
        AppRole.ROLE_SELECT -> {
            RoleSelectionScreen(
                tenants = tenants,
                onLandlordLoginSuccess = {
                    viewModel.setRole(AppRole.LANDLORD)
                    currentScreen = "dashboard"
                },
                onTenantLogin = { roomNumber ->
                    viewModel.loginTenantByRoom(roomNumber) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        AppRole.LANDLORD -> {
            BackHandler {
                if (currentScreen != "dashboard") {
                    currentScreen = "dashboard"
                } else {
                    viewModel.setRole(AppRole.ROLE_SELECT)
                }
            }

            when (currentScreen) {
                "tenants" -> {
                    TenantListScreen(
                        tenants = tenants,
                        onBack = { currentScreen = "dashboard" },
                        onAddTenant = { room, floor, name, phone, rent, meter, code, onComplete ->
                            viewModel.addTenant(room, floor, name, phone, rent, meter, code, onComplete)
                        }
                    )
                }
                "create_bill" -> {
                    CreateBillScreen(
                        tenants = tenants,
                        onBack = { currentScreen = "dashboard" },
                        onSubmitBill = { tenantId, month, rent, start, end, addCharges, addNotes, due, notes ->
                            viewModel.createBill(
                                tenantId = tenantId,
                                monthYear = month,
                                rentAmount = rent,
                                startMeterReading = start,
                                endMeterReading = end,
                                additionalCharges = addCharges,
                                additionalChargesNote = addNotes,
                                dueDate = due,
                                notes = notes
                            ) { success, msg, _ ->
                                viewModel.showToast(msg)
                                if (success) {
                                    currentScreen = "dashboard"
                                }
                            }
                        }
                    )
                }
                "analytics" -> {
                    AnalyticsScreen(
                        bills = bills,
                        payments = payments,
                        onBack = { currentScreen = "dashboard" },
                        onCollectPayment = { bill -> billToPay = bill }
                    )
                }
                else -> {
                    LandlordDashboardScreen(
                        tenants = tenants,
                        bills = bills,
                        payments = payments,
                        onNavigateCreateBill = { currentScreen = "create_bill" },
                        onNavigateTenants = { currentScreen = "tenants" },
                        onNavigateAnalytics = { currentScreen = "analytics" },
                        onRecordPayment = { bill -> billToPay = bill },
                        onBackupDrive = { viewModel.backupToGoogleDrive(context) },
                        onBroadcastReminders = { viewModel.broadcastReminders(context) },
                        onLogout = { viewModel.setRole(AppRole.ROLE_SELECT) }
                    )
                }
            }
        }
        AppRole.TENANT -> {
            BackHandler {
                viewModel.setRole(AppRole.ROLE_SELECT)
            }
            if (currentTenant != null) {
                TenantPortalScreen(
                    tenant = currentTenant!!,
                    bills = tenantBills,
                    payments = tenantPayments,
                    onLogout = { viewModel.setRole(AppRole.ROLE_SELECT) }
                )
            }
        }
    }

    // Record Payment Dialog
    billToPay?.let { bill ->
        RecordPaymentDialog(
            bill = bill,
            onDismiss = { billToPay = null },
            onSubmit = { amount, mode, ref ->
                viewModel.recordPayment(
                    billId = bill.id,
                    tenantId = bill.tenantId,
                    roomNumber = bill.roomNumber,
                    tenantName = bill.tenantName,
                    amount = amount,
                    mode = mode,
                    ref = ref
                ) { success, msg ->
                    viewModel.showToast(msg)
                    if (success) {
                        billToPay = null
                    }
                }
            }
        )
    }
}
