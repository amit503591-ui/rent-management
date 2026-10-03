package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BillEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TenantEntity
import com.example.ui.components.StatusBadge
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.FormatUtils
import com.example.util.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantPortalScreen(
    tenant: TenantEntity,
    bills: List<BillEntity>,
    payments: List<PaymentEntity>,
    language: AppLanguage = AppLanguage.HI,
    onToggleLanguage: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val str = AppStrings.get(language)
    val isHindi = language == AppLanguage.HI
    val totalOutstanding = bills.sumOf { it.remainingBalance }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tenant.roomNumber,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            if (isHindi) "कमरा ${tenant.roomNumber} (${tenant.floor}) पोर्टल" else "Room ${tenant.roomNumber} (${tenant.floor}) Portal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                },
                actions = {
                    // Language Switcher Toggle
                    FilledTonalButton(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = "Language", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = str.switchToOtherLang,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome & Real-time Balance Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (totalOutstanding > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "${str.welcomeTenant} ${tenant.tenantName}!",
                            fontSize = 14.sp,
                            color = if (totalOutstanding > 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (totalOutstanding > 0) str.currentDueBalance else (if (isHindi) "सभी बकाया चुकता हैं 🎉" else "All Dues Cleared 🎉"),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalOutstanding > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        if (totalOutstanding > 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = FormatUtils.formatCurrency(totalOutstanding),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            // Quick UPI Pay Button
                            FilledTonalButton(
                                onClick = {
                                    NotificationHelper.openUpiPayment(context, totalOutstanding, "Rent & Electricity Room ${tenant.roomNumber}")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (isHindi) "UPI (Paytm/GPay/PhonePe) से तुरंत भुगतान करें" else "Pay via UPI (Prem Lata Meena - 9413631213)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Contact Landlord WhatsApp Button
                        FilledTonalButton(
                            onClick = {
                                val landlordMsg = if (isHindi)
                                    "नमस्ते मकान मालिक जी, मैं कमरा नंबर ${tenant.roomNumber} (${tenant.floor}) से ${tenant.tenantName} बोल रहा हूँ। मुझे अपने बिल के बारे में बात करनी है।"
                                else
                                    "Hi Landlord, I am tenant ${tenant.tenantName} from Room ${tenant.roomNumber} (${tenant.floor}). I would like to discuss my bill/payment."
                                NotificationHelper.openWhatsApp(context, "+919413631213", landlordMsg)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isHindi) "व्हाट्सएप पर मकान मालिक से संपर्क करें" else "Contact Landlord via WhatsApp",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Section Header: Pending / Invoices
            item {
                Text(
                    text = str.billsAndReadings,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            if (bills.isEmpty()) {
                item {
                    Text(
                        if (isHindi) "कोई बिल जारी नहीं हुआ है।" else "No bills generated yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(bills) { bill ->
                    TenantBillCard(bill = bill, isHindi = isHindi)
                }
            }

            // Section Header: Payment Receipts
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = str.receipts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            if (payments.isEmpty()) {
                item {
                    Text(
                        if (isHindi) "कोई भुगतान रसीद उपलब्ध नहीं है।" else "No payment receipts yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(payments) { payment ->
                    TenantReceiptCard(payment = payment, isHindi = isHindi)
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun TenantBillCard(bill: BillEntity, isHindi: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(
                    text = bill.monthYear,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                StatusBadge(status = bill.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (isHindi) "कमरा किराया:" else "Room Rent:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(FormatUtils.formatCurrency(bill.rentAmount), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            (if (isHindi) "बिजली (" else "Electricity (") + "${bill.unitsConsumed.toInt()}" + (if (isHindi) " यूनिट्स @ ₹" else " units @ ₹") + "${bill.electricityRate}):",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(FormatUtils.formatCurrency(bill.electricityAmount), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                if (bill.additionalCharges > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text((if (isHindi) "अन्य (" else "Other (") + "${bill.additionalChargesNote}):", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(FormatUtils.formatCurrency(bill.additionalCharges), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (isHindi) "कुल बिल:" else "Total Bill Amount:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(FormatUtils.formatCurrency(bill.totalAmount), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                }
                if (bill.paidAmount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isHindi) "जमा की गई राशि:" else "Paid Amount:", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                        Text("- ${FormatUtils.formatCurrency(bill.paidAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
                if (bill.remainingBalance > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isHindi) "शेष बकाया:" else "Remaining Due:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(FormatUtils.formatCurrency(bill.remainingBalance), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (if (isHindi) "मीटर रीडिंग: शुरू (" else "Meter: Start (") + "${bill.startMeterReading.toInt()}" + (if (isHindi) ") ➔ अंतिम (" else ") ➔ End (") + "${bill.endMeterReading.toInt()})",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun TenantReceiptCard(payment: PaymentEntity, isHindi: Boolean = false) {
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
                        .size(36.dp)
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
                        text = (if (isHindi) "प्राप्त via " else "Paid via ") + payment.paymentMode,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = (if (isHindi) "रसीद: " else "Receipt: ") + payment.receiptNumber,
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
