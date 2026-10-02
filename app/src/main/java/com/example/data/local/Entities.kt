package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "machines")
data class MachineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val model: String,
    val manufacturer: String,
    val category: String,
    val powerRating: String,
    val supplyVoltage: String,
    val controlVoltage: String,
    val fullLoadCurrent: String,
    val plcModel: String,
    val inverterModel: String,
    val description: String,
    val isPremium: Boolean,
    val isBookmarked: Boolean = false,
    val isCustomSynced: Boolean = false
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: String = "app_config",
    val isPremiumActive: Boolean = false,
    val googleSheetId: String = "1XyZ_FA494_Industrial_Schematics_Master",
    val lastSyncTime: Long = 0L,
    val licenseKey: String = ""
)

@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0L,
    val timestamp: Long,
    val sourceUrl: String,
    val status: String,
    val recordsSynced: Int,
    val message: String
)
