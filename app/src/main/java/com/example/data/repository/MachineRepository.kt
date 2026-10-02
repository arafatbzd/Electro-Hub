package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.Machine
import com.example.data.samples.DefaultMachineryData
import com.example.data.supabase.*
import com.example.data.sync.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class SyncStatusState {
    object Idle : SyncStatusState()
    object Syncing : SyncStatusState()
    data class Success(val message: String, val timestamp: Long) : SyncStatusState()
    data class Error(val errorMessage: String) : SyncStatusState()
}

class MachineRepository(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.machineDao()
    private val syncService = SheetsSyncService()
    private val supabaseService = SupabaseService()
    private var supabaseClient: SupabaseClient? = null

    // In-memory catalog (seeded with complete default industrial dataset + any synced machines)
    private val _machineMap = MutableStateFlow<Map<String, Machine>>(emptyMap())

    val machines: StateFlow<List<Machine>> = _machineMap.map { it.values.toList() }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatusState>(SyncStatusState.Idle)
    val syncStatus: StateFlow<SyncStatusState> = _syncStatus.asStateFlow()

    val syncLogs: Flow<List<SyncLogEntity>> = dao.getSyncLogs()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Load default rich dataset into memory
        val defaultList = DefaultMachineryData.getMachines()
        val initialMap = defaultList.associateBy { it.id }.toMutableMap()
        _machineMap.value = initialMap

        // Observe settings from Room
        scope.launch(Dispatchers.IO) {
            dao.getSettingsFlow().collect { settings ->
                if (settings != null) {
                    _isPremium.value = settings.isPremiumActive
                } else {
                    dao.saveSettings(AppSettingsEntity(isPremiumActive = false))
                }
            }
        }

        // Synchronize bookmark states from DB
        scope.launch(Dispatchers.IO) {
            dao.getAllMachines().collect { dbEntities ->
                val bookmarks = dbEntities.filter { it.isBookmarked }.map { it.id }.toSet()
                _machineMap.update { currentMap ->
                    currentMap.mapValues { (id, machine) ->
                        machine.copy(isBookmarked = bookmarks.contains(id))
                    }
                }
            }
        }
    }

    fun toggleBookmark(machineId: String) {
        val current = _machineMap.value[machineId] ?: return
        val newStatus = !current.isBookmarked
        _machineMap.update { map ->
            val updated = map.toMutableMap()
            updated[machineId] = current.copy(isBookmarked = newStatus)
            updated
        }

        scope.launch(Dispatchers.IO) {
            dao.insertMachine(
                MachineEntity(
                    id = current.id,
                    name = current.name,
                    model = current.model,
                    manufacturer = current.manufacturer,
                    category = current.category.name,
                    powerRating = current.powerRating,
                    supplyVoltage = current.supplyVoltage,
                    controlVoltage = current.controlVoltage,
                    fullLoadCurrent = current.fullLoadCurrent,
                    plcModel = current.plcModel,
                    inverterModel = current.inverterModel,
                    description = current.description,
                    isPremium = false,
                    isBookmarked = newStatus
                )
            )
        }
    }

    /**
     * Add or Update a custom machine (Local + Supabase auto-sync)
     */
    fun saveMachine(machine: Machine, supabaseUrl: String, supabaseKey: String) {
        _machineMap.update { map ->
            val updated = map.toMutableMap()
            updated[machine.id] = machine
            updated
        }

        scope.launch(Dispatchers.IO) {
            // Save to Room DB
            dao.insertMachine(
                MachineEntity(
                    id = machine.id,
                    name = machine.name,
                    model = machine.model,
                    manufacturer = machine.manufacturer,
                    category = machine.category.name,
                    powerRating = machine.powerRating,
                    supplyVoltage = machine.supplyVoltage,
                    controlVoltage = machine.controlVoltage,
                    fullLoadCurrent = machine.fullLoadCurrent,
                    plcModel = machine.plcModel,
                    inverterModel = machine.inverterModel,
                    description = machine.description,
                    isPremium = false,
                    isBookmarked = machine.isBookmarked
                )
            )

            // If Supabase credentials are configured, upsert into Supabase
            if (supabaseUrl.isNotBlank() && supabaseKey.isNotBlank()) {
                supabaseService.upsertMachineToSupabase(supabaseUrl, supabaseKey, machine)
            }
        }
    }

    /**
     * Delete a machine (Local + Supabase)
     */
    fun deleteMachine(machineId: String, supabaseUrl: String, supabaseKey: String) {
        _machineMap.update { map ->
            val updated = map.toMutableMap()
            updated.remove(machineId)
            updated
        }

        scope.launch(Dispatchers.IO) {
            dao.deleteMachineById(machineId)
            if (supabaseUrl.isNotBlank() && supabaseKey.isNotBlank()) {
                supabaseService.deleteMachineFromSupabase(supabaseUrl, supabaseKey, machineId)
            }
        }
    }

    /**
     * Add or Update a PLC I/O channel in a machine
     */
    fun addOrUpdatePlcIO(
        machineId: String,
        item: com.example.data.model.PlcIOItem,
        oldAddress: String? = null,
        supabaseUrl: String,
        supabaseKey: String
    ) {
        val current = _machineMap.value[machineId] ?: return
        val currentList = current.plcIOList.toMutableList()
        val index = if (oldAddress != null) {
            currentList.indexOfFirst { it.address.equals(oldAddress, true) }
        } else {
            currentList.indexOfFirst { it.address.equals(item.address, true) }
        }

        if (index >= 0) {
            currentList[index] = item
        } else {
            currentList.add(item)
        }

        val updatedMachine = current.copy(plcIOList = currentList)
        saveMachine(updatedMachine, supabaseUrl, supabaseKey)
    }

    /**
     * Delete a PLC I/O channel from a machine
     */
    fun deletePlcIO(
        machineId: String,
        address: String,
        supabaseUrl: String,
        supabaseKey: String
    ) {
        val current = _machineMap.value[machineId] ?: return
        val updatedList = current.plcIOList.filterNot { it.address.equals(address, true) }
        val updatedMachine = current.copy(plcIOList = updatedList)
        saveMachine(updatedMachine, supabaseUrl, supabaseKey)
    }

    /**
     * Add or Update a Part (BOM Item) in a machine
     */
    fun addOrUpdatePart(
        machineId: String,
        item: com.example.data.model.PartItem,
        oldItemNo: Int? = null,
        supabaseUrl: String,
        supabaseKey: String
    ) {
        val current = _machineMap.value[machineId] ?: return
        val currentList = current.partsList.toMutableList()
        val index = if (oldItemNo != null) {
            currentList.indexOfFirst { it.itemNo == oldItemNo }
        } else {
            currentList.indexOfFirst { it.itemNo == item.itemNo }
        }

        if (index >= 0) {
            currentList[index] = item
        } else {
            currentList.add(item)
        }

        val updatedMachine = current.copy(partsList = currentList)
        saveMachine(updatedMachine, supabaseUrl, supabaseKey)
    }

    /**
     * Delete a Part from a machine's BOM
     */
    fun deletePart(
        machineId: String,
        itemNo: Int,
        supabaseUrl: String,
        supabaseKey: String
    ) {
        val current = _machineMap.value[machineId] ?: return
        val updatedList = current.partsList.filterNot { it.itemNo == itemNo }
        val updatedMachine = current.copy(partsList = updatedList)
        saveMachine(updatedMachine, supabaseUrl, supabaseKey)
    }

    fun syncFromGoogleSheet(sheetIdOrUrl: String) {
        _syncStatus.value = SyncStatusState.Syncing
        scope.launch(Dispatchers.IO) {
            val result = syncService.syncFromGoogleSheet(sheetIdOrUrl)
            when (result) {
                is SyncResult.Success -> {
                    val syncedList = result.machines
                    _machineMap.update { current ->
                        val updated = current.toMutableMap()
                        syncedList.forEach { machine ->
                            updated[machine.id] = machine
                        }
                        updated
                    }

                    dao.insertSyncLog(
                        SyncLogEntity(
                            timestamp = System.currentTimeMillis(),
                            sourceUrl = sheetIdOrUrl,
                            status = "SUCCESS",
                            recordsSynced = syncedList.size,
                            message = result.message
                        )
                    )

                    val currentSettings = dao.getSettings() ?: AppSettingsEntity()
                    dao.saveSettings(
                        currentSettings.copy(
                            googleSheetId = sheetIdOrUrl,
                            lastSyncTime = System.currentTimeMillis()
                        )
                    )

                    _syncStatus.value = SyncStatusState.Success(
                        message = result.message,
                        timestamp = System.currentTimeMillis()
                    )
                }
                is SyncResult.Error -> {
                    dao.insertSyncLog(
                        SyncLogEntity(
                            timestamp = System.currentTimeMillis(),
                            sourceUrl = sheetIdOrUrl,
                            status = "FAILED",
                            recordsSynced = 0,
                            message = result.error
                        )
                    )
                    _syncStatus.value = SyncStatusState.Error(result.error)
                }
            }
        }
    }

    fun syncFromSupabase(projectUrl: String, anonKey: String) {
        _syncStatus.value = SyncStatusState.Syncing
        scope.launch(Dispatchers.IO) {
            // Initialize Supabase Client SDK instance
            val client = SupabaseClient.create(projectUrl, anonKey, scope)
            supabaseClient = client

            // Observe Auth State
            scope.launch {
                client.auth.authState.collect { state ->
                    _authState.value = state
                }
            }

            // Start Real-time WebSocket listener
            client.realtime.connectAndSubscribe("machines")
            scope.launch {
                client.realtime.events.collect { event ->
                    when (event) {
                        is RealtimeEvent.Insert, is RealtimeEvent.Update -> {
                            val refreshResult = supabaseService.fetchFromSupabase(projectUrl, anonKey)
                            if (refreshResult is SupabaseResult.Success) {
                                _machineMap.update { current ->
                                    val updated = current.toMutableMap()
                                    refreshResult.machines.forEach { m -> updated[m.id] = m }
                                    updated
                                }
                                _syncStatus.value = SyncStatusState.Success(
                                    "⚡ Realtime change synced from Supabase!",
                                    System.currentTimeMillis()
                                )
                            }
                        }
                        is RealtimeEvent.Delete -> {
                            val refreshResult = supabaseService.fetchFromSupabase(projectUrl, anonKey)
                            if (refreshResult is SupabaseResult.Success) {
                                _machineMap.value = refreshResult.machines.associateBy { it.id }
                                _syncStatus.value = SyncStatusState.Success(
                                    "⚡ Realtime record deleted from Supabase!",
                                    System.currentTimeMillis()
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }

            val result = supabaseService.fetchFromSupabase(projectUrl, anonKey)
            when (result) {
                is SupabaseResult.Success -> {
                    val syncedList = result.machines
                    _machineMap.update { current ->
                        val updated = current.toMutableMap()
                        syncedList.forEach { machine ->
                            updated[machine.id] = machine
                        }
                        updated
                    }

                    dao.insertSyncLog(
                        SyncLogEntity(
                            timestamp = System.currentTimeMillis(),
                            sourceUrl = projectUrl,
                            status = "SUCCESS_SUPABASE",
                            recordsSynced = syncedList.size,
                            message = result.message
                        )
                    )

                    _syncStatus.value = SyncStatusState.Success(
                        message = result.message,
                        timestamp = System.currentTimeMillis()
                    )
                }
                is SupabaseResult.Error -> {
                    dao.insertSyncLog(
                        SyncLogEntity(
                            timestamp = System.currentTimeMillis(),
                            sourceUrl = projectUrl,
                            status = "FAILED_SUPABASE",
                            recordsSynced = 0,
                            message = result.error
                        )
                    )
                    _syncStatus.value = SyncStatusState.Error(result.error)
                }
            }
        }
    }

    /**
     * 1-Click Bulk Push all local machines to Supabase table
     */
    fun pushAllToSupabase(projectUrl: String, anonKey: String) {
        _syncStatus.value = SyncStatusState.Syncing
        scope.launch(Dispatchers.IO) {
            val currentList = _machineMap.value.values.toList()
            val result = supabaseService.pushAllMachinesToSupabase(projectUrl, anonKey, currentList)
            when (result) {
                is SupabaseResult.Success -> {
                    _syncStatus.value = SyncStatusState.Success(
                        message = result.message,
                        timestamp = System.currentTimeMillis()
                    )
                }
                is SupabaseResult.Error -> {
                    _syncStatus.value = SyncStatusState.Error(result.error)
                }
            }
        }
    }

    fun getBulkInsertSql(): String {
        return supabaseService.generateBulkInsertSql(_machineMap.value.values.toList())
    }

    suspend fun signInSupabase(email: String, password: String): Result<SupabaseSession> {
        val client = supabaseClient ?: return Result.failure(Exception("Supabase client not initialized. Connect with Project URL first."))
        return client.auth.signInWithEmail(email, password)
    }

    suspend fun signUpSupabase(email: String, password: String): Result<SupabaseSession> {
        val client = supabaseClient ?: return Result.failure(Exception("Supabase client not initialized. Connect with Project URL first."))
        return client.auth.signUpWithEmail(email, password)
    }

    suspend fun signOutSupabase(): Result<Unit> {
        return supabaseClient?.auth?.signOut() ?: Result.success(Unit)
    }

    fun resetToDefaults() {
        val defaultList = DefaultMachineryData.getMachines()
        _machineMap.value = defaultList.associateBy { it.id }
        _syncStatus.value = SyncStatusState.Idle
    }
}
