package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.BillDao
import com.example.data.dao.PaymentDao
import com.example.data.dao.TenantDao
import com.example.data.model.BillEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.TenantEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TenantEntity::class, BillEntity::class, PaymentEntity::class],
    version = 2,
    exportSchema = false
)
abstract class RentPulseDatabase : RoomDatabase() {

    abstract fun tenantDao(): TenantDao
    abstract fun billDao(): BillDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: RentPulseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RentPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RentPulseDatabase::class.java,
                    "rentpulse_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(database: RentPulseDatabase) {
            val tenantDao = database.tenantDao()
            val billDao = database.billDao()
            val paymentDao = database.paymentDao()

            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L
            val oneMonthAgo = now - (30L * oneDayMs)

            // Seed Sample Tenants by Floor
            val tenant1 = TenantEntity(
                roomNumber = "G1",
                floor = "Ground Floor",
                tenantName = "Rahul Sharma",
                phone = "+919876543210",
                monthlyRent = 9500.0,
                lastMeterReading = 1420.0,
                accessCode = "1010",
                active = true,
                joinedDate = now - (90L * oneDayMs)
            )
            val tenant2 = TenantEntity(
                roomNumber = "F1",
                floor = "First Floor",
                tenantName = "Priya Verma",
                phone = "+919812345678",
                monthlyRent = 11000.0,
                lastMeterReading = 2150.0,
                accessCode = "1020",
                active = true,
                joinedDate = now - (60L * oneDayMs)
            )
            val tenant3 = TenantEntity(
                roomNumber = "F2",
                floor = "First Floor",
                tenantName = "Amit Patel",
                phone = "+919765432109",
                monthlyRent = 8500.0,
                lastMeterReading = 980.0,
                accessCode = "2010",
                active = true,
                joinedDate = now - (120L * oneDayMs)
            )

            val t1Id = tenantDao.insertTenant(tenant1)
            val t2Id = tenantDao.insertTenant(tenant2)
            val t3Id = tenantDao.insertTenant(tenant3)

            // Seed sample bills
            val bill1 = BillEntity(
                tenantId = t1Id,
                roomNumber = "G1",
                tenantName = "Rahul Sharma",
                tenantPhone = "+919876543210",
                monthYear = "October 2026",
                rentAmount = 9500.0,
                startMeterReading = 1340.0,
                endMeterReading = 1420.0,
                unitsConsumed = 80.0,
                electricityRate = 11.0,
                electricityAmount = 880.0,
                additionalCharges = 200.0,
                additionalChargesNote = "Water & Maintenance",
                totalAmount = 10580.0,
                paidAmount = 0.0,
                dueDate = now + (5L * oneDayMs),
                createdDate = now - (2L * oneDayMs),
                status = "PENDING",
                notes = "Due by 7th Oct"
            )
            billDao.insertBill(bill1)

            val bill2 = BillEntity(
                tenantId = t2Id,
                roomNumber = "F1",
                tenantName = "Priya Verma",
                tenantPhone = "+919812345678",
                monthYear = "October 2026",
                rentAmount = 11000.0,
                startMeterReading = 2040.0,
                endMeterReading = 2150.0,
                unitsConsumed = 110.0,
                electricityRate = 11.0,
                electricityAmount = 1210.0,
                additionalCharges = 0.0,
                totalAmount = 12210.0,
                paidAmount = 10000.0,
                dueDate = now + (7L * oneDayMs),
                createdDate = now - (3L * oneDayMs),
                status = "PARTIALLY_PAID",
                notes = "₹2,210 balance remaining"
            )
            val b2Id = billDao.insertBill(bill2)

            paymentDao.insertPayment(
                PaymentEntity(
                    billId = b2Id,
                    tenantId = t2Id,
                    roomNumber = "F1",
                    tenantName = "Priya Verma",
                    amountPaid = 10000.0,
                    paymentDate = now - (1L * oneDayMs),
                    paymentMode = "UPI",
                    transactionRef = "UPI982347101",
                    receiptNumber = "REC-F1-001"
                )
            )

            val bill3 = BillEntity(
                tenantId = t3Id,
                roomNumber = "F2",
                tenantName = "Amit Patel",
                tenantPhone = "+919765432109",
                monthYear = "September 2026",
                rentAmount = 8500.0,
                startMeterReading = 910.0,
                endMeterReading = 980.0,
                unitsConsumed = 70.0,
                electricityRate = 11.0,
                electricityAmount = 770.0,
                additionalCharges = 0.0,
                totalAmount = 9270.0,
                paidAmount = 9270.0,
                dueDate = oneMonthAgo + (5L * oneDayMs),
                createdDate = oneMonthAgo,
                status = "PAID",
                notes = "Cleared on time"
            )
            val b3Id = billDao.insertBill(bill3)

            paymentDao.insertPayment(
                PaymentEntity(
                    billId = b3Id,
                    tenantId = t3Id,
                    roomNumber = "F2",
                    tenantName = "Amit Patel",
                    amountPaid = 9270.0,
                    paymentDate = oneMonthAgo + (3L * oneDayMs),
                    paymentMode = "Bank Transfer",
                    transactionRef = "NEFT8891234",
                    receiptNumber = "REC-F2-001"
                )
            )
        }
    }
}
