package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.BillEntity
import java.net.URLEncoder

object NotificationHelper {

    fun generateBillMessage(bill: BillEntity): String {
        val electricityDetails = "• Electricity: ${bill.unitsConsumed.toInt()} units @ ₹${bill.electricityRate}/unit = ${FormatUtils.formatCurrency(bill.electricityAmount)}"
        val rentDetails = "• Room Rent: ${FormatUtils.formatCurrency(bill.rentAmount)}"
        val additional = if (bill.additionalCharges > 0) "\n• Other Charges (${bill.additionalChargesNote}): ${FormatUtils.formatCurrency(bill.additionalCharges)}" else ""
        
        return """
            🏠 *RENT & UTILITY BILL NOTICE* - RentPulse
            
            Hello *${bill.tenantName}* (Room No: *${bill.roomNumber}*),
            Your bill for *${bill.monthYear}* is ready:
            
            $rentDetails
            $electricityDetails$additional
            ──────────────────
            💰 *Total Due:* *${FormatUtils.formatCurrency(bill.remainingBalance)}*
            📅 *Due Date:* ${FormatUtils.formatDate(bill.dueDate)}
            
            💳 *UPI Payment Details:*
            • Name: *Prem Lata Meena*
            • UPI / Mobile: *9413631213*
            
            Login to Tenant Portal with Room *${bill.roomNumber}* anytime to view live balance & receipts.
            
            Thank you!
        """.trimIndent()
    }

    fun openUpiPayment(context: Context, amount: Double, note: String) {
        try {
            // Standard UPI intent URI for phone number 9413631213
            val upiUri = Uri.parse("upi://pay?pa=9413631213@paytm&pn=PREM%20LATA%20MEENA&am=$amount&cu=INR&tn=${URLEncoder.encode(note, "UTF-8")}")
            val intent = Intent(Intent.ACTION_VIEW, upiUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No UPI app found (Google Pay / PhonePe / Paytm)", Toast.LENGTH_LONG).show()
        }
    }

    fun openWhatsApp(context: Context, phone: String, message: String) {
        try {
            val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp not installed or unable to open", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSms(context: Context, phone: String, message: String) {
        try {
            val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
            val uri = Uri.parse("smsto:$cleanPhone")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to launch SMS app", Toast.LENGTH_SHORT).show()
        }
    }
}
