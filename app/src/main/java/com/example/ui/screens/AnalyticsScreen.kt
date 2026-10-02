package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BillEntity
import com.example.data.model.PaymentEntity
import com.example.ui.components.StatusBadge
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    bills: List<BillEntity>,
    payments: List<PaymentEntity>,
    onBack: () -> Unit,
    onCollectPayment: (BillEntity) -> Unit
) {
    val totalRentCollected = payments.sumOf { it.amountPaid }
    val totalBilled = bills.sumOf { it.totalAmount }
    val totalPending = bills.sumOf { it.remainingBalance }

    // Group collections by month
    val monthlyData = bills.groupBy { it.monthYear }.entries.take(5).toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Income Trends & Payment Status", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Summary Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Billed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(FormatUtils.formatCurrency(totalBilled), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Collected", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(FormatUtils.formatCurrency(totalRentCollected), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }

            // Bar Chart Visualization Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Monthly Income Trends (Rent & Electricity)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (monthlyData.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No monthly data available yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            // Custom Canvas Bar Chart
                            val primaryColor = MaterialTheme.colorScheme.primary
                            val tertiaryColor = MaterialTheme.colorScheme.tertiary
                            val maxAmount = monthlyData.maxOfOrNull { entry -> entry.value.sumOf { it.totalAmount } }?.coerceAtLeast(1000.0) ?: 10000.0

                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height - 30.dp.toPx()
                                val barWidth = (canvasWidth / (monthlyData.size * 2 + 1)).coerceIn(20.dp.toPx(), 60.dp.toPx())
                                val spacing = barWidth

                                monthlyData.forEachIndexed { index, entry ->
                                    val monthBills = entry.value
                                    val rentTotal = monthBills.sumOf { it.rentAmount }
                                    val elecTotal = monthBills.sumOf { it.electricityAmount }

                                    val rentHeight = (rentTotal / maxAmount * canvasHeight).toFloat()
                                    val elecHeight = (elecTotal / maxAmount * canvasHeight).toFloat()

                                    val startX = spacing + index * (barWidth * 2 + spacing)

                                    // Rent Bar
                                    drawRoundRect(
                                        color = primaryColor,
                                        topLeft = Offset(startX, canvasHeight - rentHeight),
                                        size = Size(barWidth, rentHeight),
                                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                    )

                                    // Electricity Bar
                                    drawRoundRect(
                                        color = tertiaryColor,
                                        topLeft = Offset(startX + barWidth + 4.dp.toPx(), canvasHeight - elecHeight),
                                        size = Size(barWidth, elecHeight),
                                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).background(primaryColor, CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rent", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).background(tertiaryColor, CircleShape))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Electricity", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // Section Header: Payment Status Tracker
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Room-wise Payment Status Tracker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Monitor whose payment is Paid vs Due and collect instantly",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (bills.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                        Text("No bills to track.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(bills) { bill ->
                    PaymentStatusTrackerItem(bill = bill, onCollectPayment = { onCollectPayment(bill) })
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PaymentStatusTrackerItem(
    bill: BillEntity,
    onCollectPayment: () -> Unit
) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (bill.isFullyPaid) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.errorContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (bill.isFullyPaid) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (bill.isFullyPaid) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Room ${bill.roomNumber} - ${bill.tenantName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${bill.monthYear} | Total: ${FormatUtils.formatCurrency(bill.totalAmount)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (bill.remainingBalance > 0 && bill.paidAmount > 0) {
                        Text(
                            text = "Due: ${FormatUtils.formatCurrency(bill.remainingBalance)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(status = bill.status)
                if (!bill.isFullyPaid) {
                    Spacer(modifier = Modifier.height(6.dp))
                    FilledTonalButton(
                        onClick = onCollectPayment,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Collect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
