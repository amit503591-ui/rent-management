package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MaintenanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_requests ORDER BY requestDate DESC")
    fun getAllMaintenanceRequests(): Flow<List<MaintenanceEntity>>

    @Query("SELECT * FROM maintenance_requests WHERE tenantId = :tenantId ORDER BY requestDate DESC")
    fun getMaintenanceForTenant(tenantId: Long): Flow<List<MaintenanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: MaintenanceEntity): Long

    @Query("UPDATE maintenance_requests SET status = :status, resolvedDate = :resolvedDate WHERE id = :requestId")
    suspend fun updateStatus(requestId: Long, status: String, resolvedDate: Long?)

    @Query("DELETE FROM maintenance_requests WHERE tenantId = :tenantId")
    suspend fun deleteMaintenanceForTenant(tenantId: Long)
}
