package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.BillEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TenantEntity
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileOutputStream

@JsonClass(generateAdapter = true)
data class BackupData(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val tenants: List<TenantEntity>,
    val bills: List<BillEntity>,
    val payments: List<PaymentEntity>
)

object BackupHelper {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val adapter = moshi.adapter(BackupData::class.java)

    fun exportToJson(tenants: List<TenantEntity>, bills: List<BillEntity>, payments: List<PaymentEntity>): String {
        val backup = BackupData(
            tenants = tenants,
            bills = bills,
            payments = payments
        )
        return adapter.toJson(backup)
    }

    fun shareBackupToGoogleDrive(context: Context, tenants: List<TenantEntity>, bills: List<BillEntity>, payments: List<PaymentEntity>) {
        try {
            val jsonStr = exportToJson(tenants, bills, payments)
            val file = File(context.cacheDir, "RentPulse_Backup_${System.currentTimeMillis()}.json")
            FileOutputStream(file).use { it.write(jsonStr.toByteArray()) }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "RentPulse Database Backup")
                putExtra(Intent.EXTRA_TEXT, "Here is your RentPulse database backup for Google Drive sync / safe keeping.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Save Backup to Google Drive / Files")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
