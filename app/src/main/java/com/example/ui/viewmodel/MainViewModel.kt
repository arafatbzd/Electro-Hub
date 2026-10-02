package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Machine
import com.example.data.model.MachineCategory
import com.example.data.model.PartItem
import com.example.data.model.PlcIOItem
import com.example.data.repository.MachineRepository
import com.example.data.repository.SyncStatusState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class DetailTab(val title: String) {
    SPECS("Specs"),
    SCHEMATICS("Schematics (P1-24)"),
    PLC_IO("PLC I/O"),
    PARTS_BOM("Parts List"),
    TERMINALS("Terminals & Wire"),
    SHEET_SYNC("Cloud Sync")
}

data class DashboardUiState(
    val searchQuery: String = "",
    val selectedCategory: MachineCategory? = null,
    val showBookmarksOnly: Boolean = false,
    val selectedMachineId: String? = null,
    val selectedDetailTab: DetailTab = DetailTab.SPECS,
    val currentSchematicPage: Int = 1,
    val activeTracedWireId: String? = null,
    val isSheetSyncDialogOpen: Boolean = false,
    val isArchitectureGuideOpen: Boolean = false,
    val isAddEditMachineDialogOpen: Boolean = false,
    val machineBeingEdited: Machine? = null,
    val machineBeingDeleted: Machine? = null,
    val isAddEditPlcDialogOpen: Boolean = false,
    val plcBeingEdited: PlcIOItem? = null,
    val isAddEditPartDialogOpen: Boolean = false,
    val partBeingEdited: PartItem? = null,
    val isEditModeEnabled: Boolean = false,
    val isDarkTheme: Boolean = true,
    val enteredSheetId: String = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
    val enteredSupabaseUrl: String = "https://your-project.supabase.co",
    val enteredSupabaseKey: String = ""
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("electro_hub_prefs", android.content.Context.MODE_PRIVATE)

    val repository = MachineRepository(application, viewModelScope)

    val allMachines: StateFlow<List<Machine>> = repository.machines
    val syncStatus: StateFlow<SyncStatusState> = repository.syncStatus

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            isDarkTheme = prefs.getBoolean("is_dark_theme", true),
            isEditModeEnabled = prefs.getBoolean("is_edit_mode_enabled", false)
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Filtered machines based on search query, category chip, and bookmark filter
    val filteredMachines: StateFlow<List<Machine>> = combine(
        allMachines,
        _uiState
    ) { list, state ->
        list.filter { machine ->
            val matchesCategory = state.selectedCategory == null || machine.category == state.selectedCategory
            val matchesBookmark = !state.showBookmarksOnly || machine.isBookmarked
            val q = state.searchQuery.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                machine.name.lowercase().contains(q) ||
                machine.model.lowercase().contains(q) ||
                machine.manufacturer.lowercase().contains(q) ||
                machine.plcModel.lowercase().contains(q) ||
                machine.inverterModel.lowercase().contains(q) ||
                machine.plcIOList.any { it.address.lowercase().contains(q) || it.signalName.lowercase().contains(q) } ||
                machine.partsList.any { it.partNumber.lowercase().contains(q) || it.designation.lowercase().contains(q) }

            matchesCategory && matchesBookmark && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedMachine: StateFlow<Machine?> = combine(
        allMachines,
        _uiState
    ) { list, state ->
        if (state.selectedMachineId != null) {
            list.find { it.id == state.selectedMachineId }
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: MachineCategory?) {
        _uiState.update { it.copy(selectedCategory = if (it.selectedCategory == category) null else category) }
    }

    fun toggleBookmarksFilter() {
        _uiState.update { it.copy(showBookmarksOnly = !it.showBookmarksOnly) }
    }

    fun selectMachine(machineId: String?) {
        _uiState.update {
            it.copy(
                selectedMachineId = machineId,
                selectedDetailTab = DetailTab.SPECS,
                currentSchematicPage = 1,
                activeTracedWireId = null
            )
        }
    }

    fun selectDetailTab(tab: DetailTab) {
        _uiState.update { it.copy(selectedDetailTab = tab) }
    }

    fun setSchematicPage(page: Int) {
        _uiState.update { it.copy(currentSchematicPage = page, activeTracedWireId = null) }
    }

    fun selectTracedWire(wireId: String?) {
        _uiState.update {
            it.copy(activeTracedWireId = if (it.activeTracedWireId == wireId) null else wireId)
        }
    }

    fun toggleBookmark(machineId: String) {
        repository.toggleBookmark(machineId)
    }

    fun setSheetSyncDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isSheetSyncDialogOpen = visible) }
    }

    fun setArchitectureGuideVisible(visible: Boolean) {
        _uiState.update { it.copy(isArchitectureGuideOpen = visible) }
    }

    // Machine CRUD
    fun openAddMachineDialog() {
        _uiState.update {
            it.copy(isAddEditMachineDialogOpen = true, machineBeingEdited = null)
        }
    }

    fun openEditMachineDialog(machine: Machine) {
        _uiState.update {
            it.copy(isAddEditMachineDialogOpen = true, machineBeingEdited = machine)
        }
    }

    fun closeAddEditDialog() {
        _uiState.update {
            it.copy(isAddEditMachineDialogOpen = false, machineBeingEdited = null)
        }
    }

    fun openDeleteDialog(machine: Machine) {
        _uiState.update { it.copy(machineBeingDeleted = machine) }
    }

    fun closeDeleteDialog() {
        _uiState.update { it.copy(machineBeingDeleted = null) }
    }

    fun saveMachine(machine: Machine) {
        val state = _uiState.value
        repository.saveMachine(machine, state.enteredSupabaseUrl, state.enteredSupabaseKey)
        closeAddEditDialog()
        if (state.selectedMachineId == machine.id) {
            _uiState.update { it.copy(selectedMachineId = machine.id) }
        }
    }

    fun deleteConfirmedMachine() {
        val machineToDelete = _uiState.value.machineBeingDeleted ?: return
        val state = _uiState.value
        repository.deleteMachine(machineToDelete.id, state.enteredSupabaseUrl, state.enteredSupabaseKey)
        closeDeleteDialog()
        if (state.selectedMachineId == machineToDelete.id) {
            _uiState.update { it.copy(selectedMachineId = null) }
        }
    }

    // PLC I/O Customization
    fun openAddPlcDialog() {
        _uiState.update { it.copy(isAddEditPlcDialogOpen = true, plcBeingEdited = null) }
    }

    fun openEditPlcDialog(item: PlcIOItem) {
        _uiState.update { it.copy(isAddEditPlcDialogOpen = true, plcBeingEdited = item) }
    }

    fun closePlcDialog() {
        _uiState.update { it.copy(isAddEditPlcDialogOpen = false, plcBeingEdited = null) }
    }

    fun savePlcIO(item: PlcIOItem) {
        val machineId = _uiState.value.selectedMachineId ?: return
        val oldAddress = _uiState.value.plcBeingEdited?.address
        val state = _uiState.value
        repository.addOrUpdatePlcIO(machineId, item, oldAddress, state.enteredSupabaseUrl, state.enteredSupabaseKey)
        closePlcDialog()
    }

    fun deletePlcIO(address: String) {
        val machineId = _uiState.value.selectedMachineId ?: return
        val state = _uiState.value
        repository.deletePlcIO(machineId, address, state.enteredSupabaseUrl, state.enteredSupabaseKey)
    }

    // Parts List Customization
    fun openAddPartDialog() {
        _uiState.update { it.copy(isAddEditPartDialogOpen = true, partBeingEdited = null) }
    }

    fun openEditPartDialog(part: PartItem) {
        _uiState.update { it.copy(isAddEditPartDialogOpen = true, partBeingEdited = part) }
    }

    fun closePartDialog() {
        _uiState.update { it.copy(isAddEditPartDialogOpen = false, partBeingEdited = null) }
    }

    fun savePart(part: PartItem) {
        val machineId = _uiState.value.selectedMachineId ?: return
        val oldItemNo = _uiState.value.partBeingEdited?.itemNo
        val state = _uiState.value
        repository.addOrUpdatePart(machineId, part, oldItemNo, state.enteredSupabaseUrl, state.enteredSupabaseKey)
        closePartDialog()
    }

    fun deletePart(itemNo: Int) {
        val machineId = _uiState.value.selectedMachineId ?: return
        val state = _uiState.value
        repository.deletePart(machineId, itemNo, state.enteredSupabaseUrl, state.enteredSupabaseKey)
    }

    fun updateEnteredSheetId(sheetId: String) {
        _uiState.update { it.copy(enteredSheetId = sheetId) }
    }

    fun updateEnteredSupabaseUrl(url: String) {
        _uiState.update { it.copy(enteredSupabaseUrl = url) }
    }

    fun updateEnteredSupabaseKey(key: String) {
        _uiState.update { it.copy(enteredSupabaseKey = key) }
    }

    fun triggerGoogleSheetSync() {
        val sheetId = _uiState.value.enteredSheetId
        repository.syncFromGoogleSheet(sheetId)
    }

    fun triggerSupabaseSync() {
        val url = _uiState.value.enteredSupabaseUrl
        val key = _uiState.value.enteredSupabaseKey
        repository.syncFromSupabase(url, key)
    }

    fun pushAllMachinesToSupabase() {
        val url = _uiState.value.enteredSupabaseUrl
        val key = _uiState.value.enteredSupabaseKey
        repository.pushAllToSupabase(url, key)
    }

    fun getBulkSqlScript(): String {
        return repository.getBulkInsertSql()
    }

    fun resetToFactoryDefaults() {
        repository.resetToDefaults()
    }

    fun toggleEditMode(enabled: Boolean) {
        prefs.edit().putBoolean("is_edit_mode_enabled", enabled).apply()
        _uiState.update { it.copy(isEditModeEnabled = enabled) }
    }

    fun toggleTheme(isDark: Boolean) {
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
        _uiState.update { it.copy(isDarkTheme = isDark) }
    }
}
