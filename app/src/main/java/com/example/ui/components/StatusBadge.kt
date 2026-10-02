package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldGreenLight
import com.example.ui.theme.RoseOverdue
import com.example.ui.theme.RoseOverdueLight
import com.example.ui.theme.AmberElectric
import com.example.ui.theme.AmberElectricLight

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "PAID" -> Triple(EmeraldGreenLight, EmeraldGreen, "PAID")
        "PARTIALLY_PAID" -> Triple(AmberElectricLight, AmberElectric, "PARTIAL")
        "OVERDUE" -> Triple(RoseOverdueLight, RoseOverdue, "OVERDUE")
        else -> Triple(AmberElectricLight, AmberElectric, "PENDING")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
