package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.BillEntity
import java.net.URLEncoder

object NotificationHelper {

    fun generateBillMessage(bill: BillEntity, isHindi: Boolean = false): String {
        if (isHindi) {
            val electricityDetails = "• बिजली बिल: ${bill.unitsConsumed.toInt()} यूनिट्स @ ₹${bill.electricityRate}/यूनिट = ${FormatUtils.formatCurrency(bill.electricityAmount)}"
            val rentDetails = "• कमरे का किराया: ${FormatUtils.formatCurrency(bill.rentAmount)}"
            val additional = if (bill.additionalCharges > 0) "\n• अन्य शुल्क (${bill.additionalChargesNote}): ${FormatUtils.formatCurrency(bill.additionalCharges)}" else ""

            return """
                🏠 *किराया एवं बिजली बिल सूचना* - रेंटपल्स
                
                नमस्ते *${bill.tenantName}* (कमरा नं: *${bill.roomNumber}*),
                आपका माह *${bill.monthYear}* का बिल तैयार है:
                
                $rentDetails
                $electricityDetails$additional
                ──────────────────
                💰 *कुल देय राशि:* *${FormatUtils.formatCurrency(bill.remainingBalance)}*
                📅 *अंतिम तिथि:* ${FormatUtils.formatDate(bill.dueDate)}
                
                💳 *UPI भुगतान विवरण:*
                • नाम: *Prem Lata Meena*
                • UPI / मोबाइल: *9413631213*
                
                रेंटपल्स ऐप में रूम *${bill.roomNumber}* से लॉगिन करके रसीद और लाइव हिसाब देख सकते हैं।
                
                धन्यवाद!
            """.trimIndent()
        }

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

    fun generatePaymentReceiptMessage(tenantName: String, roomNumber: String, amountPaid: Double, receiptNumber: String, mode: String, isHindi: Boolean = false): String {
        if (isHindi) {
            return """
                🧾 *आधिकारिक भुगतान रसीद* - रेंटपल्स
                
                नमस्ते *${tenantName}* (कमरा नं: *${roomNumber}*),
                आपका किराया भुगतान सफलतापूर्वक प्राप्त हुआ!
                
                • *प्राप्त राशि:* ${FormatUtils.formatCurrency(amountPaid)}
                • *भुगतान माध्यम:* ${mode}
                • *रसीद संख्या:* ${receiptNumber}
                • *दिनांक:* ${FormatUtils.formatDate(System.currentTimeMillis())}
                
                समय पर भुगतान करने के लिए धन्यवाद!
            """.trimIndent()
        }

        return """
            🧾 *OFFICIAL PAYMENT RECEIPT* - RentPulse
            
            Hello *${tenantName}* (Room No: *${roomNumber}*),
            Your payment has been successfully recorded!
            
            • *Amount Paid:* ${FormatUtils.formatCurrency(amountPaid)}
            • *Payment Mode:* ${mode}
            • *Receipt Number:* ${receiptNumber}
            • *Date:* ${FormatUtils.formatDate(System.currentTimeMillis())}
            
            Thank you for your prompt payment!
        """.trimIndent()
    }

    fun openUpiPayment(context: Context, amount: Double, note: String) {
        try {
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
