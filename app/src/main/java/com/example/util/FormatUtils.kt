package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun formatCurrency(amount: Double): String {
        return try {
            currencyFormat.format(amount).replace(".00", "")
        } catch (e: Exception) {
            "₹${amount.toInt()}"
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
