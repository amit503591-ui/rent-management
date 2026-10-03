package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TenantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TenantDao {
    @Query("SELECT * FROM tenants WHERE active = 1 ORDER BY roomNumber ASC")
    fun getAllActiveTenants(): Flow<List<TenantEntity>>

    @Query("SELECT * FROM tenants ORDER BY roomNumber ASC")
    fun getAllTenants(): Flow<List<TenantEntity>>

    @Query("SELECT * FROM tenants WHERE id = :id")
    fun getTenantById(id: Long): Flow<TenantEntity?>

    @Query("SELECT * FROM tenants WHERE id = :id")
    suspend fun getTenantByIdDirect(id: Long): TenantEntity?

    @Query("SELECT * FROM tenants WHERE roomNumber = :roomNumber LIMIT 1")
    suspend fun getTenantByRoom(roomNumber: String): TenantEntity?

    @Query("SELECT * FROM tenants WHERE roomNumber = :roomNumber AND accessCode = :code LIMIT 1")
    suspend fun authenticateTenant(roomNumber: String, code: String): TenantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenant(tenant: TenantEntity): Long

    @Update
    suspend fun updateTenant(tenant: TenantEntity)

    @Delete
    suspend fun deleteTenant(tenant: TenantEntity)

    @Query("DELETE FROM tenants WHERE id = :id")
    suspend fun deleteTenantById(id: Long)

    @Query("UPDATE tenants SET lastMeterReading = :newReading WHERE id = :tenantId")
    suspend fun updateLastMeterReading(tenantId: Long, newReading: Double)
}
